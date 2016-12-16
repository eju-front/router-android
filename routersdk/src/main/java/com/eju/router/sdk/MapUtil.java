package com.eju.router.sdk;

import java.util.Map;

/**
 * map utils
 *
 * @author tangqianwei
 */
/*package*/ class MapUtil {

    static <K, V> void foreach(
            Map<K, V> map, Consumer<? super K, ? super V> consumer) throws Exception {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            K k = entry.getKey();
            V v = entry.getValue();
            if(consumer.consume(k, v)) {
                break;
            }
        }
    }

    interface Consumer<K, V> {
        boolean consume(K key, V value) throws Exception;
    }
}
