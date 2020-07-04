package com.keeplive.service.screen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;

public class ScreenListener {

    private Context mContext;
    private ScreenBroadcastReceiver mScreenReceiver;
    private ScreenStateListener mScreenStateListener;

    /**
     * 调用接口：返回给调用者屏幕状态信息
     */
    public interface ScreenStateListener {
        public void onScreenOn();

        public void onScreenOff();

        public void onUserPresent();
    }

    public ScreenListener(Context context) {
        mContext = context;
        mScreenReceiver = new ScreenBroadcastReceiver();
    }

    /**
     * 开始监听screen状态
     */
    public void begin(ScreenStateListener listener) {
        mScreenStateListener = listener;
        registerListener();//启动screen状态广播接收器
        getScreenState();//获取screen状态
    }

    public void end() {
        mScreenStateListener = null;
        unregisterListener();
    }

    /**
     * 获取screen状态
     */
    private void getScreenState() {
        PowerManager manager = (PowerManager) mContext
                .getSystemService(Context.POWER_SERVICE);
        if (manager.isScreenOn()) {
            if (mScreenStateListener != null) {
                mScreenStateListener.onScreenOn();
            }
        } else {
            if (mScreenStateListener != null) {
                mScreenStateListener.onScreenOff();
            }
        }
    }

    /**
     * 启动screen状态广播接收器
     */
    private void registerListener() {
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            mContext.registerReceiver(mScreenReceiver, filter);//动态注册广播
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止screen状态监听
     */
    public void unregisterListener() {
        try {
            mContext.unregisterReceiver(mScreenReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * screen状态广播接收者
     */
    private class ScreenBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {//接受广播
            String action = intent.getAction();
            if (null != mScreenStateListener) {
                if (Intent.ACTION_SCREEN_ON.equals(action)) {
                    mScreenStateListener.onScreenOn();//调用接口方法：开屏
                } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                    mScreenStateListener.onScreenOff();//调用接口方法：锁屏
                } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                    mScreenStateListener.onUserPresent();//调用接口方法：解锁
                }
            }
        }
    }
}