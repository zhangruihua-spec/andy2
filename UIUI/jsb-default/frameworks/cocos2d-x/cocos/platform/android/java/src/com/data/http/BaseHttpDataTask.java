package com.data.http;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.util.LoginUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import okhttp3.Request;


public class BaseHttpDataTask extends AsyncTask<Void, Void, JSONObject> {
    protected String mUrl;
    protected HttpDataLoadCallback mCallback;
    protected String mParams;
    protected int mStatus = -1;
    protected String mMsg;

    protected Context context;
    public BaseHttpDataTask(Context context, String url, HttpDataLoadCallback callback) {
        this(context,url, null, callback);
    }


    public BaseHttpDataTask(Context context,String url, String params, HttpDataLoadCallback callback) {
        this.context = context;
        mUrl = url;
        mCallback = callback;
        mParams = params;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mCallback.onDataLoadStart();
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        return getOKHttpObject(mUrl);
    }

    @Override
    protected void onPostExecute(JSONObject object) {
        super.onPostExecute(object);
        mCallback.onDataLoadComplete(mStatus, mMsg, object);
    }

    protected JSONObject getOKHttpObject(String url) {

        return null;
    }
//
    protected Request.Builder addAccountHeaer(Request.Builder builder) {
        String uid = LoginUtil.getInstance().getUid();
        String token = LoginUtil.getInstance().getToken();
        if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(token)) {

            //nothing to do.
        } else {
            builder.addHeader("X-AUTH-USER", uid)
                    .addHeader("X-AUTH-TOKEN", token);
        }
        return builder;
    }

    protected JSONObject getDataFromResponseString(String response) {
        JSONTokener jsonParser = new JSONTokener(response);
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.nextValue();
            if (null != jsonObject) {

                mStatus = jsonObject.optInt("status", -1);
                mMsg = jsonObject.optString("msg");
                if (0 == mStatus) {
                    Object dataObj = jsonObject.opt("data");
                    if(dataObj == null){
                        return null;
                    } else{
                        if(dataObj instanceof JSONObject){
                            return (JSONObject)dataObj;
                        } else if(dataObj instanceof JSONArray){
                            JSONObject resultObj = new JSONObject();
                            resultObj.put("data",dataObj);
                            return resultObj;
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
        return null;
    }
}
