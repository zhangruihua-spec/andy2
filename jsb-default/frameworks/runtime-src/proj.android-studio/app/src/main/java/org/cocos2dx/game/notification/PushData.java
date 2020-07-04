package org.cocos2dx.game.notification;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PushData {


    @SerializedName("_id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("deleted")
    private boolean deleted;

    @SerializedName("last_modified")
    private long lastModified;

    @SerializedName("expire_at")
    private long expireAt; //单位：秒

    @SerializedName("pop_at")
    private List<PopType> popTypes = new ArrayList<>();

    public class PopType {

        public static final int TYPE_ONE_TIME = 1; //固定时间点推送
        public static final int TYPE_REPEAT = 2; //每N天推送一次

        private int type;

        private Object value;//value字段可能是Long数组，也可能是一个Long值。

        private List<Long> longValues = new ArrayList<>();//value的值统一转为Long数组后赋值到该字段，外部应该使用该字段。

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public List<Long> getLongValues() {
            return longValues;
        }

        public void setLongValues(List<Long> longValues) {
            this.longValues = longValues;
        }
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public List<PopType> getPopTypes() {
        return popTypes;
    }

    public void setPopTypes(List<PopType> popTypes) {
        this.popTypes = popTypes;
    }

    public long getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(long expireAt) {
        this.expireAt = expireAt;
    }
}
