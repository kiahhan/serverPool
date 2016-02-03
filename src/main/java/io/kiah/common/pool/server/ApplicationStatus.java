package io.kiah.common.pool.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.kiah.common.pool.conf.LoadBalanceMethod;
import io.kiah.common.pool.conf.ServerInfo;
import io.kiah.common.pool.server.ping.ServerHealthManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents application status. Contains:
 * <p>
 * 1. alive server list.
 * </p>
 * <p>
 * 2. dead servers list.
 * </p>
 */
public class ApplicationStatus {

	private final ServerHealthManager shm;

	public ApplicationStatus(final ServerHealthManager shm) {
		this.shm = shm;
	}

	public String getName() {
		return shm.getBizKey();
	}

	/**
	 * @return
	 */
	@JsonProperty(value = "lbMethod", required = true)
	public LoadBalanceMethod getLBMethod() {
		return shm.getLBMethod();
	}

	@JsonProperty(value = "pingTimeout", required = true)
	public int getPingTimeout() {
		return shm.getPingTimeout();
	}

	@JsonProperty(value = "queryTimeout", required = true)
	public int getQueryTimeout() {
		return shm.getQueryTimeout();
	}

	/**
	 * @return
	 */
	@JsonProperty(value = "lastUpdate", required = true)
	public String getLastUpdate() {
		return shm.getLastUpdate().toString();
	}

	/**
	 * Returns alive server URL list
	 *
	 * @return
	 */
	public List<String> getAlives() {

		List<String> result = new ArrayList<String>(shm.getAliveServers().size());

		for (ServerInfo s : shm.getAliveServers()) {
			result.add(s.getUrl());
		}

		return result;
	}

	/**
	 * Returns dead server URL list
	 *
	 * @return
	 */
	public List<String> getDeads() {

		List<String> result = new ArrayList<String>(shm.getDeadServers().size());

		for (ServerInfo s : shm.getDeadServers()) {
			result.add(s.getUrl());
		}

		return result;
	}
}
