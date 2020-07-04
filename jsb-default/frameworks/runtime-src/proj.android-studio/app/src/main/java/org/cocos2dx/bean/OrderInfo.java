package org.cocos2dx.bean;

public class OrderInfo {
    private String version;
    private String packageName;
    private long eventTimeMillis;
    private OneTimeProduct oneTimeProductNotification;

    public void setVersion(String version) {
        this.version = version;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setEventTimeMillis(long eventTimeMillis) {
        this.eventTimeMillis = eventTimeMillis;
    }

    public void setOneTimeProductNotification(OneTimeProduct oneTimeProductNotification) {
        this.oneTimeProductNotification = oneTimeProductNotification;
    }

    public String getVersion() {
        return version;
    }

    public String getPackageName() {
        return packageName;
    }

    public long getEventTimeMillis() {
        return eventTimeMillis;
    }

    public OneTimeProduct getOneTimeProductNotification() {
        return oneTimeProductNotification;
    }
}
