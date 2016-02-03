package io.kiah.common.pool.utils;

import io.kiah.common.pool.conf.ServerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * Represents the hash ring for application servers.
 * <p/>
 * 算法实现参考：
 * https://github.com/gwhalin/Memcached-Java-Client/blob/master/src/com/meetup/
 * memcached/SockIOPool.java#L139
 * <p/>
 * Created by Kiah on 1/23/16.
 */
public class ConsistentHashRing {
	private static final Logger log = LoggerFactory.getLogger(ConsistentHashRing.class);

	// avoid recurring construction
	private static ThreadLocal<MessageDigest> MD5 = new ThreadLocal<MessageDigest>() {
		@Override
		protected MessageDigest initialValue() {
			try {
				return MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				log.error("no md5 algorithm found");
				throw new IllegalStateException("no md5 algorythm found");
			}
		}
	};

	/**
	 * Server 分布因子
	 */
	private final int factor = 32;

	/**
	 * 一致性HASH的Server 桶
	 */
	private final TreeMap<Long, ServerInfo> consistentBuckets = new TreeMap<Long, ServerInfo>();

	public ConsistentHashRing(final List<ServerInfo> servers) {
		populateConsistentBuckets(servers);
	}

	private void populateConsistentBuckets(List<ServerInfo> serverList) {
		if (serverList == null || serverList.isEmpty())
			return;

		for (ServerInfo server : serverList) {
			addServer(server);
		}
	}

	public void addServer(ServerInfo server) {

		MessageDigest md5 = MD5.get();

		for (long j = 0; j < factor; j++) {
			/*
			 * 每个server有factor个hash值 使用server的(host name + port +编号)来计算hash值
			 * 比如server - "172.16.16.41:8544"就有factor个数据用来生成hash值：
			 * 172.16.16.41:8544-1, 172.16.16.41:8544-2, ...,
			 * 172.16.16.41:8544-factor
			 */
			byte[] d = md5.digest((server.getHost() + "-" + String.valueOf(j)).getBytes());

			/*
			 * 每个hash值生成4个虚拟节点 每个server一共分配4*factor个虚拟节点
			 */
			for (int h = 0; h < 4; h++) {
				long key = ((long) (d[3 + h * 4] & 0xFF) << 24) | ((long) (d[2 + h * 4] & 0xFF) << 16)
						| ((long) (d[1 + h * 4] & 0xFF) << 8) | ((long) (d[0 + h * 4] & 0xFF));

				// 在环上保存节点
				consistentBuckets.put(key, server);
			}
		}
	}

	/**
	 * Remove a server from hash ring.
	 */
	public void removeServer(final ServerInfo server) {

		Set<Long> keySet = new HashSet<Long>(factor * 4);

		for (Entry<Long, ServerInfo> entry : consistentBuckets.entrySet()) {
			if (entry.getValue().equals(server)) {
				keySet.add(entry.getKey());
			}
		}

		for (Long key : keySet) {
			consistentBuckets.remove(key);
		}
	}

	/**
	 * Returns a server from hash ring based on the request token.
	 *
	 * @param requestIdentifier
	 *            A string represents the request.
	 */
	public ServerInfo getServingServer(final String requestIdentifier) {

		long hashCode = computeHashCode(requestIdentifier);
		SortedMap<Long, ServerInfo> tmap = consistentBuckets.tailMap(hashCode);

		Long bucket = null;
		if (tmap.isEmpty() && !consistentBuckets.isEmpty()) {
			bucket = consistentBuckets.firstKey();
		} else if (size() > 0) {
			// tailMap的第一个虚拟节点对应的即是目标server
			bucket = tmap.firstKey();
		}

		if (bucket == null)
			return null;

		return consistentBuckets.get(bucket);
	}

	/**
	 * Returns total virtual nodes in the ring.
	 */
	public int size() {
		return consistentBuckets == null ? 0 : consistentBuckets.size();
	}

	/**
	 * Remove all virtual nodes in the ring.
	 */
	public void clear() {
		if (size() > 0) {
			consistentBuckets.clear();
		}
	}

	/**
	 * Internal private hashing method.
	 * <p/>
	 * MD5 based hash algorithm for use in the consistent hashing approach.
	 */
	private static long computeHashCode(String key) {
		MessageDigest md5 = MD5.get();
		md5.reset();
		md5.update(key.getBytes());
		byte[] bKey = md5.digest();
		// 取MD5值的低32位作为key的hash值
		return ((long) (bKey[3] & 0xFF) << 24) | ((long) (bKey[2] & 0xFF) << 16) | ((long) (bKey[1] & 0xFF) << 8)
				| (long) (bKey[0] & 0xFF);
	}
}
