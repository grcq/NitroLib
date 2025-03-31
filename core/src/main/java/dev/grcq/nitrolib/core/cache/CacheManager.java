package dev.grcq.nitrolib.core.cache;

import dev.grcq.nitrolib.core.utils.KeyValue;

import java.util.HashMap;
import java.util.Map;

public class CacheManager {

    private final Map<String, KeyValue<Object, Long>> cache;

    public CacheManager() {
        this.cache = new HashMap<>();
    }

    public void set(String key, Object value, long ttl) {
        this.cache.put(key, KeyValue.of(value, System.currentTimeMillis() + ttl));
    }

    public Object get(String key) {
        KeyValue<Object, Long> kv = this.cache.get(key);
        if (kv == null) {
            return null;
        }
        if (kv.getValue() > 0 && kv.getValue() < System.currentTimeMillis()) {
            this.cache.remove(key);
            return null;
        }
        return kv.getKey();
    }

    public boolean exists(String key) {
        boolean exists = this.cache.containsKey(key);
        if (!exists) return false;

        KeyValue<Object, Long> kv = this.cache.get(key);
        if (kv.getValue() > 0 && kv.getValue() < System.currentTimeMillis()) {
            this.cache.remove(key);
            return false;
        }

        return true;
    }

    public void delete(String key) {
        this.cache.remove(key);
    }

    public void clear() {
        this.cache.clear();
    }
}
