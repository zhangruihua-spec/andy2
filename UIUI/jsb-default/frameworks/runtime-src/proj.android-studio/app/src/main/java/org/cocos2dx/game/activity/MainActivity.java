/****************************************************************************
 Copyright (c) 2015 Chukong Technologies Inc.

 http://www.cocos2d-x.org

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 ****************************************************************************/
package org.cocos2dx.game.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.support.v4.content.PermissionChecker;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import com.adjust.sdk.Adjust;
import com.arifian.networkstatelistener.lib.NetworkUtil;
import com.cocos2dx.game.BuildConfig;
import com.data.http.HttpDataLoadCallback;
import com.data.http.HttpPostDataTask;
import com.example.lib.QRCodeUtil.QRCodeUtil;
import com.keeplive.Config;
import com.keeplive.KeepLive;
import com.umeng.analytics.MobclickAgent;
import com.util.AppConfigUtils;
import com.util.AppUtil;
import com.util.Device;
import com.util.EngineUtil;
import com.util.FileUtil;
import com.util.MobileDeviceInfo;
import com.util.observable.CommandMapBean;
import com.util.observable.CommandMapObservable;
import com.util.observable.CommandObservable;
import com.util.observable.ViewUtil;
import com.util.observable.UploadAvatarObservable;

import org.cocos2dx.game.MyApplication;
import org.cocos2dx.game.util.ApkDowloadUtil;
import org.cocos2dx.game.util.ImageHelper;
import org.cocos2dx.game.service.KeepLiveService;
import org.cocos2dx.game.util.SDKWrapper;
import org.cocos2dx.game.notification.NotificationHelper;
import org.cocos2dx.game.notification.NotificationImpl;
import org.cocos2dx.lib.Cocos2dxGLSurfaceView;
import org.cocos2dx.lib.Cocos2dxJavascriptJavaBridge;
import org.cocos2dx.lib.Cocos2dxLocalStorage;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bridge.ObservableManager;

public class MainActivity extends BaseActivity implements Observer {

    private static final String TAG = MainActivity.class.getSimpleName();

    private boolean needWatchNetworkState = false;
    public static boolean isSuccess;


    private int nativeVersion = 1;

    private static Context mContext;

    public MainActivity() {
        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }

    private ImageView mImgLauncher;

    private ImageHelper mImageHelper = new ImageHelper(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
//        if (mFrameLayout != null) {
//            mImgLauncher = new ImageView(this);
//            mImgLauncher.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT));
//            mImgLauncher.setImageResource(R.drawable.splash_bg);
//            mImgLauncher.setScaleType(ImageView.ScaleType.FIT_XY);
//            mFrameLayout.addView(mImgLauncher);
//        }

//        FlurryUtil.initFlurry(this, getFlurryKey());
        initDebugMode();
        EngineUtil.init(this, new NotificationImpl(this));
        SDKWrapper.getInstance().init(this);
        initVersionCode(this);
        initChannel();
        Device.createDevicFromPhone(this).toDeviceInfoString();
        //trackDeviceInfo();

        //EngineUtil.trackEvent(EngineUtil.getPackageName(), EngineUtil.getChannel());
        //EngineUtil.trackEvent(EngineUtil.getPackageName());
        //EngineUtil.trackEvent(EngineUtil.getChannel());

        initKeepLiveService();

        UploadAvatarObservable avatarObservable = ObservableManager.getInstance().getObservable(UploadAvatarObservable.class);
        if (null != avatarObservable) {
            avatarObservable.addObserver(this);
        }

        CommandObservable commandObservable = ObservableManager.getInstance().getObservable(CommandObservable.class);
        if (null != commandObservable) {
            commandObservable.addObserver(this);
        }

        CommandMapObservable commandMapObservable = ObservableManager.getInstance().getObservable(CommandMapObservable.class);
        if (null != commandMapObservable) {
            commandMapObservable.addObserver(this);
        }

        checkStartUpSource(getIntent(), true);

    }


