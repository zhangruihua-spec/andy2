package com.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AppConfigUtils {
    private static final String TAG = AppConfigUtils.class.getSimpleName();

    public static SharedPreferences getPreferences(Context context) {
        if (context == null) {
//            LogHelper.e(TAG, "getPreferences ERR. context is nil");
            return null;
        }
        SharedPreferences preferences = context.getSharedPreferences("gold_teenpatti_888",
                Context.MODE_PRIVATE);
        return preferences;
    }

    private static final String KEY_APK_RUN_COUNT = "KEY_APK_RUN_COUNT";
    private static final String KEY_PUSH_NOTIFICATION_DATA = "KEY_PUSH_NOTIFICATION_DATA";
    private static final String KEY_PUSH_NOTIFICATION_IDS = "KEY_PUSH_NOTIFICATION_IDS";

    private static final int DEFAULT_APK_RUN_COUNT = 0;

    /**
     * @param context
     * @param apkruncount
     * @return
     */
    public static boolean setApkRunCount(Context context, int apkruncount) {
        if (null == context) {
//            LogHelper.e(TAG, "setApkRunCount ERR. context is nil");
            return false;
        }// end if
        SharedPreferences preferences = getPreferences(context);
        preferences.edit().putInt(KEY_APK_RUN_COUNT, apkruncount).commit();
        return true;

    }

    public static int getApkRunCount(Context context) {
        int count = 0;
        if (null == context) {
//            LogHelper.e(TAG, "getApkRunCount ERR. context is nil");
            return DEFAULT_APK_RUN_COUNT;
        }// end if
        SharedPreferences preferences = getPreferences(context);
        count = preferences.getInt(KEY_APK_RUN_COUNT, DEFAULT_APK_RUN_COUNT);
        return count;
    }

    private static final String SAVED_APP_VERSION_CODE = "SAVED_APP_VERSION_CODE";

    public static int getSavedAppVersionCode(Context context) {
        SharedPreferences preferences = getPreferences(context);
        return preferences.getInt(SAVED_APP_VERSION_CODE, -1);
    }

    public static void saveAppVersionCode(Context context, int versionVode) {

        SharedPreferences preferences = getPreferences(context);
        preferences
                .edit()
                .putInt(SAVED_APP_VERSION_CODE, versionVode).commit();
    }

    private static final String KEY_SCREEN_DISPLAY_WIDTH = "KEY_SCREEN_DISPLAY_WIDTH";

    private static final int DEFAULT_SCREEN_DISPLAY_WIDTH = 0;

    public static boolean setScreenDisplayWidth(Context context, int width) {
        if (null == context) {
//            LogHelper.e(TAG, "setApkRunCount ERR. context is nil");
            return false;
        }// end if
        SharedPreferences preferences = getPreferences(context);
        preferences.edit().putInt(KEY_SCREEN_DISPLAY_WIDTH, width).commit();
        return true;
    }

    public static int getScreenDisplayWidth(Context context) {
        int width = 0;
        if (null == context) {
//            LogHelper.e(TAG, "getScreenDisplayWidth ERR. context is nil");
            return DEFAULT_SCREEN_DISPLAY_WIDTH;
        }// end if
        SharedPreferences preferences = getPreferences(context);
        width = preferences.getInt(KEY_SCREEN_DISPLAY_WIDTH,
                DEFAULT_SCREEN_DISPLAY_WIDTH);
        return width;
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

    public static boolean getBooleanFromPrefenence(Context context, String key,
                                                   boolean defaultValue) {
        if (null == context) {
            return defaultValue;
        }
        return getPreferences(context).getBoolean(key, defaultValue);
    }

    public static void saveBooleanToPrefenence(Context context, String key,
                                               boolean value) {
        if (null == context) {
            return;
        }

        Editor editor = getPreferences(context).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean isPassAppStore(Context context) {
        if (null == context) {
            return false;
        }
        return getPreferences(context).getBoolean("passappstore", false);
    }

    public static void setPassAppStore(Context context, boolean pass) {
        if (null == context) {
            return;
        }

        Editor editor = getPreferences(context).edit();
        editor.putBoolean("passappstore", pass);
        editor.commit();
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

    public static void setVersionCode(Context context, int vCode) {
        SharedPreferences preference = getPreferences(context);
        preference.edit().putInt("appcodeconfig", vCode)
                .commit();
    }

    public static int getVersionCode(Context context) {
        SharedPreferences preference = getPreferences(context);
        return preference.getInt("appcodeconfig", 0);
    }

    public static void saveUid(Context context, String uid) {
        SharedPreferences preference = getPreferences(context);
        preference.edit().putString("gameuidconfig", uid)
                .commit();
    }

    public static String getUid(Context context) {
        SharedPreferences preference = getPreferences(context);
        return preference.getString("gameuidconfig", null);
    }

    public static void saveToken(Context context, String token) {
        SharedPreferences preference = getPreferences(context);
        preference.edit().putString("gametokenconfig", token)
                .commit();
    }

    public static String getToken(Context context) {
        SharedPreferences preference = getPreferences(context);
        return preference.getString("gametokenconfig", null);
    }

    public static String getDD2String(Context context) {
        if (null == context) {
            return null;
        }
        return getPreferences(context).getString("keydd2", null);
    }

    public static void saveDD2String(Context context, String jsonString) {
        if (null == context) {
            return;
        }

        Editor editor = getPreferences(context).edit();
        editor.putString("keydd2", jsonString);
        editor.commit();
    }

    public static void saveUpdateVersionCode(Context context, int code) {
        SharedPreferences preference = getPreferences(context);
        preference.edit().putInt("upvcode", code).commit();
    }

    public static int getUpdateVersionCode(Context context) {
        SharedPreferences preference = getPreferences(context);
        return preference.getInt("upvcode", 0);
    }

    public static void saveUpdateVersionName(Context context, String name) {
        SharedPreferences preference = getPreferences(context);
        preference.edit().putString("upvname", name).commit();
    }

    public static String getUpdateVersionName(Context context) {
        SharedPreferences preference = getPreferences(context);
        return preference.getString("upvname", "");
    }

    public static void saveUpdateTitle(Context context, String content) {
        SharedPreferences preference = getPreferences(context);
        preference.edit().putString("upvtitle", content).commit();
    }

    public static String getUpdateTitle(Context context) {
        SharedPreferences preference = getPreferences(context);
        return preference.getString("upvtitle", "");
    }

    public static void saveUpdateContent(Context context, String content) {
        SharedPreferences preference = getPreferences(context);
        preference.edit().putString("upvmsg", content).commit();
    }

    public static String getUpdateContent(Context context) {
        SharedPreferences preference = getPreferences(context);
        return preference.getString("upvmsg", "");
    }

    public static void saveUpdateDownloadUrl(Context context, String url) {
        SharedPreferences preference = getPreferences(context);
        preference.edit().putString("upvurl", url).commit();
    }

    public static String getUpdateDownloadUrl(Context context) {
        SharedPreferences preference = getPreferences(context);
        return preference.getString("upvurl", "");
    }

    public static void saveUpdateForcesMode(Context context, boolean mode) {
        SharedPreferences preference = getPreferences(context);
        preference.edit().putBoolean("upvmodel", mode).commit();
    }

    public static boolean getUpdateForcesMode(Context context) {
        SharedPreferences preference = getPreferences(context);
        return preference.getBoolean("upvmodel", false);
    }

    public static void savePushNotificationIds(Context context, String idJson) {
        saveStringToPrefenence(context, KEY_PUSH_NOTIFICATION_IDS, idJson);
    }

    public static String getPushNotificationIds(Context context) {
        return getStringFromPrefenence(context, KEY_PUSH_NOTIFICATION_IDS);
    }

    public static void savePushNotificationData(Context context, String data) {
        saveStringToPrefenence(context, KEY_PUSH_NOTIFICATION_DATA, data);
    }

    public static String getPushNotificationData(Context context) {
        return getStringFromPrefenence(context, KEY_PUSH_NOTIFICATION_DATA);
    }

}
