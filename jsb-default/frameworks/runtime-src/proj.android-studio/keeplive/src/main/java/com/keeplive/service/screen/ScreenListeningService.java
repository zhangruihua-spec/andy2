package com.keeplive.service.screen;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ScreenListeningService extends Service {

    private ScreenListener mScreenListener = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mScreenListener = new ScreenListener(this);
        mScreenListener.begin(listener);//开始监听screen状态
    }

    /**
     * ScreenListener.ScreenStateListener监听接口方法实现
     */
    private ScreenListener.ScreenStateListener listener = new ScreenListener.ScreenStateListener() {
        @Override
        public void onScreenOn() {
            //开屏---finish这个一个像素的Activity
            KeepLiveActivityManager.getInstance(ScreenListeningService.this).finishKeepLiveActivity();
        }

        @Override
        public void onScreenOff() {
            //锁屏---启动一个像素的Activity
            KeepLiveActivityManager.getInstance(ScreenListeningService.this).startKeepLiveActivity();

            //startActivity(intent);
        }

        @Override
        public void onUserPresent() {

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mScreenListener) {
            mScreenListener.unregisterListener();
        }
    }
}
