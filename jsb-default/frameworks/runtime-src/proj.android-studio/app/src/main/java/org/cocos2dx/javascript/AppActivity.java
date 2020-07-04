/****************************************************************************
Copyright (c) 2015-2016 Chukong Technologies Inc.
Copyright (c) 2017-2018 Xiamen Yaji Software Co., Ltd.
 
http://www.cocos2d-x.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
****************************************************************************/
package org.cocos2dx.javascript;

import org.cocos2dx.bridge.MessageManager;
import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxGLSurfaceView;
import org.cocos2dx.lib.Cocos2dxJavascriptJavaBridge;
import org.cocos2dx.observable.CommonCmd;
import org.cocos2dx.observable.ViewUtil;
import org.cocos2dx.util.AppConfigUtils;
import org.cocos2dx.util.AppUtil;
import org.cocos2dx.util.CocosBridgeEngineUtil;
import org.cocos2dx.util.DeviceInfo;
import org.json.JSONException;

import android.net.Uri;
import android.os.Bundle;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.adjust.sdk.Adjust;
import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.umeng.analytics.MobclickAgent;
//import com.google.api.core.ApiFuture;
//import com.google.api.core.ApiFutures;
//import com.google.cloud.pubsub.v1.Publisher;
//import com.google.gson.Gson;
//import com.google.protobuf.ByteString;
//import com.google.protobuf.DescriptorProtos;
//import com.google.pubsub.v1.ProjectTopicName;
//import com.google.pubsub.v1.PubsubMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.tencent.bugly.crashreport.CrashReport;


public class AppActivity extends Cocos2dxActivity implements PurchasesUpdatedListener, Observer {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        // Workaround in
        // https://stackoverflow.com/questions/16283079/re-launch-of-activity-on-home-button-but-only-the-first-time/16447508
        if (!isTaskRoot()) {
            // Android launched another instance of the root activity into an existing task
            // so just quietly finish and go away, dropping the user back into the activity
            // at the top of the stack (ie: the last state of this task)
            // Don't need to finish it again since it's finished in super.onCreate .
            return;
        }
        // DO OTHER INITIALIZATION BELOW
        AppUtil.initVersionCode(this);
        AppUtil.initChannel(this);
        DeviceInfo.createDevicFromPhone(this).toDeviceInfoString();
        CocosBridgeEngineUtil.init(this);
        SDKWrapper.getInstance().init(this);
        doStartConnectionGooglePay();
        String gprefurl = AppConfigUtils.getStringFromPrefenence(AppActivity.this,AppConfigUtils.GP_REFFERERURL);

        String no_set  = "{utm_source=(not%20set)&utm_medium=(not%20set)";
        if(TextUtils.isEmpty(gprefurl)||no_set.equals(gprefurl)){
            //如果获取到的渠道为空 或者 没有设置渠道的情况下的话 重新获取渠道
            doStartConnectionGoogleInstallreferrer();
        }

