package io.kiah.common.pool.conf;

import io.kiah.common.pool.utils.StringUtils;
import org.apache.log4j.Logger;

import javax.xml.bind.annotation.XmlValue;
import java.net.URI;
import java.net.URISyntaxException;

public class ServerInfo {

	private static final Logger log = Logger.getLogger(ServerInfo.class);

	private String url;
	private String hostname;
	private int port;

	public ServerInfo() {
	}

	public ServerInfo(final String url) {
		setUrl(url);
	}

	/**
	 * Returns server address.
	 *
	 * @Return
	 */
	public String getUrl() {
		return url;
	}

	@XmlValue
	public void setUrl(String url) {

		if (StringUtils.isNullOrEmpty(url)) {
			return;
		}

		if (url.indexOf("://") < 0) {
			url = "http://" + url;
		}

		this.url = url;

		URI uri = null;
		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
			log.error("Failed to parser url.", e);
			return;
		}

		this.hostname = uri.getHost();
		this.port = uri.getPort();
	}

	/**
	 * Returns host name.
	 *
	 * @return
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * Returns hostname:port
	 *
	 * @return
	 */
	public String getHost() {
		return getHostname().concat(":").concat(String.valueOf(getPort()));
	}

	/**
	 * Returns server port.
	 *
	 * @return
	 */
	public int getPort() {
		return port;
	}

	@Override
	public String toString() {
		return getUrl();
	}

	@Override
	public int hashCode() {
		return url.hashCode();
	}

	@Override
	public boolean equals(Object another) {

		if (another == null)
			return false;

		if (another == this)
			return true;

		if (!(another instanceof ServerInfo))
			return false;

		return another.hashCode() == hashCode();
	}
}