    private void initDebugMode() {
        Cocos2dxLocalStorage.init();
         
    }

    private void handleIntent() {
        
    }

    /**
     * 配置需要保活的服务，可配置多个。
     * 暂不提供Intent中传入参数的配置方式。
     */
    private void initKeepLiveService() {
        Config config = new Config.Builder()
                .appendService(KeepLiveService.class)
                .showLog(BuildConfig.DEBUG)
                .keepLiveInterval(15 * 60 * 1000)//每15分钟激活一次服务，除此外遇到其他系统事件也会激活
                .build();
        KeepLive.init(getApplicationContext())
                .config(config)
                .startDaemon();
    }

    @Override
    public Cocos2dxGLSurfaceView onCreateView() {
        Cocos2dxGLSurfaceView glSurfaceView = new Cocos2dxGLSurfaceView(this);
        // TestCpp should create stencil buffer
        glSurfaceView.setEGLConfigChooser(5, 6, 5, 0, 16, 8);
        SDKWrapper.getInstance().setGLSurfaceView(glSurfaceView, this);

        return glSurfaceView;
    }

    @Override
    protected void onResume() {
        Adjust.onResume();
        MobclickAgent.onResume(this); // 不能遗漏
        super.onResume();
        SDKWrapper.getInstance().onResume();

        //EngineUtil.trackEvent("native_resume_app");
    }

    @Override
    protected void onPause() {
        Adjust.onPause();
        MobclickAgent.onPause(this); // 不能遗漏
        super.onPause();
        SDKWrapper.getInstance().onPause();

        //EngineUtil.trackEvent("native_pause_app");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkStartUpSource(intent, false);
        SDKWrapper.getInstance().onNewIntent(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SDKWrapper.getInstance().onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SDKWrapper.getInstance().onStop();
        //EngineUtil.trackEvent("native_onstop_app");
    }

    @Override
    public void onBackPressed() {
        SDKWrapper.getInstance().onBackPressed();
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        SDKWrapper.getInstance().onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        SDKWrapper.getInstance().onRestoreInstanceState(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        SDKWrapper.getInstance().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        SDKWrapper.getInstance().onStart();
        super.onStart();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SDKWrapper.getInstance().onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE
                && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                performCrop(data.getData());
            }
        } else if (requestCode == CAMERA_CAPTURE
                && resultCode == Activity.RESULT_OK) {
            if (mPictureUri != null) {
                performCrop(mPictureUri);
            }
        } else if (requestCode == CROP_IMAGE
                && resultCode == Activity.RESULT_OK) {

        } else if (requestCode == SELECT_SCREENSHOT && resultCode == RESULT_OK) {
            mImageHelper.handleUpload(data.getData());
        }
    }

    @Override
    public void update(Observable o, Object arg) {

         if (o instanceof UploadAvatarObservable) {
            if (arg instanceof Integer) {
                modifyAvatar(((Integer) arg).intValue());
            }
        } else if (o instanceof CommandObservable) {
            String command = (String) arg;
            excuteCommand(command);
        } else if (o instanceof CommandMapObservable) {
            if (arg instanceof CommandMapBean) {

                CommandMapBean bean = (CommandMapBean) arg;
                excuteCommand(bean.command, bean.object);
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SDKWrapper.getInstance().onDestroy();
        ObservableManager.getInstance().clearAllObservable();
        //EngineUtil.trackEvent("native_exit_app");
        //EngineUtil.trackEvent(TrackUtil.TRACK_KILL_APP);
        EngineUtil.trackDuration("native_start_app");
    }


    private void initChannel() {
        String channel = AppConfigUtils.getChannel(this);
        if (TextUtils.isEmpty(channel)) {
            channel = AppUtil.getChannel(this);
           // Log.e("channel=",channel);
            AppConfigUtils.setChannel(this, channel);
        }
    }


    public static void initVersionCode(Context context) {

        int currentVCode = 0;
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            currentVCode = packInfo.versionCode;
        } catch (Exception e) {
        }

        int oldVCode = AppConfigUtils.getVersionCode(context);
        if (currentVCode > oldVCode) {
            //EngineUtil.trackEvent(String.format("native_update_%s_%s", oldVCode, currentVCode));
            AppConfigUtils.setVersionCode(context, currentVCode);
            AppConfigUtils.setChannel(context, null);
            onVersionUpgrade();
        }
    }

    private static void onVersionUpgrade() {

        Cocos2dxLocalStorage.init();
        Cocos2dxLocalStorage.removeItem("_res_search_path");
    }


    public static boolean hasNotchInScreen(Context context) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Log.e("test", "hasNotchInScreen ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e("test", "hasNotchInScreen NoSuchMethodException");
        } catch (Exception e) {
            Log.e("test", "hasNotchInScreen Exception");
        } finally {
            return ret;
        }
    }

