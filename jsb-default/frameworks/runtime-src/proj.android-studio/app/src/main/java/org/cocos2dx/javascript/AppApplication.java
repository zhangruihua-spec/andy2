package org.cocos2dx.javascript;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustAttribution;
import com.adjust.sdk.AdjustConfig;
import java.lang.reflect.Field;

import com.adjust.sdk.AdjustFactory;
import com.adjust.sdk.GooglePlayServicesClient;
import com.adjust.sdk.IActivityHandler;
import com.adjust.sdk.LogLevel;
import com.adjust.sdk.OnAttributionChangedListener;
import com.adjust.sdk.Util;
import com.fireball.cocos_demo.BuildConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import org.cocos2dx.bridge.MessageManager;
import org.cocos2dx.observable.CommonCmd;
import org.cocos2dx.util.AppConfigUtils;


public class AppApplication extends MultiDexApplication {
    private AdjustConfig adjustconfig;
    @Override
    public void onCreate() {
        super.onCreate();
        String appToken = BuildConfig.adjustToken;
        //String environment = AdjustConfig.ENVIRONMENT_SANDBOX;
        String environment = AdjustConfig.ENVIRONMENT_PRODUCTION;  //线上
        adjustconfig = new AdjustConfig(this, appToken, environment);
        adjustconfig.setLogLevel(LogLevel.VERBOSE); // enable all logs
        adjustconfig.setDefaultTracker(BuildConfig.apkChannel);
        adjustconfig.setOnAttributionChangedListener(new OnAttributionChangedListener() {
            @Override
            public void onAttributionChanged(AdjustAttribution attribution) {
                try{
                    Log.e("AdjustAttributionadid=",attribution.adid+"");
                    String adid = attribution.adid;
                    if(!TextUtils.isEmpty(adid)){
                        saveAdjustAdid(adid);
                    }else{
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Log.e("AdjustADid=",Adjust.getAdid()+"");
                                String adjustadid =    Adjust.getAdid();
                                if(!TextUtils.isEmpty(adjustadid)){
                                    saveAdjustAdid(adjustadid);
                                }
                            }
                        },5000);
                    }
                }catch (Exception ption){

                }
            }
        });
        Adjust.onCreate(adjustconfig);

//        IActivityHandler adjustactivityhandler =  AdjustFactory.getActivityHandler(adjustconfig);
//        //adjustactivityhandler.getDeviceInfo();
////        com.adjust.sdk.DeviceInfo deviceInfo ;
//        try{
//            String calssName = "com.adjust.sdk.DeviceInfo";
//            Class clazz = Class.forName(calssName);
//            //Object obj = clazz.newInstance();
//// 获取对象属性
//            Field[] fields = clazz.getDeclaredFields();
//            for(Field field: fields){
//                String name = field.getName();
//                field.setAccessible(true); // 私有属性必须设置访问权限
//                Object resultValue = field.get(adjustactivityhandler.getDeviceInfo());
//                // 这里可以编写你的业务代码
//                Log.e("adjust config======",name + ": " + resultValue);
//            }
//        }catch (Exception ption){
//            Log.e("ption",ption.toString());
//        }

          new Thread(new Runnable() {
              @Override
              public void run() {
                  reloadPlayIds(AppApplication.this);
                  Log.e("playAdId=",playAdId+"");
                  AppConfigUtils.saveStringToPrefenence(AppApplication.this,AppConfigUtils.ADJUST_GPS_ADID,playAdId+"");
                  CommonCmd observable = MessageManager.getInstance().getObservable(CommonCmd.class);
                  if (null != observable) {
                      observable.setCommand("message://adjustinfo");
                      observable.notifyChanged();
                  }
              }
          }).start();

       String androidId = Util.getAndroidId(this);
        AppConfigUtils.saveStringToPrefenence(AppApplication.this,AppConfigUtils.ADJUST_ANDROIDID,androidId);

       String macAddress = Util.getMacAddress(this);
       String macShortMd5 = getMacShortMd5(macAddress);
        AppConfigUtils.saveStringToPrefenence(AppApplication.this,AppConfigUtils.ADJUST_MACSHORTMD5,macShortMd5);


        registerActivityLifecycleCallbacks(new AdjustLifecycleCallbacks());


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
    private String getMacShortMd5(String macAddress) {
        if (macAddress == null) {
            return null;
        }
        String macShort = macAddress.replaceAll(":", "");
        String macShortMd5 = Util.md5(macShort);

        return macShortMd5;
    }
    private void saveAdjustAdid(String adid) {
        AppConfigUtils.saveStringToPrefenence(AppApplication.this,AppConfigUtils.ADJUST_ADID,adid);
        CommonCmd observable = MessageManager.getInstance().getObservable(CommonCmd.class);
        if (null != observable) {
            observable.setCommand("message://adjustinfo");
            observable.notifyChanged();
        }
    }

    private  static final class AdjustLifecycleCallbacks implements ActivityLifecycleCallbacks {
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

    String playAdId;
    String playAdIdSource;
    void reloadPlayIds(Context context) {
        playAdIdSource = null;
        for (int i = 0; i < 3; i += 1) {
            try {
                GooglePlayServicesClient.GooglePlayServicesInfo gpsInfo = GooglePlayServicesClient.getGooglePlayServicesInfo(context);
                playAdId = gpsInfo.getGpsAdid();
                if (playAdId != null) {
                    playAdIdSource = "service";
                    break;
                }
            } catch (Exception e) {}
            playAdId = Util.getPlayAdId(context);
            if (playAdId != null) {
                playAdIdSource = "library";
                break;
            }
        }
    }
}
