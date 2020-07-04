package org.cocos2dx.game.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.cocos2dx.game.R;
import com.util.AppConfigUtils;
import com.util.EngineUtil;

import org.cocos2dx.game.activity.MainActivity;
import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationUtil {

    private static final String TAG = NotificationUtil.class.getSimpleName();
    private Context mContext;
    private static NotificationUtil sInstance;

    public static NotificationUtil init(Context context) {
        if (null == sInstance) {
            sInstance = new NotificationUtil(context);
        }
        return sInstance;
    }

    private NotificationUtil(Context context) {
        this.mContext = context;
    }

    public void showNotification(int notificationId, String title, String contentText, final String pushInfoStr) {

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra("pushInfoStr", pushInfoStr);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Notification notification = null;
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.i("Notification", "shownotification: id=" + notificationId);
        NotificationHelper.saveTrackPushSuccessd(mContext, pushInfoStr);

        if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O && Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            notification = new NotificationCompat.Builder(mContext)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentText(contentText)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentIntent(pendingIntent).build();

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN &&
                Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            notification = new NotificationCompat.Builder(mContext)
                    .setContentTitle(title)
                    .setContentText(contentText)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setWhen(System.currentTimeMillis())
                    .build();

        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String CHANNEL_ID = "01";
            String name = "公告";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);

            notification = new Notification.Builder(mContext, CHANNEL_ID)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(contentText)
                    .setContentIntent(pendingIntent)
                    .build();
        }
        notificationManager.notify(notificationId, notification);
    }


    /**
     * 发送缓存在安卓端的推送消息（在后台无法调用Cocos端推送时使用）
     */
    public void sendLocalPushNotification() {
        PushDataList pushDataList = getLocalPushDataList(mContext);
        if (null == pushDataList) {
            return;
        }
        EngineUtil.init(mContext, new NotificationImpl(mContext));
        EngineUtil.closePush();

        for (int i = 0; i < pushDataList.getList().size(); i++) {
            PushData pushData = pushDataList.getList().get(i);
            //消息删除了，不再推送
            if (pushData.isDeleted()) {
                continue;
            }
            long nowTime = System.currentTimeMillis() / 1000; //换算为秒
            //消息过期了，不再推送
            if (pushData.getExpireAt() > 0 && nowTime > pushData.getExpireAt()) {
                continue;
            }
            for (int j = 0; j < pushData.getPopTypes().size(); j++) {
                PushData.PopType popType = pushData.getPopTypes().get(j);
                int type = popType.getType();

                if (type == PushData.PopType.TYPE_ONE_TIME) { //创建N个固定时间点的推送
                    if (null == popType.getLongValues()) {
                        continue;
                    }
                    List<Long> popTimes = popType.getLongValues();
                    for (int k = 0; k < popTimes.size(); k++) {
                        long popTime = popTimes.get(k);
                        if (nowTime < popTime) {
                            String pushInfo = pushData.getId() + "_" + type + "_" + popTime;

                            EngineUtil.sendPushNewsWithValue(
                                    String.valueOf(buildLocalPushId(pushData.getId(), type, popTime)),
                                    String.valueOf(popTime),
                                    pushData.getTitle(),
                                    pushData.getContent(),
                                    pushInfo
                            );
                        }
                    }
                } else if (type == PushData.PopType.TYPE_REPEAT) {// 每N天推送一次
                    if (null == popType.getLongValues() || popType.getLongValues().size() == 0) {
                        continue;
                    }
                    long popDays = popType.getLongValues().get(0);
                    long popTime = nowTime + (popDays * 24 * 60 * 60);
                    String pushInfo = pushData.getId() + "_" + type + "_" + popDays;

                    EngineUtil.sendPushNewsWithValue(
                            String.valueOf(buildLocalPushId(pushData.getId(), type, popDays)),
                            String.valueOf(popTime),
                            pushData.getTitle(),
                            pushData.getContent(),
                            pushInfo
                    );

                }
            }

        }

    }

    public static int buildLocalPushId(String pushId, int type, long value) {
        String key = pushId + "_" + type + "_" + value;
        return key.hashCode();
    }


    public static PushDataList getLocalPushDataList(Context context) {
        String localData = AppConfigUtils.getPushNotificationData(context);//IoUtil.loadFromAssets(context, "push_json.txt");
        //Log.d("Notification", "Send local push notification: " + localData);
        if (TextUtils.isEmpty(localData)) {
            return null;
        }
        return PushDataList.fromJSON(localData);
    }


    /**
     * 保存设定了闹钟的推送ID，以便在closePush()方法里面清理
     *
     * @param type
     * @param notificationId
     */
    public static void appendNotificationIdToPrefenence(Context context, int type, long notificationId) {
        String idJson = AppConfigUtils.getPushNotificationIds(context);
        String idContent = type + "_" + notificationId;
        if (!idJson.contains(idContent)) {
            StringBuilder builder = new StringBuilder(idJson);
            if (!TextUtils.isEmpty(idJson)) {
                builder.append(",");
            }
            builder.append(idContent);
            AppConfigUtils.savePushNotificationIds(context, builder.toString());
        }
    }


    public static List<String> getNotificationIdsFromPreference(Context context) {
        String idJson = AppConfigUtils.getPushNotificationIds(context);
        List<String> idList = new ArrayList<>();
        if (!TextUtils.isEmpty(idJson)) {
            String[] ids = idJson.split(",");
            idList = Arrays.asList(ids);
        }
        return idList;
    }

    public static void clearNotificationIds(Context context) {
        AppConfigUtils.savePushNotificationIds(context, "");
    }

    //测试用
    public static void printTimeSeconds() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss", Locale.CHINESE);
        long now = System.currentTimeMillis();
        JSONArray times = new JSONArray();
        for (int i = 0; i < 5; i++) {
            long nextTimeSeconds = (now / 1000) + (i + 1) * (5 * 60);
            times.put(nextTimeSeconds);
            Log.d(TAG, "Time" + i + ":" + nextTimeSeconds + ", " + dateFormat.format(new Date(nextTimeSeconds * 1000)));
        }
        Log.d(TAG, "Time Array: " + times.toString());
    }

}
