package io.kiah.common.pool.server.ping;

import io.kiah.common.pool.conf.model.Cluster;
import io.kiah.common.pool.conf.LoadBalanceMethod;
import io.kiah.common.pool.conf.ServerInfo;
import io.kiah.common.pool.utils.ConsistentHashRing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Kiah on 1/23/16.
 */
public abstract class ServerHealthManager {

	private static final Logger log = LoggerFactory.getLogger(ServerHealthManager.class);

	private String bizKey;
	private final String clusterName;
	private final LoadBalanceMethod lbMethod;
	private Date lastUpdate;
	private final List<ServerInfo> alives;
	private final List<ServerInfo> deads;
	private final ConsistentHashRing serverRing;
	private int lastServingServerIndex = 0;

	/**
	 * Ping Timeout
	 */
	private final int pingTimeout;

	/**
	 * Query Timeout
	 */
	private final int queryTimeout;

	/**
	 * Max Connection number
	 */
	private int maxConnectionsPerHost;

	protected ServerHealthManager(final String bizKey, final Cluster cluster) {
		if (cluster == null)
			throw new RuntimeException("cluster is null.");

		if (cluster.serverList == null || cluster.serverList.size() == 0)
			throw new RuntimeException(cluster.toString() + " do not have any server configured.");

		this.bizKey = bizKey;
		this.clusterName = cluster.name;
		this.lbMethod = cluster.lbMethod;
		this.pingTimeout = cluster.pingTimeout;
		this.queryTimeout = cluster.queryTimeout;
		this.maxConnectionsPerHost = cluster.maxConnectionsPerHost;
		this.alives = new LinkedList<ServerInfo>(cluster.serverList);
		this.serverRing = new ConsistentHashRing(cluster.serverList);
		this.deads = new LinkedList<ServerInfo>();
		this.lastUpdate = new Date();

		// Watch this connection manager
		ApplicationStatusWatcher.startWatch(this);
	}

	/**
	 * Destroy this connection manager.
	 */
	public void destroy() {
		// Remove from monitor list.
		ApplicationStatusWatcher.stopWatch(bizKey);
	}

	public String getBizKey() {
		return bizKey;
	}

	public String getClusterName() {
		return clusterName;
	}

	public LoadBalanceMethod getLBMethod() {
		return lbMethod;
	}

	/**
	 * Returns last health check date.
	 */
	public Date getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * Returns the timeout interval of normal query. 15000ms by default.
	 */
	public int getQueryTimeout() {
		if (queryTimeout <= 0)
			return 15000;

		return queryTimeout;
	}

	/**
	 * Returns the timeout interval of normal health check.. 10000ms by default.
	 */
	public int getPingTimeout() {
		if (pingTimeout <= 0)
			return 10000;

		return pingTimeout;
	}

	public int getMaxConnectionsPerHost() {
		if (maxConnectionsPerHost <= 0) {
			return 70;
		}

		return maxConnectionsPerHost;
	}

	/**
	 * Returns alive server list.
	 */
	public final List<ServerInfo> getAliveServers() {
		synchronized (ServerHealthManager.class) {
			return alives;
		}
	}

	/**
	 * Returns dead server list.
	 */
	public final List<ServerInfo> getDeadServers() {
		synchronized (ServerHealthManager.class) {
			return deads;
		}
	}

	/**
	 * @param requestIdentifier
	 *            A string represent the request.
	 * @return Server address
	 */
	public final ServerInfo getServingServer(final String requestIdentifier) {

		if (alives == null || alives.size() == 0)
			return null;

		ServerInfo result = null;

		synchronized (ServerHealthManager.class) {

			if (alives.size() > 0) {

				if (lbMethod == LoadBalanceMethod.Sequential) {
					result = alives.get(0);
				} else if (lbMethod == LoadBalanceMethod.RoundRobin) {
					if (lastServingServerIndex >= alives.size() - 1) {
						lastServingServerIndex = 0;
					} else {
						lastServingServerIndex++;
					}
					result = alives.get(lastServingServerIndex);
				} else if (lbMethod == LoadBalanceMethod.Hash) {
					result = serverRing.getServingServer(requestIdentifier);
				} else {
					result = alives.get(0);
					log.warn(String.format("No load balancing method was set for %s-%s, use sequential by default."));
				}
			}
		}

		return result;
	}

	/**
	 * Issues a ping request to check if the server is alive
	 *
	 * @param server
	 *            The server info.
	 * @param available
	 *            Current status of the server.
	 * @return True if response OK in timeout period.
	 */
	protected abstract boolean pingServer(final ServerInfo server, final boolean available);

	/**
	 *
	 */
	@Override
	public String toString() {
		return String.format("connection manager of [%s]", bizKey);
	}

	/**
	 * Executes health check of all servers in cluster.
	 */
	public final void check() {

		for (int i = 0; i < alives.size();) {
			if (!pingServer(alives.get(i), true)) {
				log.warn(alives.get(i).toString() + " seems dead.");

				synchronized (ServerHealthManager.class) {
					try {
						ServerInfo deadServer = alives.remove(i);
						serverRing.removeServer(deadServer);
						deads.add(deadServer);
					} catch (Exception e) {
						log.error("Error on removing dead server from alive list", e);
						i++;
					}
				}
				continue;
			}

			i++;
		}

		for (int i = 0; i < deads.size();) {
			if (pingServer(deads.get(i), false)) {
				log.warn(deads.get(i).toString() + " back to life.");

				synchronized (ServerHealthManager.class) {
					try {
						ServerInfo aliveServer = deads.remove(i);
						alives.add(aliveServer);
						serverRing.addServer(aliveServer);
					} catch (Exception e) {
						log.error("Error on removing alive server from dead list.", e);
						i++;
					}
				}
				continue;
			}

			i++;
		}

		this.lastUpdate = new Date();
	}
}
