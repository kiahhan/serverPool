package io.kiah.common.pool.conf.model;

import io.kiah.common.pool.conf.LoadBalanceMethod;
import io.kiah.common.pool.conf.ServerInfo;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class Cluster {

	private static final int DEFAULT_PING_TIMEOUT = 10 * 1000; // 10 seconds by
																// default.
	private static final int DEFAULT_QUERY_TIMEOUT = 15 * 1000; // 15 seconds by
																// default.
	private static final int DEFAULT_MAX_CONNECTION_PER_HOST = 70;

	public Cluster() {
		this.pingTimeout = DEFAULT_PING_TIMEOUT;
		this.queryTimeout = DEFAULT_QUERY_TIMEOUT;
		this.maxConnectionsPerHost = DEFAULT_MAX_CONNECTION_PER_HOST;
	}

	/**
	 * Gets or sets cluster name.
	 */
	@XmlAttribute(name = "name")
	public String name;

	/**
	 * Gets or sets load balance method.
	 */
	@XmlAttribute(name = "lbMethod")
	public LoadBalanceMethod lbMethod;

	/**
	 * Gets or sets timeout of health check.
	 */
	@XmlAttribute(name = "pingTimeout")
	public int pingTimeout;

	/**
	 * Gets or sets timeout of query.
	 */
	@XmlAttribute(name = "queryTimeout")
	public int queryTimeout;

	/**
	 * Gets or sets server list.
	 */
	@XmlElement(name = "server")
	public List<ServerInfo> serverList;

	/**
	 * Gets or set maxConnectionsPerhost
	 */
	@XmlAttribute(name = "maxConnectionsPerHost")
	public int maxConnectionsPerHost;

	@Override
	public String toString() {
		return String.format("{Cluster:%s, lbMethod:%s, pingTimeout:%d, queryTimeout:%d}", name, lbMethod.toString(),
				pingTimeout, queryTimeout, maxConnectionsPerHost);
	}
}