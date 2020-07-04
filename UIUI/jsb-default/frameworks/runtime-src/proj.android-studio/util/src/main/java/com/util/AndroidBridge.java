package com.util;

import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Handler;

public class AndroidBridge {

    public interface ScriptCallBack {
        void closeEbetGame();
    }

    private static final String TAG = "AndroidBridge";
    private Handler _handler;
    private ScriptCallBack mScriptCallBack;

    public void setScriptCallBack(ScriptCallBack cb) {
        mScriptCallBack = cb;
    }

    public AndroidBridge(Handler handler) {
        this._handler = handler;
    }

    @JavascriptInterface
    public void closeEbetGame() {

        if (this._handler != null) {
            if (mScriptCallBack != null) {
                mScriptCallBack.closeEbetGame();
            }
        }
    }

    // 电竞游戏
    @JavascriptInterface
    public void postMessage(String jsonString) {
        Log.i("testIMCG", "postMessage=" + jsonString);
        if (TextUtils.isEmpty(jsonString) || TextUtils.equals("undefined", jsonString)) {
            this.exitGame();
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            if (jsonObject == null) {
                this.exitGame();
                return;
            }

            String action = jsonObject.optString("action");
            Log.i("testIMCG", "postMessage action=" + action);
            if (TextUtils.equals("quitESports", action)) { // 退出
                this.exitGame();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void exitGame() {
        if (this._handler != null) {
            if (mScriptCallBack != null) {
                Log.i("testIMCG", "postMessage exitGame");
                mScriptCallBack.closeEbetGame();
            }
        }
    }
}
