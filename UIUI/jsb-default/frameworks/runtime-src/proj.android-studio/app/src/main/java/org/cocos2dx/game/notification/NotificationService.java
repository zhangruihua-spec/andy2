package org.cocos2dx.game.notification;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * 通知服务
 */
public class NotificationService extends IntentService {

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String title = intent.getExtras().getString("title");
        String content = intent.getExtras().getString("content");
        long tag = intent.getExtras().getLong("tag");
        int notificationId = intent.getExtras().getInt("notificationId");
        String pushInfoStr = intent.getExtras().getString("pushInfoStr");

        Log.i("Notification", "NotificationService==onHandleIntent >" + tag + "< >" + content + "<");

        NotificationUtil.init(this).showNotification(notificationId, title, content, pushInfoStr);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}

