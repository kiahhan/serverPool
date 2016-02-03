package io.kiah.common.pool.server;

import io.kiah.common.pool.conf.ConfigurationManager;
import io.kiah.common.pool.conf.ServerInfo;
import io.kiah.common.pool.conf.model.Cluster;
import io.kiah.common.pool.conf.model.ServingConfiguration;
import io.kiah.common.pool.conf.model.SolrServerConfiguration;
import io.kiah.common.pool.server.ping.ServerHealthManager;
import io.kiah.common.pool.server.ping.SolrConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Kiah on 1/23/16.
 */
public class SolrConnectionPool {

	private static final Logger log = LoggerFactory.getLogger(SolrConnectionPool.class);

	/**
	 * Cache server pool objects.
	 */
	private static final Map<String, SolrConnectionPool> POOLS = new ConcurrentHashMap<String, SolrConnectionPool>();

	private final SolrServerConfiguration conf;

	private final ServerHealthManager healthManager;

	private SolrConnectionPool(final String bizKey) {
		conf = getApplicationConfiguration();
		healthManager = createHealthManager(bizKey, conf.getServingCluster(bizKey));
	}

	public static void init() {
		for (ServingConfiguration sc : getApplicationConfiguration().servings) {
			new SolrConnectionPool(sc.bizKey);
		}
	}

	private ServerHealthManager createHealthManager(String bizKey, final Cluster cluster) {
		return new SolrConnectionManager(bizKey, cluster);
	}

	/**
	 * Returns application configuration.
	 *
	 * @return
	 */
	private static SolrServerConfiguration getApplicationConfiguration() {
		return ConfigurationManager.getSolrServerConfiguration();
	}

	private TargetServerInfo getTargetServer(final String requestIdentifier) {

		ServerInfo server = healthManager.getServingServer(requestIdentifier);

		if (server == null) {
			log.error("No alive server for cluster - " + healthManager.getClusterName());
			throw new RuntimeException("No alive server for cluster - " + healthManager.getClusterName());
		}

		return TargetServerInfo.createServer(server.getUrl(), healthManager.getQueryTimeout(),
				healthManager.getMaxConnectionsPerHost());
	}

	private List<ServerInfo> getAliveServers() {
		return healthManager.getAliveServers();
	}

	public void shutdown() {
		healthManager.destroy();
	}

	public static List<ServerInfo> getAliveServers(final String bizKey) {

		SolrConnectionPool pool = retrievePool(bizKey);

		if (pool == null) {
			synchronized (SolrConnectionPool.class) {
				pool = new SolrConnectionPool(bizKey);
				cachePool(bizKey, pool);
			}
		}

		return pool.getAliveServers();
	}

	public static TargetServerInfo getTargetServer(final String bizKey, final String requestIdentifier) {

		SolrConnectionPool pool = retrievePool(bizKey);

		if (pool == null) {
			synchronized (SolrConnectionPool.class) {
				pool = new SolrConnectionPool(bizKey);
				cachePool(bizKey, pool);
			}
		}

		return pool.getTargetServer(requestIdentifier);
	}

	/**
	 * Gets pool from cache.
	 */
	protected static SolrConnectionPool retrievePool(final String bizKey) {
		return POOLS.get(bizKey);
	}

	/**
	 * Cache a pool.
	 */
	protected static SolrConnectionPool cachePool(final String bizKey, final SolrConnectionPool pool) {
		return POOLS.put(bizKey, pool);
	}

	/**
	 * Clear pools. It MUST be invoked when application server configuration
	 * changed.
	 * 
	 * @param servings
	 */
	public static synchronized void clearPools(final List<ServingConfiguration> servings) {

		for (ServingConfiguration sc : servings) {
			SolrConnectionPool pool = retrievePool(sc.bizKey);

			if (pool == null)
				continue;

			// Shutdown the pool to release resource.
			pool.shutdown();

			// Delete the pool from cache.
			POOLS.remove(sc.bizKey);
			cachePool(sc.bizKey, new SolrConnectionPool(sc.bizKey));

		}
	}

}
