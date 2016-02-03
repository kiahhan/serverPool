package io.kiah.common.pool.conf.model;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created by Kiah on 1/23/16.
 */
public class ServingConfiguration {

	@XmlAttribute(name = "bizKey")
	public String bizKey;

	@XmlAttribute(name = "cluster")
	public String cluster;
}
