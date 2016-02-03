package io.kiah.common.pool.conf.model;

import io.kiah.common.pool.conf.ServerInfo;
import io.kiah.common.pool.utils.StringUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "configuration")
public class SolrServerConfiguration implements Serializable {

	public static final String FILE_NAME = "server-solr.xml";

	@XmlElementWrapper(name = "serving")
	@XmlElement(name = "inst")
	public List<ServingConfiguration> servings;

	@XmlElementWrapper(name = "clusters")
	@XmlElement(name = "cluster")
	public List<Cluster> clusters;

	public Cluster getServingCluster(String bizKey) {
		if (bizKey == null || "".equals(bizKey))
			return null;

		if (servings == null || servings.size() == 0)
			return null;

		if (clusters == null || clusters.size() == 0)
			return null;

		for (ServingConfiguration s : servings) {

			if (StringUtils.isNullOrEmpty(s.cluster))
				continue;

			if (s.bizKey.equalsIgnoreCase(bizKey)) {

				for (Cluster c : clusters) {
					if (c.name.equalsIgnoreCase(s.cluster))
						return c;
				}

				break;
			}
		}

		return null;

	}

	/**
	 * 获得所有Solr实例的Url
	 */
	public List<String> getAllClusterSolrUrl() {

		List<String> list = new ArrayList<String>();

		if (clusters != null && clusters.size() > 0) {
			for (Cluster cluster : clusters) {
				if (cluster.serverList != null) {
					for (ServerInfo serverInfo : cluster.serverList) {
						list.add(serverInfo.getUrl());
					}
				}
			}
		}

		return list;
	}

}