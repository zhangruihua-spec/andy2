package com.keeplive;

import android.content.Context;
import android.content.SharedPreferences;

import com.keeplive.util.Utils;

import java.util.List;

public class Preference {

    private static Preference sInstance;
    private SharedPreferences mPreferences;
    private static final String KEY_KEEP_LIVE_SERVICE_NAMES = "keep_live_service_names";
    private static final String KEY_KEEP_LIVE_INTERVAL = "keep_live_interval";
    private static final String KEY_KEEP_LIVE_LAST_TIME = "keep_live_last_time";

    public static Preference init(Context context) {
        if (null == sInstance) {
            sInstance = new Preference(context);
        }
        return sInstance;
    }

    public static Preference getInstance() {
        return sInstance;
    }

    private Preference(Context context) {
        mPreferences = context.getSharedPreferences("keep_live", Context.MODE_PRIVATE);
    }

    public void saveServiceNames(List<String> serviceNames) {
        if (null != serviceNames) {
            String content = Utils.joinString(serviceNames, ",");
            mPreferences.edit().putString(KEY_KEEP_LIVE_SERVICE_NAMES, content).apply();
        }
    }

    public List<String> getServiceNames() {
        String content = mPreferences.getString(KEY_KEEP_LIVE_SERVICE_NAMES, "");
        return Utils.splitString(content, ",");
    }

    public void saveInterval(long keepInterval) {
        mPreferences.edit().putLong(KEY_KEEP_LIVE_INTERVAL, keepInterval).apply();
    }

    public long getInterval() {
        return mPreferences.getLong(KEY_KEEP_LIVE_INTERVAL, 15 * 60 * 1000);//默认拉活间隔15分钟
    }

    public void saveKeepLiveLastTime(long lastTime) {
        mPreferences.edit().putLong(KEY_KEEP_LIVE_LAST_TIME, lastTime).apply();
    }

    public long getKeepLiveLastTime() {
        return mPreferences.getLong(KEY_KEEP_LIVE_LAST_TIME, 0);
    }
}