        CommonCmd commandObservable = MessageManager.getInstance().getObservable(CommonCmd.class);
        if (null != commandObservable) {
            commandObservable.addObserver(this);
        }
        //bugly
        CrashReport.initCrashReport(getApplicationContext(),"23a238948f", true);
//        int result = 1 / 0;//测试
    }

    /**
     *  链接google 谷歌安装统计
     */
    InstallReferrerClient referrerClient;
    private void doStartConnectionGoogleInstallreferrer() {


        referrerClient = InstallReferrerClient.newBuilder(this).build();
        referrerClient.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                switch (responseCode) {
                    case InstallReferrerClient.InstallReferrerResponse.OK:
                        // Connection established.
                        ReferrerDetails response = null;
                        try {
                            response = referrerClient.getInstallReferrer();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        if(response!=null){
                            String referrerUrl = response.getInstallReferrer();
                            long referrerClickTime = response.getReferrerClickTimestampSeconds();
                            long appInstallTime = response.getInstallBeginTimestampSeconds();
                            boolean instantExperienceLaunched = response.getGooglePlayInstantParam();
                            //DeviceInfo.gpref = referrerUrl;
                            if(!TextUtils.isEmpty(referrerUrl)){
                                AppConfigUtils.saveStringToPrefenence(AppActivity.this,AppConfigUtils.GP_REFFERERURL,referrerUrl);
                                DeviceInfo.createDevicFromPhone(AppActivity.this).toDeviceInfoString();
                                ViewUtil.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        runOnGLThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Cocos2dxJavascriptJavaBridge.evalString("cc.onRefreshDevice()");
                                            }
                                        });
                                    }
                                });

                        }
                            referrerClient.endConnection();
                        }

                        break;
                    case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                        // API not available on the current Play Store app.
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                        // Connection couldn't be established.
                        break;
                }
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });
    }

    @Override
    public Cocos2dxGLSurfaceView onCreateView() {
        Cocos2dxGLSurfaceView glSurfaceView = new Cocos2dxGLSurfaceView(this);
        // TestCpp should create stencil buffer
        glSurfaceView.setEGLConfigChooser(5, 6, 5, 0, 16, 8);
        SDKWrapper.getInstance().setGLSurfaceView(glSurfaceView, this);

        return glSurfaceView;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SDKWrapper.getInstance().onResume();
        Adjust.onResume();
        MobclickAgent.onResume(this); // 不能遗漏
    }

    @Override
    protected void onPause() {
        super.onPause();
        SDKWrapper.getInstance().onPause();
        Adjust.onPause();
        MobclickAgent.onPause(this); // 不能遗漏
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SDKWrapper.getInstance().onDestroy();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SDKWrapper.getInstance().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        SDKWrapper.getInstance().onNewIntent(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SDKWrapper.getInstance().onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SDKWrapper.getInstance().onStop();
    }

    @Override
    public void onBackPressed() {
        SDKWrapper.getInstance().onBackPressed();
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        SDKWrapper.getInstance().onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        SDKWrapper.getInstance().onRestoreInstanceState(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        SDKWrapper.getInstance().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        SDKWrapper.getInstance().onStart();
        super.onStart();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof CommonCmd) {
            String command = (String) arg;
            excuteCommonCmd(command);
        }
    }
    private boolean canFinish = false;
    private static final int MSG_QUIT_FLAT = 0x001;
    private static final int MSG_QUIT_TOAST = 0x002;
    private static final int MSG_QUIT_FINAL = 0x003;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_QUIT_TOAST:
                    Toast.makeText(AppActivity.this, "Press again to exit the game", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_QUIT_FLAT:
                    canFinish = false;
                    break;
                case MSG_QUIT_FINAL:
                    killProcess();
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private void killProcess() {
        android.os.Process.killProcess(android.os.Process.myPid());    //获取PID
        System.exit(0);   //常规java、c#的标准退出法，返回值为0代表正常退出
    }
    private void exitGame() {
        if (canFinish) {
            mHandler.sendEmptyMessageDelayed(MSG_QUIT_FINAL, 200);
        } else {
            canFinish = true;
            mHandler.sendEmptyMessage(MSG_QUIT_TOAST);
        }
        mHandler.sendEmptyMessageDelayed(MSG_QUIT_FLAT, 1000);
    }

    private void excuteCommonCmd(String command) {
        Uri uri = Uri.parse(command);
        String query = uri.getQuery();
        String host = uri.getHost();
        Bundle bundle = getBundle(query);
//
        if (TextUtils.equals("gppay", host)) {
            final String payid = bundle.getString("payid");
            String token = bundle.getString("token");
            String uid = bundle.getString("uid");
            ViewUtil.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    getSkuList(payid);
                }
            });

        }else if(TextUtils.equals("msg", host)){
             String cmd = bundle.getString("cmd");
             if("quit_game".equals(cmd)){
                 exitGame();
             }else if(TextUtils.equals("paycallback", cmd)){
                 final String bayStatus = bundle.getString("status");
                 runOnGLThread(new Runnable() {
                     @Override
                     public void run() {
                         Cocos2dxJavascriptJavaBridge.evalString(String.format("cc.onBayCallbackJS(%s)", bayStatus));
                     }
                 });
             }
        }else if(TextUtils.equals("url", host)){
            final String url = bundle.getString("url");
            ViewUtil.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //打开浏览器
                    Intent intent = new Intent(AppActivity.this,ComWebviewActivity.class);
                    intent.putExtra(ComWebviewActivity.URL,url);
                    startActivity(intent);
                }
            });
        }else if(TextUtils.equals("installreferrerreceiver",host)){
            final String referrer = bundle.getString("referrer");
            DeviceInfo.createDevicFromPhone(AppActivity.this).toDeviceInfoString();
            ViewUtil.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    runOnGLThread(new Runnable() {
                        @Override
                        public void run() {
                            Cocos2dxJavascriptJavaBridge.evalString("cc.onRefreshDevice()");
                        }
                    });
                }
            });
        }else if(TextUtils.equals("adjustinfo",host)){
            DeviceInfo.createDevicFromPhone(AppActivity.this).toDeviceInfoString();
            ViewUtil.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    runOnGLThread(new Runnable() {
                        @Override
                        public void run() {
                            Cocos2dxJavascriptJavaBridge.evalString("cc.onRefreshDevice()");
                        }
                    });
                }
            });
        }
    }

    private Bundle getBundle(String query) {
        Bundle bundle = new Bundle();
        try {
            if (!TextUtils.isEmpty(query)) {
                String[] params = query.split("&");
                if (params != null && params.length > 0) {
                    for (String kv : params) {
                        String[] data = kv.split("=");
                        if (data != null && data.length == 2) {
                            bundle.putString(data[0], data[1]);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bundle;
    }

    /**
     * 做开始尝试连接google pay的操作
     */
    private BillingClient billingClient;
    private boolean isConnect = false;

    private void doStartConnectionGooglePay() {

        billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The billing client is ready. You can query purchases here.
                    isConnect = true;
                    queryAndConsumePurchase();
                    //Log.e("PayDiamondActivity", "谷歌支付链接成功");
                } else {
                    //Log.e("PayDiamondActivity", billingResult.getResponseCode() + "");
                    isConnect = false;
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                isConnect = false;
            }
        });
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            //Toast.makeText(this,"User cancel",Toast.LENGTH_SHORT).show();
        } else {
            // Handle any other error codes.
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    /**
     * 根据谷歌后台设置的商品id查询商品详情：
     *
     * @param goodsId
     */
    private List<String> skuList = new ArrayList<>();

    private void getSkuList(String goodsId) {
        skuList.clear();
        skuList.add(goodsId);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                                && skuDetailsList != null) {
                            if (skuDetailsList.size() > 0) {
                                SkuDetails skuDetail = skuDetailsList.get(0);
//                                ToastUtil.show(skuDetail.toString());
                                googlePay("", skuDetail);
                            }
                        }
                    }
                });
    }


    public void googlePay(String mOrderId, SkuDetails skuDetails) {
        // this.mOrderId = mOrderId;
        if (isConnect) {
            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetails)
                    .build();
            int responseCode = billingClient.launchBillingFlow(AppActivity.this, flowParams).getResponseCode();
            if (responseCode != 0) {
                //ToastUtil.show(responseCode + ":Current region does not support Google payments");
            }
        } else {
            Toast.makeText(AppActivity.this,"Current region does not support Google payments",Toast.LENGTH_SHORT).show();
        }
    }


    private Handler handler = new Handler();

    private void handlePurchase(final Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            // Grant entitlement to the user.
            // Acknowledge the purchase if it hasn't already been acknowledged.
            if (!purchase.isAcknowledged()) {
                if (!purchase.isAcknowledged()) {
                    acknowledgePurchase(purchase);
                }
                //消耗品 开始消耗
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        consumePuchase(purchase, consumeDelay);
                    }
                }, 2000);

            }

            //通知服务端
            postGooglePay(purchase);
        }
    }

    private void postGooglePay(Purchase purchase) {
        String DeveloperPayload = purchase.getDeveloperPayload();
        String OrderId = purchase.getOrderId();
        final String OriginalJson = purchase.getOriginalJson();
        String PackageName = purchase.getPackageName();
        int PurchaseState = purchase.getPurchaseState();
        long PurchaseTime = purchase.getPurchaseTime();
        String PurchaseToken = purchase.getPurchaseToken();
        String Signature = purchase.getSignature();
        String Sku = purchase.getSku();
        Log.e("OriginalJson",OriginalJson);
        //{"orderId":"GPA.3375-8230-1170-72287","packageName":"com.happy.gptestone","productId":"product_releae_70gold","purchaseTime":1588922809159,
        // "purchaseState":0,"purchaseToken":"opfhfblldmcjblhcjglfgpml.AO-J1Oyss5hVAaLX_SRxvGcvC7aNFsoXkCSs-aM1Z2Ul9XNooIdW_jxm8K5G_VDyns2XCh1pHhoVK-zfjuFHL_4CEOtv32MGC2q1ucd3rwSyIupsnQXRNmhJrt2Vdi9U19vVsAPGS9sp",
        // "acknowledged":false}
        runOnGLThread(new Runnable() {
            @Override
            public void run() {
                Cocos2dxJavascriptJavaBridge.evalString(String.format("cc.callbackNativeGpPay(%s)", OriginalJson));
            }
        });

//        OrderInfo orderInfo = new OrderInfo();
//        orderInfo.setVersion(DeviceInfo.getVersionName());
//        orderInfo.setEventTimeMillis(PurchaseTime);
//        orderInfo.setPackageName(CocosBridgeEngineUtil.getPackageName());
//
//        OneTimeProduct oneTimeProduct = new OneTimeProduct();
//        oneTimeProduct.setVersion(DeviceInfo.getVersionName());
//        oneTimeProduct.setNotificationType(PurchaseState);
//        oneTimeProduct.setPurchaseToken(PurchaseToken);
//        oneTimeProduct.setSku(Sku);
//        orderInfo.setOneTimeProductNotification(oneTimeProduct);
//        final String jsonString=new Gson().toJson(orderInfo);
            // Create a publisher instance with default settings bound to the topic
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//
//                    ProjectTopicName topicName = ProjectTopicName.of("happyrummy", "gppay");
//                    Publisher publisher = null;
//                    List<ApiFuture<String>> futures = new ArrayList<>();
//                    publisher = Publisher.newBuilder(topicName).build();
//
//                    ByteString data = ByteString.copyFromUtf8(jsonString);
//                    PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
//
//                    // Schedule a message to be published. Messages are automatically batched.
//                    ApiFuture<String> future = publisher.publish(pubsubMessage);
//                    futures.add(future);
//                    List<String> messageIds = ApiFutures.allAsList(futures).get();
//
//                    for (String messageId : messageIds) {
//                        System.out.println(messageId);
//                    }
//
//                    if (publisher != null) {
//                        // When finished with the publisher, shutdown to free up resources.
//                        publisher.shutdown();
//                    }
//                    } catch (Exception ption){
//                        Log.e("Exception",ption.toString());
//                    }finally {
//                        // Wait on any pending requests
//
//                    }
//                }
//
//            }).start();



    }


    private final int consumeImmediately = 0;
    private final int consumeDelay = 1;

    //消耗商品
    private void consumePuchase(final Purchase purchase, final int state) {
        ConsumeParams.Builder consumeParams = ConsumeParams.newBuilder();
        consumeParams.setPurchaseToken(purchase.getPurchaseToken());
        consumeParams.setDeveloperPayload(purchase.getDeveloperPayload());
        billingClient.consumeAsync(consumeParams.build(), new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                // Log.i(TAG, "onConsumeResponse, code=" + billingResult.getResponseCode());
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // Log.i(TAG, "onConsumeResponse,code=BillingResponseCode.OK");
                    if (state == consumeImmediately) {

                    }
                } else {
                    //如果消耗不成功，那就再消耗一次
                    // Log.i(TAG, "onConsumeResponse=getDebugMessage==" + billingResult.getDebugMessage());
                    if (state == consumeDelay && billingResult.getDebugMessage().contains("Server error, please try again")) {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                queryAndConsumePurchase();
                            }
                        }, 5 * 1000);
                    }
                }
            }
        });
    }

    //确认订单
    private void acknowledgePurchase(Purchase purchase) {
        AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();
        AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // Log.i(TAG, "Acknowledge purchase success");
                } else {
                    //Log.i(TAG, "Acknowledge purchase failed,code=" + billingResult.getResponseCode() + ",\nerrorMsg=" + billingResult.getDebugMessage());
                }
            }
        };
        billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
    }

    //查询最近的购买交易，并消耗商品
    private void queryAndConsumePurchase() {
        //queryPurchases() 方法会使用 Google Play 商店应用的缓存，而不会发起网络请求
        billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP,
                new PurchaseHistoryResponseListener() {
                    @Override
                    public void onPurchaseHistoryResponse(BillingResult billingResult,
                                                          List<PurchaseHistoryRecord> purchaseHistoryRecordList) {
                        {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchaseHistoryRecordList != null) {
                                for (PurchaseHistoryRecord purchaseHistoryRecord : purchaseHistoryRecordList) {
                                    // Process the result.
                                    //确认购买交易，不然三天后会退款给用户
                                    try {
                                        Purchase purchase = new Purchase(purchaseHistoryRecord.getOriginalJson(), purchaseHistoryRecord.getSignature());
                                        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                                            //消耗品 开始消耗
                                            consumePuchase(purchase, consumeImmediately);
                                            //确认购买交易
                                            if (!purchase.isAcknowledged()) {
                                                acknowledgePurchase(purchase);
                                            }
                                            //TODO：这里可以添加订单找回功能，防止变态用户付完钱就杀死App的这种
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                });

    }
}