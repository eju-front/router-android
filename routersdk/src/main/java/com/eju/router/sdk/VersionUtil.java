package com.eju.router.sdk;

/**
 * Created by Joe on 2016/11/30.
 * Email lovejjfg@gmail.com
 */

/*package*/ class VersionUtil {
    private static final String delimiter = "\\.";

    /**
     * compare v1 to v2
     *
     * @param v1 the first
     * @param v2 the second
     * @param deepComplete whether compare all length
     * @return a negative integer, zero, or a positive integer as the
     * specified String is greater than, equal to, or less
     * than this String, ignoring case considerations.
     */
    static int versionCompare(String v1, String v2, boolean deepComplete) {
        if (v1.equals(v2)) {
            return 0;
        }
        String[] v1s = v1.split(delimiter);
        String[] v2s = v2.split(delimiter);
        int len = deepComplete
                ? Math.max(v1s.length, v2s.length)
                : Math.min(v1s.length, v2s.length);

        for (int i = 0; i < len; i++) {
            String c1 = null == v1s[i] ? "" : v1s[i];
            String c2 = null == v2s[i] ? "" : v2s[i];

            int result = c1.compareToIgnoreCase(c2);
            if (result != 0) return result;
        }

        return 0;
    }
}
