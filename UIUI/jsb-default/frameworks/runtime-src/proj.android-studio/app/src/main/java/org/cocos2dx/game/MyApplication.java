package org.cocos2dx.game;

import android.app.Activity;
import android.app.Application;
import android.media.projection.MediaProjection;
import android.os.Bundle;
import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.LogLevel;
import com.cocos2dx.game.BuildConfig;
import com.tencent.smtt.sdk.QbSdk;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;


public class MyApplication extends Application {
    public static MediaProjection mediaProjection;
    public static long time;
    private static Application sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        String appToken = BuildConfig.adjustToken;
        //String environment = AdjustConfig.ENVIRONMENT_SANDBOX;
        String environment = AdjustConfig.ENVIRONMENT_PRODUCTION;  //线上
        AdjustConfig config = new AdjustConfig(this, appToken, environment);
        config.setLogLevel(LogLevel.VERBOSE); // enable all logs
        config.setDefaultTracker(BuildConfig.apkChannel);
        Adjust.onCreate(config);
        registerActivityLifecycleCallbacks(new AdjustLifecycleCallbacks());
        QbSdk.initX5Environment(sInstance,null);
        /**
         * 注意: 即使您已经在AndroidManifest.xml中配置过appkey和channel值，也需要在App代码中调
         * 用初始化接口（如需要使用AndroidManifest.xml中配置好的appkey和channel值，
         * UMConfigure.init调用中appkey和channel参数请置为null）。
         */
        UMConfigure.init(this,BuildConfig.Uappkey,BuildConfig.apkChannel,UMConfigure.DEVICE_TYPE_PHONE,null);
        // 选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        UMConfigure.setLogEnabled(BuildConfig.DEBUG);
    }

    public static Application getInstance() {
        return sInstance;
    }

    private static final class AdjustLifecycleCallbacks implements ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            Adjust.onResume();
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Adjust.onPause();
        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }

        //...
    }
}

