package io.kiah.common.pool.server;

/**
 * Returns a server choose from connection pool. Created by Kiah on 1/23/16.
 */
public class TargetServerInfo {
	private final String url;
	private final int timeout;
	private final int maxConnectionsPerHost;

	private TargetServerInfo(final String url, final int timeout, final int maxConnectionsPerHost) {
		this.url = url;
		this.timeout = timeout;
		this.maxConnectionsPerHost = maxConnectionsPerHost;
	}

	public String getUrl() {
		return url;
	}

	public int getTimeout() {
		return timeout;
	}

	public int getMaxConnectionsPerHost() {
		return maxConnectionsPerHost;
	}

	@Override
	public String toString() {
		return getUrl();
	}

	public static TargetServerInfo createServer(final String serverUrl, final int timeout,
			final int maxConnectionsPerHost) {
		return new TargetServerInfo(serverUrl, timeout, maxConnectionsPerHost);
	}
}
