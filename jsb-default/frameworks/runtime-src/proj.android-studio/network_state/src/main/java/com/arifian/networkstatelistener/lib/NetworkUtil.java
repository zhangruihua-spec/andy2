package com.arifian.networkstatelistener.lib;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by faqih on 24/05/17.
 */

public class NetworkUtil {
    public static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        dumpNetworkInfo(activeNetwork);
        return activeNetwork != null &&
                activeNetwork.isConnected();

    }

    public static boolean isConnecting(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        dumpNetworkInfo(activeNetwork);
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public static void dumpNetworkInfo(NetworkInfo info) {

        if (null != info) {

            int type = info.getType();
            String extraInfo = info.getExtraInfo();
            String sub_name = info.getSubtypeName();
            Log.e("NetworkUtil", String.format("type: %s, extraInfo: %s, subName: %s", type, extraInfo, sub_name));

        } else {
            Log.e("NetworkUtil", "unknow network......");
        }
    }

    private static void init(Activity activity) {

        if (NetworkUtil.isConnected(activity)) {

            Toast.makeText(activity, "网络已经连接", Toast.LENGTH_LONG).show();

        } else if (NetworkUtil.isConnecting(activity)) {
            Toast.makeText(activity, "多络连接中......", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(activity, "网络已断开连接", Toast.LENGTH_LONG).show();

        }

        /**
        String packageStr = activity.getApplicationContext().getPackageName();

        ViewGroup nslRoot = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).findViewWithTag("nsl_root");
        if (nslRoot != null) {
            ViewGroup nslRootParent = (ViewGroup) nslRoot.getParent();

            int connectingId = activity.getResources().getIdentifier("nsl_connecting", "string", packageStr);
            connectingId = connectingId == 0 ? R.string.nsl_connecting : connectingId;

            int noInternetId = activity.getResources().getIdentifier("nsl_no_internet", "string", packageStr);
            noInternetId = noInternetId == 0 ? R.string.nsl_no_internet : noInternetId;

            int bgColorId = activity.getResources().getIdentifier("nsl_bg", "color", packageStr);
            int textColorId = activity.getResources().getIdentifier("nsl_text", "color", packageStr);

            if (Build.VERSION.SDK_INT >= 23) {
                bgColorId = bgColorId == 0 ? activity.getColor(R.color.nsl_bg) : activity.getColor(bgColorId);
                textColorId = textColorId == 0 ? activity.getColor(R.color.nsl_text) : activity.getColor(textColorId);
            } else {
                bgColorId = bgColorId == 0 ? activity.getResources().getColor(R.color.nsl_bg) : activity.getResources().getColor(bgColorId);
                textColorId = textColorId == 0 ? activity.getResources().getColor(R.color.nsl_text) : activity.getResources().getColor(textColorId);
            }

            int paddingId = activity.getResources().getIdentifier("nsl_padding", "dimen", packageStr);
            int padding = (paddingId == 0 ? activity.getResources().getDimensionPixelSize(R.dimen.nsl_padding) : activity.getResources().getDimensionPixelSize(paddingId));

            ViewGroup nslNotificationRoot = (ViewGroup) nslRootParent.findViewById(R.id.nsl_notification_root);
            if (NetworkUtil.isConnected(activity)) {
                if (nslNotificationRoot != null) {
                    nslRootParent = (ViewGroup) nslNotificationRoot.getParent();
                    nslNotificationRoot.removeView(nslRoot);

                    nslRootParent.removeView(nslNotificationRoot);

                    nslRootParent.addView(nslRoot);
                }
            } else if (NetworkUtil.isConnecting(activity)) {
                if (nslNotificationRoot == null) {
                    nslRootParent.removeView(nslRoot);

                    View view = LayoutInflater.from(nslRootParent.getContext()).inflate(R.layout.nsl, nslRootParent, false);
                    nslRootParent.addView(view);
                    nslNotificationRoot = (ViewGroup) view.findViewById(R.id.nsl_notification_root);

                    nslNotificationRoot.addView(nslRoot);
                }
                TextView nslTextView = (TextView) nslNotificationRoot.findViewById(R.id.nsl_text);
                nslTextView.setText(connectingId);
                nslTextView.setBackgroundColor(bgColorId);
                nslTextView.setTextColor(textColorId);
                nslTextView.setPadding(0, padding, 0, padding);
            } else {
                if (nslNotificationRoot == null) {
                    nslRootParent.removeView(nslRoot);

                    View view = LayoutInflater.from(nslRootParent.getContext()).inflate(R.layout.nsl, nslRootParent, false);
                    nslRootParent.addView(view);
                    nslNotificationRoot = (ViewGroup) view.findViewById(R.id.nsl_notification_root);

                    nslNotificationRoot.addView(nslRoot);
                }

                TextView nslTextView = (TextView) nslNotificationRoot.findViewById(R.id.nsl_text);
                nslTextView.setText(noInternetId);
                nslTextView.setBackgroundColor(bgColorId);
                nslTextView.setTextColor(textColorId);
                nslTextView.setPadding(0, padding, 0, padding);
            }
        }
         **/
    }
}
