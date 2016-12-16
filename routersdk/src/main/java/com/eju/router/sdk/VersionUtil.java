package com.eju.router.sdk;

/**
 * Created by Joe on 2016/11/30.
 * Email lovejjfg@gmail.com
 */

/*package*/ class VersionUtil {
    private static final String delimiter = "\\.";

    /**
     * 比较2个版本号
     *
     */
    static int versionCompare(String v1, String v2, boolean complete) {
        if (v1.equals(v2)) {
            return 0;
        }
        String[] v1s = v1.split(delimiter);
        String[] v2s = v2.split(delimiter);
        int len = complete
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
