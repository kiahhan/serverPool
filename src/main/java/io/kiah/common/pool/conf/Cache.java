package io.kiah.common.pool.conf;

import java.io.Serializable;

/**
 * Created by Kiah on 1/23/16.
 */
public interface Cache {
	<V extends Serializable> void put(CacheKey key, V obj);

	<V extends Serializable> V get(CacheKey key);

	boolean containsKey(CacheKey key);

	void remove(CacheKey key);
}