    private void saveImageToGallery(String bgToUrl, String viewToUrl) {
        Intent intent = new Intent(getContext(), SaveImgAvtivity.class);
        intent.putExtra("bgToUrl", bgToUrl);
        intent.putExtra("viewToUrl", viewToUrl);
        getContext().startActivity(intent);
    }


    private void openUrlByaBrowser(String url, String direction) {

    }

    private void screenShotAction(String payType) {
        Intent intent = new Intent(getContext(), ScreenShotActivity.class);
        intent.putExtra("payType", payType);
        getContext().startActivity(intent);
    }

    private void openPayWebView(String url, String isLandscape, String requestCode) {

    }

    private void saveImage(String src) {
        mImageHelper.downloadImage2Photo(src);
    }

    /**
     * will read and post to server in js code.
     */
    private void trackDeviceInfo() {


        String dVersion = AppConfigUtils.getStringFromPrefenence(this, "dversion_cache");
        if (TextUtils.isEmpty(dVersion)) {
            dVersion = MobileDeviceInfo.getUniqueDeviceIDVersion();
            AppConfigUtils.saveStringToPrefenence(this, "dversion_cache", dVersion);
        }

        String deviceInfo = AppConfigUtils.getStringFromPrefenence(this, "deviceinfo_cache");
        if (TextUtils.isEmpty(deviceInfo)) {
            deviceInfo = MobileDeviceInfo.getUniqueDeviceID(this);
            AppConfigUtils.saveStringToPrefenence(this, "deviceinfo_cache", deviceInfo);
        }


    }

