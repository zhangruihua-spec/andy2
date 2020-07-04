package com.data.http;

import org.json.JSONObject;


public interface HttpDataLoadCallback {
    public void onDataLoadStart();
    public void onDataLoadComplete(int status, String msg, JSONObject object);
}
