package org.cocos2dx.javascript.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.adjust.sdk.AdjustReferrerReceiver;

import org.cocos2dx.bridge.MessageManager;
import org.cocos2dx.observable.CommonCmd;
import org.cocos2dx.util.AppConfigUtils;

public class InstallReferrerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        new AdjustReferrerReceiver().onReceive(context, intent);
        // Google Analytics receiver.
        //new CampaignTrackingReceiver().onReceive(context, intent);
        String referrer = intent.getStringExtra("referrer");
        if(!TextUtils.isEmpty(referrer)){
            Log.e("InstallReferrer=",referrer);
            AppConfigUtils.saveStringToPrefenence(context,AppConfigUtils.GP_REFFERERURL,referrer);
            CommonCmd observable = MessageManager.getInstance().getObservable(CommonCmd.class);
            if (null != observable) {
                observable.setCommand(String.format("message://installreferrerreceiver?referrer=%s", referrer));
                observable.notifyChanged();
            }
        }
    }
}
