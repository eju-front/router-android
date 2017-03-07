package com.eju.router.sdk;

import android.content.Context;
import android.preference.PreferenceManager;


/*package*/ class PrefUtil {
    public static final String LoginResult = "LoginResult";
    private PrefUtil() {
    }

    /**
     * put int value with the key
     * @param context the mContext
     * @param key the key to save
     * @param value the value of the key
     */
    public static void putInt(Context context, String key, int value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(key, value)
                .apply();
    }
    /**
     * get int value with the key
     * @param context the mContext
     * @param key the key to save
     * @param defaultValue the defaultValue of the key
     */
    public static int getInt(Context context, String key, int defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(key, defaultValue);
    }
    /**
     * put String value with the key
     * @param context the mContext
     * @param key the key to save
     * @param value the value of the key
     */
    public static void putString(Context context, String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(key, value)
                .apply();
    }
    /**
     * get String value with the key
     * @param context the mContext
     * @param key the key to save
     * @param defaultValue the defaultValue of the key
     */
    public static String getString(Context context, String key, String defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(key, defaultValue);
    }

    /**
     * put boolean value with the key
     * @param context the mContext
     * @param key the key to save
     * @param value the value of the key
     */
    public static void putBoolean(Context context, String key, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(key, value)
                .apply();
    }
    /**
     * get boolean value with the key
     * @param context the mContext
     * @param key the key to save
     * @param defaultValue the defaultValue of the key
     */
    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(key, defaultValue);
    }
    /**
     * put float value with the key
     * @param context the mContext
     * @param key the key to save
     * @param value the value of the key
     */
    public static void putFloat(Context context, String key, float value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putFloat(key, value)
                .apply();
    }
    /**
     * get float value with the key
     * @param context the mContext
     * @param key the key to save
     * @param defaultValue the defaultValue of the key
     */
    public static float getFloat(Context context, String key, float defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getFloat(key, defaultValue);
    }

    /**
     * clear all data
     * @param context the mContext
     */
    public static void clear(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .clear()
                .apply();
    }

    /**
     * remove the specified value
     * @param context the mContext
     * @param key the key
     */
    public static void remove(Context context, String key) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .remove(key)
                .apply();
    }
}
