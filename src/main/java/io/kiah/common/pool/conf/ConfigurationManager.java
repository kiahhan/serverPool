package io.kiah.common.pool.conf;

import io.kiah.common.pool.conf.model.SolrServerConfiguration;
import io.kiah.common.pool.server.SolrConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Kiah on 1/23/16.
 */
public class ConfigurationManager {

	private static final Logger log = LoggerFactory.getLogger(ConfigurationManager.class);
	private static final ConfigResource resource = new ConfigResource();

	/**
	 * Cannot be instantiation.
	 */
	private ConfigurationManager() {
	}

	public static void reload(String fileName) {
		resource.reload(fileName);
		SolrConnectionPool.clearPools(ConfigurationManager.getSolrServerConfiguration().servings);
	}

	/**
	 * Returns solr server configuration info.
	 */
	public static SolrServerConfiguration getSolrServerConfiguration() {
		SolrServerConfiguration solrServerConfiguration = resource.get(SolrServerConfiguration.FILE_NAME);
		if (solrServerConfiguration == null) {
			log.error("Failed to load solrServer configuration.");
		}
		return solrServerConfiguration;
	}
}
