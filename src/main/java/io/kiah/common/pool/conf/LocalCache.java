package io.kiah.common.pool.conf;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Kiah on 1/23/16.
 */
public class LocalCache implements Cache {

	private static Map<String, ConcurrentHashMap<String, Object>> cacheMap;

	LocalCache() {
		cacheMap = new HashMap<String, ConcurrentHashMap<String, Object>>();
	}

	public static LocalCache cache = new LocalCache();

	@Override
	public <V extends Serializable> void put(CacheKey key, V obj) {

		if (!cacheMap.containsKey(key.getCacheIndex())) {
			synchronized (this) {
				if (!cacheMap.containsKey(key.getCacheIndex())) {
					cacheMap.put(key.getCacheIndex(), new ConcurrentHashMap<String, Object>());
				}
			}
		}

		cacheMap.get(key.getCacheIndex()).put(key.getKey(), obj);
	}

	@Override
	public <V extends Serializable> V get(CacheKey key) {
		ConcurrentHashMap<String, Object> objectMap = cacheMap.get(key.getCacheIndex());
		if (objectMap == null || objectMap.isEmpty())
			return null;

		return (V) objectMap.get(key.getKey());
	}

	@Override
	public boolean containsKey(CacheKey key) {
		return cacheMap.containsKey(key.getCacheIndex()) && cacheMap.get(key.getCacheIndex()).containsKey(key.getKey());
	}

	@Override
	public void remove(CacheKey key) {
		if (!cacheMap.containsKey(key.getCacheIndex()))
			return;

		cacheMap.get(key.getCacheIndex()).remove(key.getKey());
	}
}
