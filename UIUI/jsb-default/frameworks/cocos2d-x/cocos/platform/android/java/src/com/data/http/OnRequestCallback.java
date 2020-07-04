package com.data.http;

import org.json.JSONObject;

public interface OnRequestCallback {
    void onRequestStart();
    void onRequestSuccess(int status, String msg, JSONObject object);
    void onRequestFailed(int status,String msg);
}
