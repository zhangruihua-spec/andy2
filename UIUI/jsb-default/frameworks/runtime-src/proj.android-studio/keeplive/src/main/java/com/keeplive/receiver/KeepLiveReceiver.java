package com.keeplive.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.keeplive.KeepLive;
import com.keeplive.util.LogUtil;

/**
 * 安卓8.0以后，很多广播已经变成了隐式广播，无法通过静态注册监听。
 * 因此，该广播接收器只针对安卓8.0以下系统进行处理。
 * 安卓8.0以上使用JobScheduler方案替代。
 */
public class KeepLiveReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        LogUtil.d("broadcast: " + action);
        //Toast.makeText(context, "Rcv broadcast: " + action, Toast.LENGTH_LONG).show();

        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //android 8.0以上无法直接在后台StartService
            } else {
                KeepLive.init(context).startDaemon();
            }
        } else {
            KeepLive.init(context).startDaemon();
        }

    }


}
