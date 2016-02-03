package io.kiah.common.test;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrException;

import java.io.IOException;

/**
 * Created by Kiah on 1/26/16.
 */
public class SearchTest {
	public static void main(String[] args) {

		SolrClient solrClient = new HttpSolrClient("http://localhost:8983/solr/reviewmc/");

		QueryResponse response = null;
		SolrQuery query = new SolrQuery();
		query.setQuery("*:*");
		try {
			solrClient.ping();
			// response = solrClient.query(query);

		} catch (SolrException e) {
			System.err.println("solr client create error");
		} catch (IOException e) {
			System.err.println("IO error");
		} catch (SolrServerException e) {
			System.err.println("SOLR server error");
		}
		System.out.println("xxx:" + response);
	}
}
