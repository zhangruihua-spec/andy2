package org.cocos2dx.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;

import org.cocos2dx.bridge.MessageManager;
import org.cocos2dx.observable.CommonCmd;

public class CocosBridgeEngineUtil {
    private static Context selfcontext;

    public static void init(Context context) {
        selfcontext = context;
    }
    public static void quitGame() {
        CommonCmd observable = MessageManager.getInstance().getObservable(CommonCmd.class);
        if (null != observable) {
            observable.setCommand("message://msg?cmd=quit_game");
            observable.notifyChanged();
        }
    }

    public static void openUrlBySystemDefaultBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        selfcontext.startActivity(intent);
    }


    public static String getDeviceInfo() {
        String deviceInfo = DeviceInfo.getDeviceInfo();
        return deviceInfo;
    }

    public static String getDeviceID() {
        String deviceid = DeviceInfo.getDeviceID();
        return deviceid;
    }

    public static String getMacID() {
        String macaddr = DeviceInfo.getMacAddr();
        return macaddr;
    }

    public static String getPackageName() {
        String pkg = AppUtil.getPackageName(selfcontext);
        return pkg;
    }
    public static String getVersionName() {
        String vname = DeviceInfo.getVersionName();
        return vname;
    }
    public static String getVersionCode() {
        String vCode = DeviceInfo.getVersionCode();
        return vCode;
    }
    public static String getChannel() {
        String chn = AppUtil.getChannel(selfcontext);
        return chn;
    }
    public static void onFinishLoadLaunchView() {
    }
    public static boolean isNetworkAvailable() {
        return true;
    }
    public static void trackEvent(String event){
        String track_event = String.format("%s", event);
        if (TextUtils.isEmpty(track_event)) {
            return;
        }
        AdjustEvent adjustEvent = new AdjustEvent(track_event);
        Adjust.trackEvent(adjustEvent);
    }
//    public static void nativeLog(String log,String morelog){
//
//    }
    public static final void startWatchNetWorkState() {

    }

    public static void openGpPay(String payindex,String token, String uid){
        CommonCmd observable = MessageManager.getInstance().getObservable(CommonCmd.class);
        if (null != observable) {
            observable.setCommand(String.format("message://gppay?payid=%s&token=%s&uid=%s", payindex,token,uid));
            observable.notifyChanged();
        }
    }
    public static void openUrlByWebview(String url) {
        CommonCmd observable = MessageManager.getInstance().getObservable(CommonCmd.class);
        if (null != observable) {
            observable.setCommand(String.format("message://url?url=%s", url));
            observable.notifyChanged();
        }
    }
    /**
     * 存储配置信息
     *
     * @return
     */
    public static final void saveStringToPreference(String key, String value) {
        AppConfigUtils.saveStringToPrefenence(selfcontext, key, value);
    }

    public static final String getStringFromPreference(String key) {
        return AppConfigUtils.getStringFromPrefenence(selfcontext, key);
    }
}
