package com.data.http;

import android.content.Context;
import android.net.Uri;


import com.util.Device;


import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpGetDataTask extends BaseHttpDataTask {

    public HttpGetDataTask(Context context,String url, HttpDataLoadCallback callback) {
        super(context,url, callback);
    }

    @Override
    protected JSONObject getOKHttpObject(String url) {

    JSONObject object = null;

        OkHttpClient.Builder okhttpBuilder = new OkHttpClient.Builder();
        okhttpBuilder.connectTimeout(8, TimeUnit.SECONDS);
        okhttpBuilder.readTimeout(8, TimeUnit.SECONDS);
        okhttpBuilder.writeTimeout(8, TimeUnit.SECONDS);
        OkHttpClient client = okhttpBuilder.build();
        Request.Builder builder = new Request.Builder();

        addAccountHeaer(builder);

        url = Uri.parse(url).buildUpon()
                .appendQueryParameter("p", Device.getDeviceInfo())
                .build().toString();
        Request request = builder.url(url).build();

        try {
            Response response = client.newCall(request).execute();
            object = getDataFromResponseString(response.body().string());
        } catch (IOException e) {
//            LogHelper.e("getOKHttpObject", e.toString());
        }
        return object;
    }

}
