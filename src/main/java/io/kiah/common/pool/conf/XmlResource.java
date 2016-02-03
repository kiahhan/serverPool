package io.kiah.common.pool.conf;

import io.kiah.common.config.ConfigInstanceGenerator;
import io.kiah.common.pool.conf.model.SolrServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Kiah on 1/23/16.
 */
public class XmlResource {
	private static final Logger log = LoggerFactory.getLogger(XmlResource.class);

	private Cache instancePool;

	public XmlResource() {
		instancePool = LocalCache.cache;
	}

	public void reload(String xmlFile) {

		CacheKey key = createCacheKey(xmlFile);

		synchronized (this) {
			if (instancePool.containsKey(key)) {
				instancePool.remove(key);
			}
			getInstance(xmlFile);
		}
	}

	public <T extends SolrServerConfiguration> T get(String xmlFile) {
		return getInstance(xmlFile);
	}

	private <T extends SolrServerConfiguration> T getInstance(String xmlFile) {

		CacheKey key = createCacheKey(xmlFile);
		T result;

		if (!instancePool.containsKey(key)) {
			synchronized (this) {
				if (!instancePool.containsKey(key)) {
					result = (T) ConfigInstanceGenerator.getConfigInstance(xmlFile);
					if (result == null) {
						throw new RuntimeException(
								key.getKey() + " not in ServiceConfigRegister.xml or xml file not exists.");
					}
					instancePool.put(key, result);
				}
			}
		}

		result = (T) instancePool.get(key);

		if (result == null) {
			log.warn(String.format("Failed to retrive instance of xml file \"%s\", the result is NULL.", xmlFile));
		}

		return result;
	}

	/**
	 * Returns cache key object.
	 *
	 * @param key
	 *            The string identify an object.
	 * @return
	 */
	protected CacheKey createCacheKey(String key) {
		return new XmlCacheKey(key);
	}

	static class XmlCacheKey implements CacheKey {

		private final static String INDEX_NAME = "XML";

		private String key;

		public XmlCacheKey(String key) {
			this.key = key;
		}

		@Override
		public String getKey() {
			return key;
		}

		@Override
		public String getCacheIndex() {
			return INDEX_NAME;
		}
	}
}
