package org.cocos2dx.javascript;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import androidx.annotation.Nullable;
import com.adjust.sdk.Adjust;
import com.fireball.cocos_demo.R;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.umeng.analytics.MobclickAgent;

import org.cocos2dx.bridge.MessageManager;
import org.cocos2dx.observable.CommonCmd;

public class ComWebviewActivity extends Activity {

    public static final String URL = "URL";

    private WebView webview;
    private String payurl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  设置横屏代码：setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
        //   设置竖屏代码：setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.comwebview_layout);
        webview = findViewById(R.id.webviewactivity_layout_webview);

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
        payurl = getIntent().getStringExtra(URL);
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
                    CommonCmd observable = MessageManager.getInstance().getObservable(CommonCmd.class);
                    if (null != observable) {
                        observable.setCommand("message://msg?cmd=paycallback&status="+status);
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