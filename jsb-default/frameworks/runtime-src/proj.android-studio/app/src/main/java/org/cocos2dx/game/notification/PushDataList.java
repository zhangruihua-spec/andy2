package org.cocos2dx.game.notification;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import com.util.JSONUtil;

import java.util.ArrayList;
import java.util.List;

public class PushDataList {

    @SerializedName("list")
    private List<PushData> list = new ArrayList<>();

    @SerializedName("ts")
    private long ts;

    public List<PushData> getList() {
        return list;
    }

    public void setList(List<PushData> list) {
        this.list = list;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public static PushDataList fromJSON(String pushJSON) {
        PushDataList pushDataList = null;
        try {
            Gson gson = new Gson();
            pushDataList = gson.fromJson(pushJSON, PushDataList.class);

            for (int i = 0; i < pushDataList.getList().size(); i++) {
                PushData pushData = pushDataList.getList().get(i);
                List<PushData.PopType> popTypes = pushData.getPopTypes();
                if (null != popTypes) {
                    for (int j = 0; j < popTypes.size(); j++) {
                        PushData.PopType popType = popTypes.get(j);
                        JSONUtil.performTransformWithEvaluation(popType, "setLongValues", popType.getValue(), Long.class);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pushDataList;
    }
}
