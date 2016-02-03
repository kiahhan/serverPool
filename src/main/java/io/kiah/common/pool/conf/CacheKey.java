package io.kiah.common.pool.conf;

/**
 * Represents cache key for single record. Created by Kiah on 1/23/16.
 */
public interface CacheKey {
	/**
	 * Returns name for specified database/index.
	 *
	 * @return
	 */
	String getCacheIndex();

	/**
	 * Returns key for a record.
	 *
	 * @return
	 */
	String getKey();
}
