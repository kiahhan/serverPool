package io.kiah.common.pool.conf;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlEnum(String.class)
public enum LoadBalanceMethod {

	/**
	 * 
	 */
	@XmlEnumValue("sequential") Sequential,

	/**
	 * Round Robin Algorithm
	 */
	@XmlEnumValue("roundRobin") RoundRobin,

	/**
	 * Consistent Hash Algorithm
	 */
	@XmlEnumValue("hash") Hash;
}