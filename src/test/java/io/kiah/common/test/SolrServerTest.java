package io.kiah.common.test;

import io.kiah.common.pool.server.*;
import io.kiah.common.pool.utils.DateUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrException;

import java.util.Date;
import java.util.concurrent.Callable;

public class SolrServerTest {

	public static void main(String[] args) throws Exception {
		SolrConnectionPool.init();
		Thread.sleep(10000);
		for (int i = 0; i <= 100; i++) {
			Thread.sleep(500);
			ServiceStatus ss = new ServiceStatus();
			for (ApplicationStatus s : ss.getApplications()) {
				System.out.println(s.getName() + " alive string: " + s.getAlives().toString());
				System.out.println(s.getName() + " dead string: " + s.getDeads().toString());
			}
		}
		// ExecutorService es = Executors.newFixedThreadPool(2);
		// List<Future<String>> results = new ArrayList<Future<String>>();
		// for (int i = 0; i <= 10; i++) {
		// Future<String> f = null;

		// if(i%2==0){
		// f=es.submit(new SubTask_Solr_RL());
		// }else{
		// f=es.submit(new SubTask_Solr_All());
		// }
		// f = es.submit(new SubTask_Date());

		// f=es.submit(new SubTask_Solr_All());
		// results.add(f);
		// }
		//
		// for (Future<String> f : results) {
		// f.get();
		// System.out.println("[used Time]=" + f.get());
		// }
		// es.shutdownNow();
	}

	/**
	 * sub Task class
	 */
	public static class SubTask_Solr_All implements Callable<String> {

		@Override
		public String call() throws Exception {
			long start = System.currentTimeMillis();
			QueryResponse qr = null;
			try {
				SolrQuery sq = new SolrQuery("*:*");
				SolrServer ss = new SolrServer(sq, "pinyin", null);
				qr = ss.query();
			} catch (Exception e) {
				System.out.println("sdafsdafsafasdfasdfsad");
				e.printStackTrace();
			}
			long end = System.currentTimeMillis();
			return qr.getResults().getNumFound() + " [used Time]= " + (end - start);
		}
	}

	/**
	 * sub Task class
	 */
	public static class SubTask_Solr_RL implements Callable<String> {

		@Override
		public String call() throws Exception {
			long start = System.currentTimeMillis();
			TargetServerInfo server = SolrConnectionPool.getTargetServer("meal", null);
			SolrClient solrClient = new HttpSolrClient(server.getUrl());

			QueryResponse response = null;
			SolrQuery query = new SolrQuery();
			query.setQuery("*:*");
			try {
				response = solrClient.query(query);

			} catch (SolrException e) {
				System.err.println("solr client create error");
			} catch (Exception e) {
				System.err.println("IO error");
			}
			long end = System.currentTimeMillis();
			return response.getResults().getNumFound() + " [used Time]= " + (end - start);
		}
	}

	public static class SubTask_Date implements Callable<String> {

		@Override
		public String call() throws Exception {
			long start = System.currentTimeMillis();
			Date yesterday = DateUtils.getThenNewDate(-1);
			long end = System.currentTimeMillis();
			long result = end - start;
			return this.hashCode() + " | " + result + " | " + yesterday;
		}
	}

}
