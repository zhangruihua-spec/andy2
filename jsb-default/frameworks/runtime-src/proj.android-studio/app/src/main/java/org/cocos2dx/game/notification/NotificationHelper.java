package org.cocos2dx.game.notification;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.util.AppConfigUtils;

import org.cocos2dx.game.activity.MainActivity;
import org.cocos2dx.lib.Cocos2dxJavascriptJavaBridge;
import org.json.JSONArray;
import org.json.JSONException;

public class NotificationHelper {

    static final String KEY_TRACK_CLICK = "_notification_track_click";
    static final String KEY_TRACK_PUSH_SUCCESSED = "_notification_track_push_successed";

    public static void saveTrackClick(Context context, String pushInfoStr) {
        appendString(context, KEY_TRACK_CLICK, pushInfoStr);
    }

    public static void saveTrackPushSuccessd(Context context, String pushInfoStr) {
        appendString(context, KEY_TRACK_PUSH_SUCCESSED, pushInfoStr);
    }

    static void appendString(Context context, String key, String pushInfoStr) {
        String trackClickStr = AppConfigUtils.getStringFromPrefenence(context, key);
        JSONArray jsonArray = null;
        try {
            if (TextUtils.isEmpty(trackClickStr)) {
                jsonArray = new JSONArray();
                jsonArray.put(0, pushInfoStr);
            } else {
                jsonArray = new JSONArray(trackClickStr);
                if (jsonArray == null) {
                    jsonArray = new JSONArray();
                    jsonArray.put(0, pushInfoStr);
                } else {
                    jsonArray.put(jsonArray.length(), pushInfoStr);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonArray == null) {
            jsonArray = new JSONArray();
        }
        AppConfigUtils.saveStringToPrefenence(context, key, jsonArray.toString());
        Log.i("Notification", "saveTrack " + key + " =" + jsonArray.toString());
    }

    static void reportTrackClick(Context context) {
        String jsonStr = AppConfigUtils.getStringFromPrefenence(context, KEY_TRACK_CLICK);
        if (TextUtils.isEmpty(jsonStr)) {
            // do nothing
        } else {
            try {
                JSONArray jsonArray = new JSONArray(jsonStr);
                if (jsonArray != null && jsonArray.length() > 0) {
                    for (int index = 0; index < jsonArray.length(); index++) {
                        String pushInfoStr = jsonArray.optString(index);
                        if (!TextUtils.isEmpty(pushInfoStr)) {
                            Cocos2dxJavascriptJavaBridge.evalString(String.format("cc.onNativeDidReceiveNotificationResponse('%s')", pushInfoStr));
                        }
                    }
                }
                AppConfigUtils.saveStringToPrefenence(context, KEY_TRACK_CLICK, "");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i("Notification", "reportTrackClick=" + e.getMessage());
            }
        }
    }

    static void reportTrackPushSuccessed(Context context) {
        String jsonStr = AppConfigUtils.getStringFromPrefenence(context, KEY_TRACK_PUSH_SUCCESSED);
        if (TextUtils.isEmpty(jsonStr)) {
            // do nothing
        } else {
            try {
                JSONArray jsonArray = new JSONArray(jsonStr);
                if (jsonArray != null && jsonArray.length() > 0) {
                    for (int index = 0; index < jsonArray.length(); index++) {
                        String pushInfoStr = jsonArray.optString(index);
                        if (!TextUtils.isEmpty(pushInfoStr)) {
                            Cocos2dxJavascriptJavaBridge.evalString(String.format("cc.onNativePushNotificationSucceed('%s')", pushInfoStr));
                        }
                    }
                }
                AppConfigUtils.saveStringToPrefenence(context, KEY_TRACK_PUSH_SUCCESSED, "");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i("Notification", "reportTrackPushSuccessed=" + e.getMessage());
            }
        }
    }

    public static void reportTrack(final MainActivity appActivity) {
        appActivity.runOnGLThread(new Runnable() {
            @Override
            public void run() {
                reportTrackClick(appActivity);
                reportTrackPushSuccessed(appActivity);
                Log.i("Notification", "reportTrack=" + AppConfigUtils.getStringFromPrefenence(appActivity, KEY_TRACK_CLICK) + " <>" + AppConfigUtils.getStringFromPrefenence(appActivity, KEY_TRACK_PUSH_SUCCESSED));
            }
        });
    }
}
