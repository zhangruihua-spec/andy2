package com.keeplive.test;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.keeplive.util.LogUtil;

public class MyAlarmService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.i("Start MyAlarmService...");
        stopSelf();
        return START_STICKY;
    }
}
