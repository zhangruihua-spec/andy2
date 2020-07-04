package org.cocos2dx.game.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;

import com.adjust.sdk.Adjust;
import com.cocos2dx.game.R;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.umeng.analytics.MobclickAgent;
import com.util.observable.CommandObservable;

import org.json.JSONException;
import org.json.JSONObject;

import bridge.ObservableManager;

public class PayWebViewActivity extends Activity {

    public static final String PAYURL = "PAYURL";

    private WebView webview;
    private String payurl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  设置横屏代码：setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
     //   设置竖屏代码：setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
       // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.paywebviewactivity_layout);
        webview = findViewById(R.id.paywebviewactivity_layout_webview);

//        findViewById(R.id.paywebviewactivity_layout_testbutton).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                CommandObservable observable = ObservableManager.getInstance().getObservable(CommandObservable.class);
//                if (null != observable) {
//                    observable.setCommand("message://command?cmd=paycallback&status=0");
//                    observable.notifyChanged();
//                }
//                finish();
//            }
//        });
        initPayUrl();
        initWebViewSettings();
    }

    /**
     * 初始化参数传递
     */
    private void initPayUrl() {
        payurl = getIntent().getStringExtra(PAYURL);
    }

    private void initWebViewSettings() {
        WebSettings webSetting = webview.getSettings();
        webSetting.setAllowFileAccess(true);
         // webSetting.setLayoutAlgorithm(IX5WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setSaveFormData(false);
        webSetting.setSavePassword(false);
        webSetting.setGeolocationEnabled(true);

        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0)
                .getPath());
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // webSetting.setPreFectch(true);
        long time = System.currentTimeMillis();
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

//            @Override
//            public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    if (webResourceRequest.getUrl().toString().contains("recharge/success.html")){
//                        webView.loadUrl(webResourceRequest.getUrl().toString());
//                        return true;
//                    }
//                }
//
//                return false;
//            }
        });
        webview.addJavascriptInterface(new PayWebViewInterface(), "android");
        if(!TextUtils.isEmpty(payurl)){
            //payurl = "http://192.168.1.11:8888/api/pay/recharge/success.html";
            webview.loadUrl(payurl);
        }
        Log.d("time-cost", "cost time: "
                + (System.currentTimeMillis() - time));
    }
    private class PayWebViewInterface {
//        @JavascriptInterface
//        public void myFunctionName(String jsonString){
//            // 自定义做点什么，这里解析一个json格式字符串
//            try {
//                JSONObject jsonObj = new JSONObject(jsonString);
//               // mBook.bookName = jsonObj.optString("name");
//               // mBook.bookAuthor = jsonObj.optString("author");
//               // mBook.coverImage = jsonObj.optString("cover_image");
//            }
//         catch (JSONException e) {
//            e.printStackTrace();
//        }
      @JavascriptInterface
      public void jumpToHall(final String status){
                // 自定义做点什么，这里解析一个json格式字符串
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CommandObservable observable = ObservableManager.getInstance().getObservable(CommandObservable.class);
                    if (null != observable) {
                        observable.setCommand("message://command?cmd=paycallback&status="+status);
                        observable.notifyChanged();
                    }
                    finish();
                }
            });
     }
}

    @Override
    protected void onResume() {
        super.onResume();
        Adjust.onResume();
        MobclickAgent.onResume(this); // 不能遗漏
    }

    @Override
    protected void onPause() {
        super.onPause();
        Adjust.onPause();
        MobclickAgent.onPause(this); // 不能遗漏
    }
}
