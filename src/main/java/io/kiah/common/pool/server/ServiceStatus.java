package io.kiah.common.pool.server;

import io.kiah.common.pool.conf.ConfigurationManager;
import io.kiah.common.pool.conf.model.ServingConfiguration;
import io.kiah.common.pool.server.ping.ApplicationStatusWatcher;
import io.kiah.common.pool.server.ping.ServerHealthManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kiah on 1/23/16.
 */
public class ServiceStatus {

	private List<ApplicationStatus> applications;

	/**
	 * Default ctor.
	 * <p>
	 * It will collect application status by default.
	 * </p>
	 */
	public ServiceStatus() {
		retriveApplicationStatus();
	}

	/**
	 * Collect application status.
	 */
	private void retriveApplicationStatus() {

		applications = new ArrayList<ApplicationStatus>();
		for (ServingConfiguration inst : ConfigurationManager.getSolrServerConfiguration().servings) {
			ServerHealthManager cm = ApplicationStatusWatcher.getHealthManager(inst.bizKey);
			if (cm == null)
				continue;
			applications.add(new ApplicationStatus(cm));
		}
	}

	public List<ApplicationStatus> getApplications() {
		return applications;
	}
}
