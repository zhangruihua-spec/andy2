package com.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;
import com.util.observable.CommandMapBean;
import com.util.observable.CommandMapObservable;
import com.util.observable.CommandObservable;
import com.util.observable.ViewUtil;
import com.util.observable.UploadAvatarObservable;

import java.io.File;
import java.util.HashMap;

import bridge.ObservableManager;


public class EngineUtil {

    private static final String TAG = "EngineUtil";

    private static Context mContext;
    private static AbstractNotification mAbstractNotification;

    public static void init(Context context, AbstractNotification abstractNotification) {
        mContext = context;
        mAbstractNotification = abstractNotification;
    }

    public static final void test() {

    }
    public static final void quitGame() {

        CommandObservable observable = ObservableManager.getInstance().getObservable(CommandObservable.class);
        if (null != observable) {
            observable.setCommand("message://command?cmd=quit_game");
            observable.notifyChanged();
        }
    }

    public static boolean hasWechat() {
        return hasApp("com.tencent.mm");
    }

    public static boolean hasQQ() {
        return hasApp("com.tencent.mobileqq");
    }

    public static boolean hasApp(String pkg) {
        return AppUtil.checkApkExist(mContext, pkg);
    }



    public static void changeUserAvatar(int type) {
//        android.util.Log.e("changeUserAvatar", "changeUserAvatar: " + type);
        UploadAvatarObservable observable = ObservableManager.getInstance().getObservable(UploadAvatarObservable.class);
        if (null != observable) {
            observable.setAvatarType(type);
            observable.notifyUploadAvatar();
        }
    }


