package io.kiah.common.pool.server;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.SolrParams;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kiah on 1/24/16.
 */
public class SolrServer extends ApplicationServer<QueryResponse> {

	public String bizKey;
	private SolrParams cmd;

	private static final Map<String, HttpSolrClient> solrServerMapping = new HashMap<String, HttpSolrClient>();

	public SolrServer(final SolrParams cmd, final String bizKey, final String requestIdentifier)
			throws URISyntaxException {
		super(SolrConnectionPool.getTargetServer(bizKey, requestIdentifier));
		this.cmd = cmd;
		this.bizKey = bizKey;
	}

	@Override
	public QueryResponse query() throws Exception {

		QueryResponse res = null;
		try {
			SolrClient server = getSolrServer();
			res = server.query(cmd, SolrRequest.METHOD.GET);
		} catch (Exception e) {
			throw e;
		}

		return res;
	}

	private HttpSolrClient getSolrServer() {
		HttpSolrClient solrServer = solrServerMapping.get(getUrl());
		if (solrServer == null) {
			solrServer = createSolrServer();
		}
		return solrServer;
	}

	private HttpSolrClient createSolrServer() {
		synchronized (solrServerMapping) {
			HttpSolrClient server = solrServerMapping.get(getUrl());
			if (server == null) {
				server = new HttpSolrClient(getUrl());
				server.setConnectionTimeout(getQueryTimeout());
				server.setSoTimeout(getQueryTimeout());
				server.setDefaultMaxConnectionsPerHost(getMaxConnectionsPerHost());
				server.setMaxTotalConnections(getMaxConnectionsPerHost());
				server.setAllowCompression(true);
				solrServerMapping.put(getUrl(), server);
			}
			return server;
		}
	}
}
