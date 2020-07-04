package org.cocos2dx.bean;

public class OneTimeProduct {
    private String version;
    private int notificationType;
    private String purchaseToken;
    private String sku;

    public void setVersion(String version) {
        this.version = version;
    }

    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
    }

    public void setPurchaseToken(String purchaseToken) {
        this.purchaseToken = purchaseToken;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getVersion() {
        return version;
    }

    public int getNotificationType() {
        return notificationType;
    }

    public String getPurchaseToken() {
        return purchaseToken;
    }

    public String getSku() {
        return sku;
    }

}