    public static void openUrlByDefaultBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        mContext.startActivity(intent);
    }

    public static void openUrlByGameCity(String url) {
        CommandObservable observable = ObservableManager.getInstance().getObservable(CommandObservable.class);
        if (null != observable) {
            observable.setCommand(String.format("message://url?url=%s", url));
            observable.notifyChanged();
        }
    }



    public static String getDeviceInfo() {
        String deviceInfo = Device.getDeviceInfo();
        outPutLog("getDeviceInfo", deviceInfo);
        return deviceInfo;
    }

    public static String getDeviceID() {
        String dID = Device.getDeviceID();
        outPutLog("getDeviceID", dID);
        return dID;
    }

    public static String getMacID() {
        String mID = Device.getMacAddr();
        outPutLog("getMacID", mID);
        return mID;
    }

    public static String getPackageName() {
        String pkg = AppUtil.getPackageName(mContext);
        outPutLog("getPackageName", pkg);
        return pkg;
    }

    public static String getVersionName() {
        String vName = Device.getVersionName();
        outPutLog("getVersionName", vName);
        return vName;
    }

    public static String getVersionCode() {
        String vCode = Device.getVersionCode();
        outPutLog("getVersionCode", vCode);
        return vCode;
    }

    public static String getChannel() {
        String chn = AppUtil.getChannel(mContext);
        outPutLog("getChannel", chn);
        return chn;
    }



    public static void startQQ(final String str) {

        ViewUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ClipboardManager manager = (ClipboardManager)
                        mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                manager.setPrimaryClip(ClipData.newPlainText(null, str));
                Toast.makeText(mContext, "已复制QQ号码", Toast.LENGTH_SHORT).show();
            }
        });

        AppUtil.startAppByPackageName(mContext, "com.tencent.mobileqq");
    }

    public static void startWechat(final String str) {

        ViewUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ClipboardManager manager = (ClipboardManager)
                        mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                manager.setPrimaryClip(ClipData.newPlainText(null, str));
                Toast.makeText(mContext, "已复制微信号码", Toast.LENGTH_SHORT).show();
            }
        });

        AppUtil.startAppByPackageName(mContext, "com.tencent.mm");
    }

    //
    public static void startWechatEvent(final String str) {

        ViewUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ClipboardManager manager = (ClipboardManager)
                        mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                manager.setPrimaryClip(ClipData.newPlainText(null, str));
//                Toast.makeText(mContext, "已复制微信号码", Toast.LENGTH_SHORT).show();
            }
        });

        AppUtil.startAppByPackageName(mContext, "com.tencent.mm");
    }


    public static void copyText(final String strText) {

        ViewUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ClipboardManager manager = (ClipboardManager)
                        mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                manager.setPrimaryClip(ClipData.newPlainText(null, strText));
                //Toast.makeText(mContext, "已复制到剪切板", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //1 QQ 2 微信
    public static void startApp(final String str) {

        String[] strSplit = str.split(":");
        switch (strSplit[0]) {
            case "1": {
                startQQ(strSplit[1]);
                break;
            }
            case "2": {
                startWechat(strSplit[1]);
                break;
            }
            default: {
                break;
            }
        }
    }

    public static void outPutLog(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            android.util.Log.e(tag, msg);
        }
    }

    /**
     * 游戏登录之后，将token存下来供java层调用
     *
     * @param uid
     * @param token
     */
    public static final void onLoginSuccess(String uid, String token) {
        outPutLog("onLoginSuccess", "uid: " + uid + " token: " + token);
        LoginUtil.getInstance().login(uid, token);
    }

    /**
     * 退出登录后，清理token数据
     */
    public static final void cleanToken() {

        LoginUtil.getInstance().logOut();
    }

    /**
     * 存储配置信息
     *
     * @return
     */
    public static final void saveStringToPreference(String key, String value) {
        AppConfigUtils.saveStringToPrefenence(mContext, key, value);
    }

    public static final String getStringFromPreference(String key) {
        return AppConfigUtils.getStringFromPrefenence(mContext, key);
    }

    public static final void saveStringToKeyChain(String key, String value) {
        try {
            // 优先读取本地db
            String dbFile = LocalPackageBufDir.GetShareDBPath(mContext, "com.gold.id") + key;
//            outPutLog("----->Javautil dbFile: ", dbFile);
//            File dbf = new File(dbFile);
            if (!TextUtils.isEmpty(value)) {
                LocalPackageBufDir.writeFile(dbFile, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final String getStringFromKeyChain(String key) {
        String value = "";
        try {
            // 优先读取本地db
            String dbFile = LocalPackageBufDir.GetShareDBPath(mContext, "com.gold.id") + key;
//            outPutLog("----->Javautil dbFile: ", dbFile);
            File dbf = new File(dbFile);
            if (dbf.exists()) {
                value = LocalPackageBufDir.readFile(dbFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(TextUtils.isEmpty(value)){
            value = "";
        }
        outPutLog("----->getStringFromKeyChain ：", value);
        return value;
    }


    public static boolean isNetworkAvailable() {
        return NetworkUtil.isNetworkAvailable(mContext);
    }

    public static final void startWatchNetWorkState() {

        CommandObservable observable = ObservableManager.getInstance().getObservable(CommandObservable.class);
        if (null != observable) {
            observable.setCommand("message://command?cmd=start_watch_network");
            observable.notifyChanged();
        }
    }

    private final static boolean isAndroidEmulator() {
        String model = Build.MODEL;
        outPutLog(TAG, "model=" + model);
        String product = Build.PRODUCT;
        outPutLog(TAG, "product=" + product);
        boolean isEmulator = false;
        if (product != null) {
            isEmulator = product.equals("sdk") || product.contains("_sdk") || product.contains("sdk_");
        }
        outPutLog(TAG, "isEmulator=" + isEmulator);
        return isEmulator;
    }

    public static String getUniqueDeviceInfo() {
        String dVersion = AppConfigUtils.getStringFromPrefenence(mContext, "dversion_cache");
        String deviceInfo = AppConfigUtils.getStringFromPrefenence(mContext, "deviceinfo_cache");
        return deviceInfo + "&v" + dVersion;
    }


    public static void saveAccountsToNative(String accountStr) {
        Log.d("cocos2d-x", "saveAccountsToNative：" + accountStr);
        String pkgName = getPackageName();
        KeyChainTool<String> keyChainTool = new KeyChainTool<>();
        try {
            keyChainTool.keyChainSave(pkgName, accountStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String getAccountsFromNative() {
        String pkgName = getPackageName();
        KeyChainTool<String> keyChainTool = new KeyChainTool<>();
        String account = keyChainTool.keyChainLoad(pkgName);
        Log.d("cocos2d-x", "getAccountsFromNative：" + account);
        return account == null ? "" : account;
    }


    static String clipText = "";

    public static String getClipboardText() {
        clipText = "";
        ViewUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ClipboardManager manager = (ClipboardManager)
                        mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData data = null;
                if (manager != null) {
                    data = manager.getPrimaryClip();
                }
                if (data != null) {
                    ClipData.Item item = data.getItemAt(0);
                    if (item != null) {
                        CharSequence clipData = item.getText();
                        if (clipData != null) {
                            clipText = clipData.toString();
                        }
                    }
                }
            }
        });
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return clipText;
    }

    //单纯的打开微信
    public static void openWeChat() {
        AppUtil.startAppByPackageName(mContext, "com.tencent.mm");
    }

    //单纯的打开QQ
    public static void openQQ() {
        AppUtil.startAppByPackageName(mContext, "com.tencent.mobileqq");
    }


    public static void trackEvent(String event) {
        String track_event = String.format("%s", event);
        if (TextUtils.isEmpty(track_event)) {
            return;
        }
        AdjustEvent adjustEvent = new AdjustEvent(track_event);
        Adjust.trackEvent(adjustEvent);
    }

    public static void trackEvent(String action, String event) {
        String track_event = "";
        if (!TextUtils.isEmpty(action)) {
            track_event = String.format("%s", action);
        }
        if (!TextUtils.isEmpty(event)) {
            track_event = String.format("%s_%s", track_event, event);
        }
        if (TextUtils.isEmpty(track_event)) {
            return;
        }


    }

    public static void trackEvent(String category, String action, String event) {
        String track_event = "";
        if (!TextUtils.isEmpty(category)) {
            track_event = String.format("%s", category);
        }
        if (!TextUtils.isEmpty(action)) {
            track_event = String.format("%s_%s", track_event, action);
        }
        if (!TextUtils.isEmpty(event)) {
            track_event = String.format("%s_%s", track_event, event);
        }
        if (TextUtils.isEmpty(track_event)) {
            return;
        }
    }

    public static void trackDuration(String event) {
        if (TextUtils.isEmpty(event)) {
            return;
        }
        String[] strings = event.split("_");
        String lastString = "";
        if (strings.length > 0) {
            if (strings[strings.length - 1] != null) {
                lastString = strings[strings.length - 1];
            }
        }

    }

//    public static void shareImgToWc(String url, String isSaveImg) {
//        CommandMapObservable observable = ObservableManager.getInstance().getObservable(CommandMapObservable.class);
//        if (null != observable) {
//            CommandMapBean bean = new CommandMapBean();
//            bean.object = new HashMap<>();
//            bean.object.put("url", url);
//            bean.object.put("isSaveImg", isSaveImg);
//            bean.command = "message://share";
//            observable.setCommandMap(bean);
//            observable.notifyChanged();
//        }
//    }
//
//    public static void shareWcMoneyToWc(String url, String isSaveImg) {
//        CommandMapObservable observable = ObservableManager.getInstance().getObservable(CommandMapObservable.class);
//        if (null != observable) {
//            CommandMapBean bean = new CommandMapBean();
//            bean.object = new HashMap<>();
//            bean.object.put("url", url);
//            bean.object.put("isSaveImg", isSaveImg);
//            bean.command = "message://share_url_money";
//            observable.setCommandMap(bean);
//            observable.notifyChanged();
//        }
//    }

    public static void cachePromotionImage(String url, int type) {
        CommandMapObservable observable = ObservableManager.getInstance().getObservable(CommandMapObservable.class);
        if (null != observable) {
            CommandMapBean bean = new CommandMapBean();
            bean.object = new HashMap<>();
            bean.object.put("url", url);
            bean.object.put("type", type);
            bean.command = "message://cache_promotion_image";
            observable.setCommandMap(bean);
            observable.notifyChanged();
        }
    }

    public static void saveImageToGallery(String bgToUrl, String viewToUrl) {
        CommandMapObservable observable = ObservableManager.getInstance().getObservable(CommandMapObservable.class);
        if (null != observable) {
            CommandMapBean bean = new CommandMapBean();
            bean.object = new HashMap<>();
            bean.object.put("bgToUrl", bgToUrl);
            bean.object.put("viewToUrl", viewToUrl);
            bean.command = "message://save_image_to_gallery";
            observable.setCommandMap(bean);
            observable.notifyChanged();
        }
    }

    public static void openUrlByaBrowser(String url, String direction) {
        //EngineUtil.trackEvent(TrackUtil.TRACK_PRESS_CHARGE_INFASTPAY);
        CommandMapObservable observable = ObservableManager.getInstance().getObservable(CommandMapObservable.class);
        if (null != observable) {
            CommandMapBean bean = new CommandMapBean();
            bean.object = new HashMap<>();
            bean.object.put("url", url);
            bean.object.put("direction", direction);
            bean.command = "message://open_url_bya_browser";
            observable.setCommandMap(bean);
            observable.notifyChanged();
        }
    }

    public static void openUrlByPayBrowser(String url, String isLandscape, String requestCode) {
        CommandMapObservable observable = ObservableManager.getInstance().getObservable(CommandMapObservable.class);
        if (null != observable) {
            CommandMapBean bean = new CommandMapBean();
            bean.object = new HashMap<>();
            bean.object.put("url", url);
            bean.object.put("isLandscape", isLandscape);
            bean.object.put("requestCode", requestCode);
            bean.command = "message://open_url_by_pay_browser";
            observable.setCommandMap(bean);
            observable.notifyChanged();
        }
    }

    public static void getImageUrl(String url) {
        CommandMapObservable observable = ObservableManager.getInstance().getObservable(CommandMapObservable.class);
        if (null != observable) {
            CommandMapBean bean = new CommandMapBean();
            bean.object = new HashMap<>();
            bean.object.put("url", url);
            bean.command = "message://get_image_url";
            observable.setCommandMap(bean);
            observable.notifyChanged();
        }
    }

    public static void openUrlByH5Browser(String url) {
        CommandObservable observable = ObservableManager.getInstance().getObservable(CommandObservable.class);
        if (null != observable) {
            observable.setCommand("message://h5?url=" + url);
            observable.notifyChanged();
        }
    }

    public static void screenShotAction(String payType) {
        CommandObservable observable = ObservableManager.getInstance().getObservable(CommandObservable.class);
        if (null != observable) {
            observable.setCommand("message://screen_shot_action?payType=" + payType);
            observable.notifyChanged();
        }
    }

    public static void onMainJSStartRunning() {

    }

    public static void onStartLoadLaunchScene() {

    }

    public static void onFinishLoadLaunchScene() {
        CommandObservable observable = ObservableManager.getInstance().getObservable(CommandObservable.class);
        if (null != observable) {
            observable.setCommand("message://finish_load_launch");
            observable.notifyChanged();
        }
    }

    public static void closePush() {
        // 关闭push之前，先track之前已经推送的消息
        CommandObservable observable = ObservableManager.getInstance().getObservable(CommandObservable.class);
        if (null != observable) {
            observable.setCommand("message://report_notification_track");
            observable.notifyChanged();
        }
        if (null != mAbstractNotification) {
            mAbstractNotification.closePush();
        }
    }

    /**
     *更新新的版本的apk
     */
    public static void UpdateNewVersionApk(String version,String apkurl){
        CommandObservable observable = ObservableManager.getInstance().getObservable(CommandObservable.class);
        if (null != observable) {
            observable.setCommand("message://UpdateNewVersionApk?version="+version+"&apkurl="+apkurl);
            observable.notifyChanged();
        }
    }

    /**
     * app被杀死时(Android用户常见操作)，无法生效
     */
    public static void sendPushNewsWithValue(String notificationIdString, String triggerAtMillisStr, String title, String content, String pushInfoStr) {
        if (null != mAbstractNotification) {
            mAbstractNotification.sendPushNewsWithValue(notificationIdString, triggerAtMillisStr, title, content, pushInfoStr);
        }
    }


    /**
     * 获取当前内存
     * NULL "" "total" 为统计所有内存，其他为统计当前剩余内存
     * NAVIVE VERSION 14
     */
    public static String getMemory(String type) {
        return Device.getMemory(mContext, type);
    }
}
