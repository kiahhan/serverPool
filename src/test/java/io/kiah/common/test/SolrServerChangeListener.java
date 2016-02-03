package io.kiah.common.test;

import io.kiah.common.config.ConfigInstanceGenerator;
import io.kiah.common.config.listener.IConfigChangedListener;
import io.kiah.common.config.register.Config;
import io.kiah.common.pool.server.ApplicationStatus;
import io.kiah.common.pool.server.ServiceStatus;
import io.kiah.common.pool.server.SolrConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolrServerChangeListener implements IConfigChangedListener {
	private static Logger logger = LoggerFactory.getLogger(SolrServerChangeListener.class);

	@Override
	public void configChanged(Config config) {
		logger.info("Local file changed");
		System.out.println(config.getXmlPath() + ":" + config.getLastModifyTime(config.getXmlPath()) + ":"
				+ config.getValue(config.getXmlPath()));
		System.out.println(ConfigInstanceGenerator.getConfigInstance(config.getXmlPath()).toString());
		System.out.println(config.getValue(config.getXmlPath()).toString());
		System.out.println(
				ConfigInstanceGenerator.getConfigInstance(config.getXmlPath()) == config.getValue(config.getXmlPath()));

	}

	public static void main(String[] args) throws InterruptedException {
		SolrConnectionPool.init();

		// List servers = SolrConnectionPool.getAliveServers("relatedLinks");

		// SolrServerConfiguration ssc =
		// SolrConnectionPool.getApplicationConfiguration();
		// System.out.println(ssc.getServingCluster("relatedLinks").serverList.toString());

		// Thread.sleep(10*1000);
		// System.out.println("alive:"+servers.toString());
		// System.out.println("dead:"+scm.getDeadServers().toString());

		for (int i = 0; i < 10; i++) {
			Thread.sleep(10 * 1000);

			ServiceStatus ss = new ServiceStatus();
			for (ApplicationStatus s : ss.getApplications()) {
				System.out.println(s.getName() + " alive string: " + s.getAlives().toString());
				System.out.println(s.getName() + " dead string: " + s.getDeads().toString());
			}
			// List s = SolrConnectionPool.getAliveServers("relatedLinks");
			// System.out.println("reload alive:"+s.toString());
		}

	}

}
