package com.keeplive;

import android.text.TextUtils;

import java.util.ArrayList;

public class Config {

    private ArrayList<String> serviceNames = new ArrayList<String>();
    private boolean showLog;
    private long interval;


    private Config(Builder builder) {
        this.serviceNames = builder.serviceNames;
        this.showLog = builder.showLog;
        this.interval = builder.interval;
    }

    public ArrayList<String> getServiceNames() {
        return serviceNames;
    }

    public void setServiceNames(ArrayList<String> serviceNames) {
        this.serviceNames = serviceNames;
    }

    public boolean isShowLog() {
        return showLog;
    }

    public void setShowLog(boolean showLog) {
        this.showLog = showLog;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public static class Builder {

        ArrayList<String> serviceNames = new ArrayList<String>();
        boolean showLog;
        long interval;

        public Builder() {
        }

        /**
         * @param serviceName, Service全路径名
         * @return
         */
        public Builder appendService(String serviceName) {
            if (TextUtils.isEmpty(serviceName)) {
                return this;
            }
            if (!this.serviceNames.contains(serviceName)) {
                this.serviceNames.add(serviceName);
            }
            return this;
        }

        /**
         * @param serviceClass, Service class类型
         * @return
         */
        public Builder appendService(Class serviceClass) {
            if (null == serviceClass) {
                return this;
            }
            if (!this.serviceNames.contains(serviceClass.getName())) {
                this.serviceNames.add(serviceClass.getName());
            }
            return this;
        }

        public Builder keepLiveInterval(long interval) {
            this.interval = interval;
            return this;
        }

        public Builder showLog(boolean showLog) {
            this.showLog = showLog;
            return this;
        }

        public Config build() {
            return new Config(this);
        }

    }
}
