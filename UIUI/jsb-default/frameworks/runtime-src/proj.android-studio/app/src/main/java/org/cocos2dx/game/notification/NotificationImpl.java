package org.cocos2dx.game.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.util.AbstractNotification;
import com.util.FormatUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationImpl extends AbstractNotification {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss", Locale.CHINESE);
    private Context mContext;

    public NotificationImpl(Context context) {
        mContext = context;
    }

    @Override
    public void closePush() {
        List<String> idList = NotificationUtil.getNotificationIdsFromPreference(mContext);//已经通过sendPushNewsWithValue设定的闹钟ID。
        Log.d("Notification", "即将清理定时器：" + idList.toString());
        PushDataList pushDataList = NotificationUtil.getLocalPushDataList(mContext);//服务器配置的推送列表
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < idList.size(); i++) {
            String idContent = idList.get(i);
            if (!TextUtils.isEmpty(idContent)) {
                String[] idParts = idContent.split("_");
                if (idParts.length == 2) {
                    int type = FormatUtil.parseInt(idParts[0]);
                    int notificationId = FormatUtil.parseInt(idParts[1]);
                    if (type == PushData.PopType.TYPE_ONE_TIME) {
                        cancelNotificationById(notificationId);
                        //For log
                        builder.append(idContent + ",");
                    } else if (type == PushData.PopType.TYPE_REPEAT) {
                        //对于type=2的闹钟，如果已经在服务器端移除，则也需要取消该闹钟
                        if (!isPushExistOnServer(pushDataList, notificationId)) {
                            cancelNotificationById(notificationId);
                            //For log
                            builder.append(idContent + ",");
                        }
                    }
                }
            }
        }
        if (!TextUtils.isEmpty(builder)) {
            Log.d("Notification", "成功清理定时器：" + builder.toString());
        } else {
            Log.d("Notification", "没有需要清理的定时器");
        }
        NotificationUtil.clearNotificationIds(mContext);
    }


    private void cancelNotificationById(int notificationId) {
        //取消定时器必须传入相同request_code
        Intent intent = new Intent(mContext, NotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(mContext, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private boolean isPushExistOnServer(PushDataList pushDataList, int notificationId) {
        boolean found = false;
        if (null != pushDataList) {
            for (int i = 0; i < pushDataList.getList().size(); i++) {
                PushData pushData = pushDataList.getList().get(i);

                List<PushData.PopType> popTypes = pushData.getPopTypes();
                for (int j = 0; j < popTypes.size(); j++) {
                    PushData.PopType popType = popTypes.get(j);
                    List<Long> values = popType.getLongValues();
                    if (null == values) {
                        continue;
                    }
                    int type = popType.getType();
                    for (int k = 0; k < values.size(); k++) {
                        long value = values.get(k);
                        int tempId = NotificationUtil.buildLocalPushId(pushData.getId(), type, value);
                        if (notificationId == tempId) {
                            found = true;
                            break;
                        }
                    }

                }
            }
        }
        return found;
    }


    @Override
    public void sendPushNewsWithValue(String notificationIdString, String triggerAtMillisStr, String title, String content, String pushInfoStr) {
        if (TextUtils.isEmpty(content) && TextUtils.isEmpty(title)) {
            return;
        }
        int type = 0;
        long value = 0;
        //pushInfoStr的格式：[pushId]_[type]_[value]
        if (!TextUtils.isEmpty(pushInfoStr)) {
            String[] pushInfos = pushInfoStr.split("_");
            if (pushInfos.length == 3) {
                type = FormatUtil.parseInt(pushInfos[1]);
                value = FormatUtil.parseLong(pushInfos[2]);
            }
        }

        long tag = System.currentTimeMillis();
        int notificationId = FormatUtil.parseInt(notificationIdString);
        // alarmManager启动的type不同，time意义也不同
        //
        // RTC_WAKEUP：闹钟在睡眠状态下会唤醒系统并执行提示功能，该状态下闹钟使用【绝对时间】
        // ELAPSED_REALTIME_WAKEUP：闹钟在睡眠状态下会唤醒系统并执行提示功能，该状态下闹钟使用【相对时间】

        // 绝对到期时间
        long triggerTime = FormatUtil.parseLong(triggerAtMillisStr) * 1000;

        // 这里不用隐式启动的原因：5.0以后系统对隐式启动有限制，无法保证所有版本都可以安全隐式启动
        Intent intent = new Intent(mContext, NotificationService.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        intent.putExtra("tag", tag);
        intent.putExtra("notificationId", notificationId);
        intent.putExtra("pushInfoStr", pushInfoStr);

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        boolean shouldSetAlarm = true;
        if (type == 2) {
            //此方法可以判断是否存在已存在相同闹钟，request_code相同认定为同一闹钟
            PendingIntent sender = PendingIntent.getService(mContext, notificationId, intent, PendingIntent.FLAG_NO_CREATE);
            if (sender != null) {
                Log.i("Notification", "推送闹钟[" + notificationId + "]已存在，重复间隔：" + value + "天");
                shouldSetAlarm = false;
            }
        }

        if (shouldSetAlarm) {
            PendingIntent pendingIntent = PendingIntent.getService(mContext, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (type == PushData.PopType.TYPE_ONE_TIME) {
                Log.i("Notification", "设定推送[" + notificationId + "]：" + dateFormat.format(new Date(triggerTime)) + ", 不重复");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //MARSHMALLOW OR ABOVE
                    Log.i("Notification", "=2===1===" + tag);
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //LOLLIPOP 21 OR ABOVE
                    Log.i("Notification", "=2===2===" + tag);
                    AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(triggerTime, pendingIntent);
                    alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //KITKAT 19 OR ABOVE
                    Log.i("Notification", "=2===3===" + tag);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                } else { //FOR BELOW KITKAT ALL DEVICES
                    Log.i("Notification", "=2===4===" + tag);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                }
            } else if (type == PushData.PopType.TYPE_REPEAT) {
                if (value > 0) {
                    Log.i("Notification", "设定推送[" + notificationId + "]：" + dateFormat.format(new Date(triggerTime)) + ", 重复间隔：" + value + "天");
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerTime, value * AlarmManager.INTERVAL_DAY, pendingIntent);
                }
            }
        }
        NotificationUtil.appendNotificationIdToPrefenence(mContext, type, notificationId);
    }
}
