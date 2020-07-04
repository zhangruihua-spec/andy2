package org.cocos2dx.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class AppConfigUtils {
    private static final String TAG = AppConfigUtils.class.getSimpleName();
    public static final String GP_REFFERERURL = "GP_REFFERERURL";
    public static final String ADJUST_ADID = "ADJUST_ADID";
    public static final String ADJUST_GPS_ADID = "ADJUST_GPS_ADID";
    public static final String ADJUST_ANDROIDID = "ADJUST_ANDROIDID";
    public static final String ADJUST_MACSHORTMD5 = "ADJUST_MACSHORTMD5";


    public static SharedPreferences getPreferences(Context context) {
        if (context == null) {
//            LogHelper.e(TAG, "getPreferences ERR. context is nil");
            return null;
        }
        SharedPreferences preferences = context.getSharedPreferences("happyteenpatti",
                Context.MODE_PRIVATE);
        return preferences;
    }

    public static String getStringFromPrefenence(Context context, String key) {
        if (null == context) {
            return "";
        }
        return getPreferences(context).getString(key, "");
    }

    public static void saveStringToPrefenence(Context context, String key,
                                              String value) {
        if (null == context) {
            return;
        }

        Editor editor = getPreferences(context).edit();
        editor.putString(key, value);
        editor.apply();
    }
    public static void setVersionCode(Context context, int vCode) {
        SharedPreferences preference = getPreferences(context);
        preference.edit().putInt("buildcodeconfig", vCode)
                .commit();
    }

    public static int getVersionCode(Context context) {
        SharedPreferences preference = getPreferences(context);
        return preference.getInt("buildcodeconfig", 0);
    }
    public static void setChannel(Context context, String channel) {
        SharedPreferences preference = getPreferences(context);
        preference.edit().putString("appchannelconfig", channel)
                .commit();
    }

    public static String getChannel(Context context) {
        SharedPreferences preference = getPreferences(context);
        return preference.getString("appchannelconfig", null);
    }
}
