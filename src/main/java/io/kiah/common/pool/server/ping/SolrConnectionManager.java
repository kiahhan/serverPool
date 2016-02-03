package io.kiah.common.pool.server.ping;

import io.kiah.common.pool.conf.ServerInfo;
import io.kiah.common.pool.conf.model.Cluster;
import io.kiah.common.pool.utils.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kiah on 1/23/16.
 */
public class SolrConnectionManager extends ServerHealthManager {

	private static final Logger log = LoggerFactory.getLogger(SolrConnectionManager.class);

	private static final Map<String, HttpSolrClient> solrServerMapping = new HashMap<String, HttpSolrClient>();

	public SolrConnectionManager(final String bizKey, final Cluster cluster) {
		super(bizKey, cluster);
	}

	@Override
	protected boolean pingServer(ServerInfo server, boolean available) {

		if (server == null || StringUtils.isNullOrEmpty(server.getUrl())) {
			log.error("Cannot issue a ping request of null or empty server url.");
			return false;
		}
		try {
			SolrClient solr = getSolrServer(server.getUrl());
			solr.ping();
		} catch (IOException e) {
			log.error("IOError on ping " + server.getUrl());
			return false;
		} catch (SolrServerException e) {
			if (available) {
				log.error("Solr server error on ping " + server.getUrl());
				return false;
			}
		} catch (SolrException e) {
			log.error("SolrException on ping " + server.getUrl());
			return false;
		}
		return true;
	}

	private HttpSolrClient getSolrServer(String url) {
		HttpSolrClient solrServer = solrServerMapping.get(url);
		if (solrServer == null) {
			solrServer = createSolrServer(url);
		}
		return solrServer;
	}

	private HttpSolrClient createSolrServer(String url) {
		synchronized (solrServerMapping) {
			HttpSolrClient server = solrServerMapping.get(url);
			if (server == null) {
				server = new HttpSolrClient(url);
				server.setConnectionTimeout(getQueryTimeout());
				server.setSoTimeout(getPingTimeout());
				server.setDefaultMaxConnectionsPerHost(getMaxConnectionsPerHost());
				server.setMaxTotalConnections(getMaxConnectionsPerHost());
				solrServerMapping.put(url, server);
			}
			return server;
		}
	}
}
