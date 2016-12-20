package com.eju.router.sdk;

import java.util.Map;

/**
 * map utils
 *
 * @author tangqianwei
 */
/*package*/ class MapUtil {

    /**
     * iterate the map with {@link Consumer}
     *
     * @param map         map
     * @param consumer    consumer
     * @param <K>         Key Template
     * @param <V>         Value Template
     * @throws Exception if error
     */
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

    /**
     * Consumer
     *
     * @param <K>    Key Template
     * @param <V>    Value Template
     */
    interface Consumer<K, V> {

        /**
         * handle each pair of MapEntry
         *
         * @param key      key of entry
         * @param value    value of entry
         * @return whether should handle next pair
         * @throws Exception if error
         */
        boolean consume(K key, V value) throws Exception;
    }
}
