package io.kiah.common.pool.server;

import io.kiah.common.pool.utils.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * 封装一台Server的信息（Url）。 Created by Kiah on 1/24/16.
 */
public abstract class ApplicationServer<TResult> {

	private String url;
	private String hostname;
	private int port;
	private int timeout;
	private int maxConnectionPerHost;

	protected ApplicationServer(final TargetServerInfo targetServer) throws URISyntaxException {
		initServer(targetServer);
	}

	/**
	 * Initialize server info
	 *
	 * @param targetServer
	 * @throws URISyntaxException
	 */
	protected void initServer(final TargetServerInfo targetServer) throws URISyntaxException {
		if (targetServer == null || StringUtils.isNullOrEmpty(targetServer.getUrl())) {
			throw new RuntimeException("Target server url is null or empty.");
		}

		if (!targetServer.getUrl().startsWith("http://")) {
			this.url = "http://" + targetServer.getUrl();
		} else {
			this.url = targetServer.getUrl();
		}

		URI uri;
		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
			throw e;
		}
		this.hostname = uri.getHost();
		this.port = uri.getPort();

		this.timeout = targetServer.getTimeout();
		this.maxConnectionPerHost = targetServer.getMaxConnectionsPerHost();
	}

	public String getUrl() {
		return url;
	}

	public String getHostname() {
		return hostname;
	}

	public int getPort() {
		return port;
	}

	/**
	 * Returns the interval of timeout. 15 seconds by default.
	 * 
	 * @return
	 */
	public int getQueryTimeout() {
		if (timeout <= 0) {
			return 15000;
		}
		return timeout;
	}

	/**
	 *
	 * @return
	 */
	public int getMaxConnectionsPerHost() {
		if (maxConnectionPerHost <= 0) {
			return 70;
		}
		return maxConnectionPerHost;
	}

	public abstract TResult query() throws Exception;

}
