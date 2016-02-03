package io.kiah.common.pool.conf;

/**
 * Created by Kiah on 1/23/16.
 */
public class ConfigResource extends XmlResource {

	public ConfigResource() {
		super();
	}

	/**
	 * Returns cache key object.
	 *
	 * @param key
	 *            The string identify an object.
	 * @return
	 */
	@Override
	protected CacheKey createCacheKey(String key) {
		return new ConfigCacheKey(key);
	}

	static class ConfigCacheKey implements CacheKey {

		private final static String INDEX_NAME = "CONF";

		private String key;

		public ConfigCacheKey(String key) {
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
