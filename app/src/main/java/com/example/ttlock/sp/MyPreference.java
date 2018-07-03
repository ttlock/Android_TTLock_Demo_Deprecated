package com.example.ttlock.sp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

/**
 * Created by Administrator on 2016/8/7 0007.
 */
public class MyPreference {
    public static String ACCESS_TOKEN = "access_token";
    public static String OPEN_ID = "openid";

//    public static String UPDATE_DATE = "update_date";
//    public static String expires_in = "expires_in";

    public static int getInt(Context context, String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(key, 0);
    }

    public static int getOpenid(Context context, String key) {
        String openId = getStr(context, MyPreference.OPEN_ID);
        if(TextUtils.isEmpty(openId))
            return 0;
        return Integer.valueOf(openId);
    }

    public static void putStr(Context context, String key, String value) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getStr(Context context, String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(key, "");
    }

    public static boolean getBool(Context context, String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(key, false);
    }

    public static boolean contains(Context context, String key)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.contains(key);
    }

    public static void remove(Context context, String key)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.apply();
    }

}
