package org.cocos2dx.game.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.keeplive.KeepLive;
import com.util.SysUtil;
import com.util.observable.CommandObservable;

import org.cocos2dx.game.activity.MainActivity;
import org.cocos2dx.game.notification.NotificationUtil;

import bridge.ObservableManager;

public class KeepLiveService extends Service {

    private static final String TAG = KeepLive.class.getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "Reschedule push alarm clock");
        if (SysUtil.isForeground(getBaseContext(), MainActivity.class)) {
            disposePushNotification();
        } else {
            NotificationUtil.init(this).sendLocalPushNotification();
        }
        return super.onStartCommand(intent, flags, startId);
    }


    private void disposePushNotification() {
        CommandObservable observable = ObservableManager.getInstance().getObservable(CommandObservable.class);
        if (null != observable) {
            observable.setCommand("message://dispose_push_notification");
            observable.notifyChanged();
        }
    }


}
