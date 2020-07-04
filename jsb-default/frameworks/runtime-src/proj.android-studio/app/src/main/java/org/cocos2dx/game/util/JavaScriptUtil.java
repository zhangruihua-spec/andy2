package org.cocos2dx.game.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.util.AppUtil;
import com.util.EngineUtil;
import com.util.observable.ViewUtil;

public class JavaScriptUtil {

    public interface ScriptCallBack {
        void hideTopBar();

        void goBack();

        void quidBrowser();
        void showBrowserToolBar();
        void hideBrowserToolBar();
        void refreshWebView();
        void clearCache();
        void getImageUrl(String url);

        void showNativeCloseButton();
        void hideNativeCloseButton();
    }

    private Context mContext;
    private ScriptCallBack mScriptCallBack;

    public JavaScriptUtil(Context context) {
        mContext = context;
    }

    public void setScriptCallBack(ScriptCallBack cb) {
        mScriptCallBack = cb;
    }

    @JavascriptInterface
    public void jsTest() {
        Toast.makeText(mContext, "call native success.", Toast.LENGTH_SHORT)
                .show();
    }

    @JavascriptInterface
    public void openUrlByDefaultBrowser(String url) {

        if (TextUtils.isEmpty(url)) {
            Toast.makeText(mContext, "URL不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.equals(url, "weixin://dl/chat")) {
            AppUtil.startAppByPackageName(mContext, "com.tencent.mm");
        } else {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            mContext.startActivity(intent);
        }
    }
    @JavascriptInterface
    public void openUrlByaBrowser(String url) {

    }
    @JavascriptInterface
    public void copyText(final String text) {
        ViewUtil.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                ClipboardManager manager = (ClipboardManager)
                        mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                manager.setPrimaryClip(ClipData.newPlainText(null, text));
            }
        });
    }
    @JavascriptInterface
    public void showNativeToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT)
                .show();

    }
    @JavascriptInterface
    public void showNativeDialog(String dialog, String content) {

    }
    @JavascriptInterface
    public void quitBrowser() {
        if (null != mScriptCallBack) {
            mScriptCallBack.quidBrowser();
        }
    }
    @JavascriptInterface
    public void showCloseButton() {
        if (null != mScriptCallBack) {
            mScriptCallBack.showNativeCloseButton();
        }
    }
    @JavascriptInterface
    public void hideCloseButton() {
        if (null != mScriptCallBack) {
            mScriptCallBack.hideNativeCloseButton();
        }
    }

    @JavascriptInterface
    public void showBrowserToolBar() {
        if (null != mScriptCallBack) {
            mScriptCallBack.showBrowserToolBar();
        }
    }
    @JavascriptInterface
    public void hideBrowserToolBar() {
        if (null != mScriptCallBack) {
            mScriptCallBack.hideBrowserToolBar();
        }
    }
    @JavascriptInterface
    public void refreshWebView() {
        if (null != mScriptCallBack) {
            mScriptCallBack.refreshWebView();
        }
    }
    @JavascriptInterface
    public void clearCache() {
        if (null != mScriptCallBack) {
            mScriptCallBack.clearCache();
        }
    }
    @JavascriptInterface
    public void getImageUrl(String url) {
        if (null != mScriptCallBack) {
            mScriptCallBack.getImageUrl(url);
        }
    }

    @JavascriptInterface
    public void goHome() {

    }

    @JavascriptInterface
    public void goRechargeWithAmount(int amount) {

    }

    @JavascriptInterface
    public void goRecharge() {

    }

    @JavascriptInterface
    public void goShare(String url, String title, String content, String imgUrl) {

    }

    @JavascriptInterface
    public void goShare(String url, String title, String content, String imgUrl, String source) {


    }

    @JavascriptInterface
    public void goShareSign(String url, String title, String content, String imgUrl) {
        //ShareUtil.showShare(mContext, title, url, content,
        //imgUrl, ShareActivity.SOURCE_SIGN_SHARE);
        //AppTrackUtil.trackShareClick(mContext, ShareActivity.SOURCE_SIGN_SHARE);
    }

    @JavascriptInterface
    public String getVersionCode() {
        return EngineUtil.getVersionCode();
    }

    @JavascriptInterface
    public String getUserInfo() {

        return "";
    }

    @JavascriptInterface
    public void executeCommand(final String command, final String data) {

    }

    @JavascriptInterface
    public void saveToClipBoard(final String text) {
        ViewUtil.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                ClipboardManager manager = (ClipboardManager)
                        mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                manager.setPrimaryClip(ClipData.newPlainText(null, text));
            }
        });
    }

    @JavascriptInterface
    public void goBack() {
        ViewUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mScriptCallBack != null) {
                    mScriptCallBack.goBack();
                }
            }
        });
    }

    @JavascriptInterface
    public void hideNativeTopBar() {
        ViewUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mScriptCallBack != null) {
                    mScriptCallBack.hideTopBar();
                }
            }
        });
    }

    @JavascriptInterface
    public void successOnloadPageFastpay() {
    }

}