    private Bundle getBundle(String query) {
        Bundle bundle = new Bundle();
        try {
            if (!TextUtils.isEmpty(query)) {
                String[] params = query.split("&");
                if (params != null && params.length > 0) {
                    for (String kv : params) {
                        String[] data = kv.split("=");
                        if (data != null && data.length == 2) {
                            bundle.putString(data[0], data[1]);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bundle;
    }

    private void excuteCommand(String command) {
        Uri uri = Uri.parse(command);
        String query = uri.getQuery();
        String host = uri.getHost();
        Bundle bundle = getBundle(query);
//
        if (TextUtils.equals("url", host)) {
            final String url = command.replace("message://url?url=", "");
            ViewUtil.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //打开浏览器
                    Intent intent = new Intent(MainActivity.this,PayWebViewActivity.class);
                    intent.putExtra(PayWebViewActivity.PAYURL,url);
                    startActivity(intent);
                }
            });

        } else if (TextUtils.equals("command", host)) {
            String cmd = bundle.getString("cmd");
            if (TextUtils.equals("quit_game", cmd)) {
                exitGame();
            } else if (TextUtils.equals("paycallback", cmd)) { //在主线程中支付成功的回调
                final String bayStatus = bundle.getString("status");
//                if (TextUtils.equals("0", bayStatus)) {
//                   // BaseActivity.nativePaymentCallback(0);
//                } else {
//                    runOnGLThread(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            Cocos2dxJavascriptJavaBridge.evalString(String.format("cc.onBayCallbackJS(%s)", bayStatus));
//                        }
//                    });
//                }
                runOnGLThread(new Runnable() {
                    @Override
                    public void run() {
                        Cocos2dxJavascriptJavaBridge.evalString(String.format("cc.onBayCallbackJS(%s)", bayStatus));
                    }
                });
            } else if (TextUtils.equals("start_watch_network", cmd)) {

                //开始监听网络状态.
                needWatchNetworkState = true;
                //只有cocos请求过来，才往客户端赋值
                runOnGLThread(new Runnable() {
                    @Override
                    public void run() {
                        Cocos2dxJavascriptJavaBridge.evalString(String.format("cc.onNativeVersion(%s)", nativeVersion));
                    }
                });
            } else if (TextUtils.equals("network_state_changed", cmd)) {

                runOnGLThread(new Runnable() {
                    @Override
                    public void run() {
                        //网络状况发生改变
                        onNetworkStateChanged();
                    }
                });

            }
        } else if (TextUtils.equals("sdk", host)) {
            final String chargeInfo = command.replace("message://sdk?url=", "");
            ViewUtil.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startWxMiniProgram(chargeInfo);
                }
            });
        } else if (TextUtils.equals("h5", host)) {
            final String url = command.replace("message://h5?url=", "");
            ViewUtil.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        } else if (TextUtils.equals("native", host)) {
            String url = command.replace("message://native?url=", "");

        } else if (TextUtils.equals("service", host)) {
            try {
                // Pick Image From Gallery
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_SCREENSHOT);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        } else if (TextUtils.equals("screen_shot_action", host)) {
            String payType = command.replace("message://screen_shot_action?payType=", "");
            screenShotAction(payType);
        } else if (TextUtils.equals("finish_load_launch", host)) {
            onFinishLoadLaunchScene();
        } else if (TextUtils.equals("on_h5_back", host)) {
            final String requestCode = command.replace("message://on_h5_back?requestCode=", "");
            runOnGLThread(new Runnable() {
                @Override
                public void run() {
                    Cocos2dxJavascriptJavaBridge.evalString(String.format("cc.onH5Back('%s')", requestCode));
                }
            });
        } else if (TextUtils.equals("on_close_webview", host)) {
            runOnGLThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("MainActivity", "onCloseWebview");
                    Cocos2dxJavascriptJavaBridge.evalString(String.format("cc.onCloseWebview()"));
                }
            });
        } else if (TextUtils.equals("report_notification_track", host)) {
            NotificationHelper.reportTrack(MainActivity.this);

        } else if (TextUtils.equals("dispose_push_notification", host)) {
            runOnGLThread(new Runnable() {
                @Override
                public void run() {
                    Cocos2dxJavascriptJavaBridge.evalString("cc.onNativeDisposePushNotification()");
                }
            });
        }else if(TextUtils.equals("UpdateNewVersionApk",host)){
            final String version = bundle.getString("version");
            final String apkurl = bundle.getString("apkurl");
            Log.e("---","beforeupdateNewApk");
            runOnGLThread(new Runnable() {
                @Override
                public void run() {
                    ApkDowloadUtil.getInstance().updateNewApk(version,apkurl);
                }
            });

        }else if(TextUtils.equals("updateHotProgress",host)){
            final String progress = bundle.getString("progress");
            final String max = bundle.getString("max");
            runOnGLThread(new Runnable() {
                @Override
                public void run() {
                    //Log.e("---","progress="+progress+"==max="+max);
                    String formatestring =    String.format("cc.updateHotProgress(%s,%s)", String.valueOf(progress),String.valueOf(max));
                    //Log.e("---",formatestring);
                    Cocos2dxJavascriptJavaBridge.evalString(formatestring);
                }
            });


        }else if(TextUtils.equals("InstallNewVersionApk",host)){
            final String path = bundle.getString("path");
            runOnGLThread(new Runnable() {
                @Override
                public void run() {
                    installApk(path);
                }
            });

        }
    }

    private void excuteCommand(String command, Map object) {
        Uri uri = Uri.parse(command);
        String host = uri.getHost();

        if (TextUtils.equals("cache_promotion_image", host)) {

            Object url = object.get("url");
            Object type = object.get("type");
            if (url instanceof String && type instanceof Integer) {
                cachePromotionImage((String) url, (Integer) type);
            }
        } else if (TextUtils.equals("save_image_to_gallery", host)) {

            Object bgToUrl = object.get("bgToUrl");
            Object viewToUrl = object.get("viewToUrl");
            if (bgToUrl instanceof String && viewToUrl instanceof String) {
                saveImageToGallery((String) bgToUrl, (String) viewToUrl);
            }
        } else if (TextUtils.equals("open_url_bya_browser", host)) {

            Object url = object.get("url");
            Object direction = object.get("direction");
            if (url instanceof String && direction instanceof String) {
                openUrlByaBrowser((String) url, (String) direction);
            }
        } else if (TextUtils.equals("share_url_money", host)) {

            Object url = object.get("url");
            Object isSaveImg = object.get("isSaveImg");
            if (url instanceof String && isSaveImg instanceof String) {
                shareWcMoneyToWc((String) url, (String) isSaveImg);
            }
        } else if (TextUtils.equals("open_url_by_pay_browser", host)) {

            Object url = object.get("url");
            Object isLandscape = object.get("isLandscape");
            Object requestCode = object.get("requestCode");
            if (url instanceof String && isLandscape instanceof String) {
                openPayWebView((String) url, (String) isLandscape, (String) requestCode);
            }
        } else if (TextUtils.equals("get_image_url", host)) {
            Object url = object.get("url");
            if (url instanceof String) {
                saveImage((String) url);
            }
        } else if (TextUtils.equals("share", host)) {
            Object url = object.get("url");
            Object isSaveImg = object.get("isSaveImg");
            if (url instanceof String && isSaveImg instanceof String) {
                shareImgToWc((String) url, (String) isSaveImg);
            }
        }
    }

    public void onFinishLoadLaunchScene() {
        if (mImgLauncher == null || mFrameLayout == null)
            return;
        ViewUtil.runOnUiThreadDelay(new Runnable() {
            @Override
            public void run() {
                mFrameLayout.removeView(mImgLauncher);
                mImgLauncher = null;
            }
        }, 500L);

        handleIntent();

    }



    private void startWxMiniProgram(String chargeInfo) {

    }

    private void onNetworkStateChanged() {

        if (!needWatchNetworkState) {
            return;
        }

        //1 已连接, 2 连接中, 3 未连接
        int netWorkState = 1;
        if (NetworkUtil.isConnected(this)) {

            netWorkState = 1;
//            Toast.makeText(this, "网络已经连接", Toast.LENGTH_LONG).show();

        } else if (NetworkUtil.isConnecting(this)) {
            netWorkState = 2;
//            Toast.makeText(this, "网络连接中......", Toast.LENGTH_LONG).show();

        } else {
            netWorkState = 3;
//            Toast.makeText(this, "网络已断开连接", Toast.LENGTH_LONG).show();

        }
        Cocos2dxJavascriptJavaBridge.evalString(String.format("cc.netWorkStateChanged(%s)", netWorkState));
    }



    //////************                 ************///////
    private static final int TARGE_BITMAP_SIZE = 120;

    private final static int SELECT_IMAGE = 100;

    private final static int CAMERA_CAPTURE = 200;

    private final static int CROP_IMAGE = 300;

    private final static int SELECT_SCREENSHOT = 400;

    private Uri mPictureUri;

    private void tackPicture() {
        mPictureUri = null;
        // use standard intent to capture an image
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // test intent for save
        if (captureIntent.resolveActivity(getPackageManager()) != null) {
            // create a file to save picture
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri());
            // we will handle the returned data in onActivityResult
            startActivityForResult(captureIntent, CAMERA_CAPTURE);
        }
    }

    private Uri generateFileUri() {
        // set package-specific directories to save picture
        File dirFile = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File tempFile = new File(dirFile, String.valueOf(System.currentTimeMillis()) + ".jpg");
        FileUtil.ensureFile(tempFile);
        mPictureUri = Uri.fromFile(tempFile);
        return mPictureUri;
    }

    private void selectPicture() {
        try {
            mPictureUri = null;
            // Pick Image From Gallery
            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, SELECT_IMAGE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void performCrop(Uri contentUri) {
        try {
            // Start Crop Activity
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(contentUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 280);
            cropIntent.putExtra("outputY", 280);

            // retrieve data on return
//            cropIntent.putExtra("return-data", true);
            // return to our file
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri());
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_IMAGE);
        } catch (ActivityNotFoundException e) {
            // respond to users whose devices do not support the crop action
            e.printStackTrace();
        }
    }

    private void modifyAvatar(int type) {
        if (0 == type) {
            selectPicture();
        } else {
            tackPicture();
        }
    }


    private void updateAvatar(String token, final String host) {

    }

    private void updateUserInfo(String key, String value) {
        String url = Uri.parse(AppConfigUtils.getStringFromPrefenence(this, "_common_host_server") + "/api/change_user_info")
                .buildUpon().build().toString();
        new HttpPostDataTask(this, url, String.format("%s=%s", key, value), new HttpDataLoadCallback() {
            @Override
            public void onDataLoadStart() {
                Toast.makeText(MainActivity.this, "正在设置头像...", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDataLoadComplete(int status, String msg, JSONObject object) {

                if (null != object) {
                    android.util.Log.e("ChangeUserInfo", object.toString());
                }
                Toast.makeText(MainActivity.this, "新头像设置成功...", Toast.LENGTH_LONG).show();
                runOnGLThread(new Runnable() {
                    @Override
                    public void run() {

                        Cocos2dxJavascriptJavaBridge.evalString("cc.uploadComplete()");
                    }
                });
            }
        }).execute();
    }


    //---------------------------------------------- 选择截图上传 -------------------------------------------------


    //---------------------------------------------- 合成带二维码的图片分享到微信 -------------------------------------------------
    private void shareImgToWc(String url, String isSaveImg) {
        generateQrImageAndShare(1, url, 212, 468, 1032, isSaveImg);
    }

    private void shareWcMoneyToWc(String url, String isSaveImg) {

        generateQrImageAndShare(2, url, 228, 245, 936, isSaveImg);
    }



    //缓存配置的宣传图片
    private void cachePromotionImage(final String urlStr, final int type) {
        String bundleId = EngineUtil.getPackageName();
        String channel = EngineUtil.getChannel();
        String imageName = null;
        //先取出图片名称
        String urlImageName = urlStr.substring(urlStr.lastIndexOf("/") + 1);
        urlImageName = urlImageName.replace(".jpg", "");
        urlImageName = urlImageName.replace(".png", "");

        if (type == 1) {
            imageName = String.format("%s_%s_%s.jpg", bundleId, channel, urlImageName);
        } else if (type == 2) {
            imageName = String.format("%s_%s_%s.jpg", bundleId, channel, urlImageName);
        }

        final String imageFileName = imageName;
        if (TextUtils.isEmpty(urlStr) || TextUtils.isEmpty(imageFileName)) {
            return;
        }

        //保存图片名称
        EngineUtil.saveStringToPreference(String.format("%s_%s_%s", bundleId, channel, type), imageFileName);

        ExecutorService es = Executors.newSingleThreadScheduledExecutor();
        es.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //传入需要的网址
                    URL url = new URL(urlStr);
                    //打开网络连接
                    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    //设置网络延时
                    connection.setConnectTimeout(5 * 1000);
                    //设置获取方式
                    connection.setRequestMethod("GET");
                    //转变为输入流
                    InputStream inputStream = connection.getInputStream();
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        //将输入流转变为字节流
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        //每次读取以一字节读取
                        byte[] buffer = new byte[1024];
                        //初始化读取字节的长度
                        int len;
                        //设置len
                        while ((len = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, len);
                        }
                        outputStream.close();
                        inputStream.close();

                        byte[] byteArray = outputStream.toByteArray();
                        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                        //保存图片
                        File file = new File(mContext.getFilesDir(), imageFileName);
                        FileOutputStream fos = new FileOutputStream(file);
                        bmp.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                        fos.flush();
                        fos.close();
                        Log.d("cocos2d-x", "图片缓存成功：" + file.getAbsolutePath());
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    File file = new File(mContext.getFilesDir(), imageFileName);
                    file.delete();
                }
            }
        });

    }

    private Bitmap getCachePromotionImage(int type) {
        String bundleId = EngineUtil.getPackageName();
        String channel = EngineUtil.getChannel();

        String imageName = EngineUtil.getStringFromPreference(String.format("%s_%s_%s", bundleId, channel, type));

        if (imageName != null) {
            File file = new File(mContext.getFilesDir(), imageName);
            Bitmap bitmap = null;
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            }
            return bitmap;
        }

        return null;
    }

    /**
     * @param type   1表示分享宣传图，2表示分享收益图
     * @param url    二维码链接
     * @param qrSize 二维码尺寸
     * @param x      二维码坐标x
     * @param y      二维码坐标y
     */
    private void generateQrImageAndShare(final int type, final String url, final int qrSize, final int x, final int y, final String isSaveImg) {
        ExecutorService es = Executors.newSingleThreadScheduledExecutor();
        es.execute(new Runnable() {
            @SuppressLint("ResourceType")
            @Override
            public void run() {
                Log.d("cocos2d-x", "开始生成二维码图片分享");

                int permission = PermissionChecker.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if ("true".equals(isSaveImg) && permission != PackageManager.PERMISSION_GRANTED) {
                    ViewUtil.getUIThreadHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "保存图片功能被禁用，请打开手机权限或截屏保存", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }

                Bitmap baseBitmap = getCachePromotionImage(type);
                boolean localRes = true;
                if (baseBitmap == null) {
                    Log.d("cocos2d-x", "本地无缓存图片");
                    if ((isSaveImg).equals("false")) {
                        if (EngineUtil.hasWechat()) {
                            ViewUtil.getUIThreadHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    //Toast.makeText(mContext, "已复制链接，即将打开微信", Toast.LENGTH_SHORT).show();
                                }
                            });
                            EngineUtil.copyText(url);
                            EngineUtil.openWeChat();
                        } else {
                            ViewUtil.getUIThreadHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                 //   Toast.makeText(mContext, "请先安装微信", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        return;
                    }

                    ViewUtil.getUIThreadHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "保存图片失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                } else {
                    Log.d("cocos2d-x", "使用缓存图片：" + type);
                    localRes = false;
                }

                Bitmap qrBitmap = QRCodeUtil.createQRCodeBitmap(qrSize, url);
                Log.d("cocos2d-x", "qrBitmap = " + qrBitmap.getWidth() + "， " + qrBitmap.getHeight());



                BitmapDrawable[] array = new BitmapDrawable[2];
                array[0] = new BitmapDrawable(mContext.getResources(), baseBitmap);
                array[1] = new BitmapDrawable(mContext.getResources(), qrBitmap);


                Log.d("cocos2d-x", "drawable1：" + array[0].getIntrinsicWidth() + "," + array[0].getIntrinsicHeight());
                Log.d("cocos2d-x", "drawable2：" + array[1].getIntrinsicWidth() + "," + array[1].getIntrinsicHeight());

                LayerDrawable la = new LayerDrawable(array);
                array[1].setGravity(Gravity.LEFT | Gravity.TOP);
                la.setLayerInset(0, 0, 0, 0, 0);
                la.setLayerInset(1, x, y, 0, 0);

                Log.d("cocos2d-x", "LayerDrawable宽高：" + la.getIntrinsicWidth() + "," + la.getIntrinsicHeight());
                Bitmap tbitmap = Bitmap.createBitmap(la.getIntrinsicWidth(), la.getIntrinsicHeight(), Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(tbitmap);
                la.setBounds(0, 0, la.getIntrinsicWidth(), la.getIntrinsicHeight());
                la.draw(canvas);
                if ((isSaveImg).equals("true")) {
                    saveImage(tbitmap);
                } else {
                    shareToWc(tbitmap);
                }
            }
        });
    }

    //微信分享部分
    private void shareToWc(Bitmap bitmap) {


    }

    //保存文件到指定路径
    public void saveImage(Bitmap bmp) {
        String fileName = System.currentTimeMillis() + ".jpg";
        String path = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                + "/" + fileName;
        File extDownloadFolderPath = new java.io.File(path);
        File file = extDownloadFolderPath;//new File(appDir, fileName);
        try {
            FileOutputStream fos;
            fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();
            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            if (isSuccess) {
                showLoading("已保存图片至相册");
            } else {
                showLoading("保存图片失败，请稍后重试。");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //精确计算文本宽度
    private int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }


    private boolean canFinish = false;

    private void exitGame() {

        //EngineUtil.trackEvent("native_on_keyback_click");
        if (canFinish) {

            //EngineUtil.trackEvent("native_exit_app");
            //EngineUtil.trackEvent(TrackUtil.TRACK_QUIT_APP);
            EngineUtil.trackDuration("native_start_app");

            mHandler.sendEmptyMessageDelayed(MSG_QUIT_FINAL, 200);

        } else {

            canFinish = true;
            mHandler.sendEmptyMessage(MSG_QUIT_TOAST);

        }
        mHandler.sendEmptyMessageDelayed(MSG_QUIT_FLAT, 1000);
    }

    private void killAppProcess() {
        android.os.Process.killProcess(android.os.Process.myPid());    //获取PID
        System.exit(0);   //常规java、c#的标准退出法，返回值为0代表正常退出
    }

    private static final int MSG_QUIT_FLAT = 0x001;
    private static final int MSG_QUIT_TOAST = 0x002;
    private static final int MSG_QUIT_FINAL = 0x003;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_QUIT_TOAST:
                    Toast.makeText(MainActivity.this, "Press again to exit the game", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_QUIT_FLAT:
                    canFinish = false;
                    break;
                case MSG_QUIT_FINAL:
                    killAppProcess();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private String getFlurryKey() {

        return "";
    }

    private void checkStartUpSource(Intent intent, boolean fromCreate) {
        if (null == intent) {
            return;
        }

        Log.i("Notification", fromCreate ? "startNotification===create" : "startNotification===newIntent");

        final String pushInfoStr = intent.getStringExtra("pushInfoStr");
        Log.i("Notification", "startNotification===pushInfoStr=" + pushInfoStr);
        if (!TextUtils.isEmpty(pushInfoStr)) {
            NotificationHelper.saveTrackClick(MainActivity.getContext(), pushInfoStr);
        }
    }
    /**
     * 安装apk
     * @param path
     */
    private void installApk(String path){
        File apkFile = new File(path);
        if (!apkFile.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
//      安装完成后，启动app（源码中少了这句话）

        if (null != apkFile) {
            try {
                //兼容7.0
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri contentUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".fileProvider", apkFile);
                    intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                    //兼容8.0
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        boolean hasInstallPermission = mContext.getPackageManager().canRequestPackageInstalls();
//                        if (!hasInstallPermission) {
//                            startInstallPermissionSettingActivity();
//                            return;
//                        }
//                    }
                } else {
                    intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                if (mContext.getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
                    mContext.startActivity(intent);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

    }

    private void startInstallPermissionSettingActivity() {
        //注意这个是8.0新API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }
}
