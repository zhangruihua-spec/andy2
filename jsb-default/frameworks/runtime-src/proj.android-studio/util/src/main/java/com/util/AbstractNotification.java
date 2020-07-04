package com.util;

public abstract class AbstractNotification {

    public abstract void closePush();

    public abstract void sendPushNewsWithValue(String notificationIdString, String triggerAtMillisStr, String title, String content, String pushInfoStr);

}
