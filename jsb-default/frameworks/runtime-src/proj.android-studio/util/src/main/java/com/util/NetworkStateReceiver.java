package com.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.widget.Toast;


public class NetworkStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println("Network state changes");
        //检测API是不是小于21，因为到了API21之后getNetworkInfo(int networkType)方法被弃用
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {

            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //获取ConnectivityManager对象对应的NetworkInfo对象
            //获取WIFI连接的信息
            NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            //获取移动数据连接的信息
            NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            onNetWorkStateChanged(context, wifiNetworkInfo, dataNetworkInfo);
        } else {
            //这里的就不写了，前面有写，大同小异
           // System.out.println("API level 大于21");
            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //获取所有网络连接的信息
            Network[] networks = connMgr.getAllNetworks();
            //用于存放网络连接信息
            StringBuilder sb = new StringBuilder();
            //通过循环将网络信息逐个取出来
            NetworkInfo wifiNetworkInfo = null;
            NetworkInfo dataNetworkInfo = null;
            for (int i = 0; i < networks.length; i++) {
                //获取ConnectivityManager对象对应的NetworkInfo对象
                NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    wifiNetworkInfo = networkInfo;
                } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    dataNetworkInfo = networkInfo;
                } else {
                    //none.
                }

//                sb.append(networkInfo.getTypeName() + " connect is " + networkInfo.isConnected());
            }
            onNetWorkStateChanged(context, wifiNetworkInfo, dataNetworkInfo);
//            Toast.makeText(context, sb.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void onNetWorkStateChanged(Context context, NetworkInfo wifiNetworkInfo, NetworkInfo dataNetworkInfo) {


        if (null != wifiNetworkInfo && wifiNetworkInfo.isConnected()) {
//            JavaUtil.onWifiConnected();
            Toast.makeText(context, "WiFi connected", Toast.LENGTH_SHORT).show();
        } else if (null != dataNetworkInfo && dataNetworkInfo.isConnected()) {
            Toast.makeText(context, "Mobile data connected", Toast.LENGTH_SHORT).show();
//            JavaUtil.onMobileDataConnected();
        } else {
            Toast.makeText(context, "Network disconnected", Toast.LENGTH_SHORT).show();
//            JavaUtil.onNetWorkDisconnected();
        }
    }
}