package org.cocos2dx.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.fireball.cocos_demo.BuildConfig;

import org.cocos2dx.lib.Cocos2dxLocalStorage;

import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class AppUtil {


    public static String getPackageName(Context context) {
        if (null == context) {
            return "";
        }
        return context.getPackageName();
    }

    public static String getChannel(Context context) {
        if (null == context) {
            return null;
        }
        String channel = null;
        channel = AppConfigUtils.getChannel(context);
        if (!TextUtils.isEmpty(channel)) {
            return channel;
        }

//        channel = getChannelFromApk(context);
//        if (!TextUtils.isEmpty(channel)) {
//            return channel;
//        }
//
//        PackageManager pManager = context.getPackageManager();
//        String pkg = context.getPackageName();
//        try {
//            ApplicationInfo applicationInfo = pManager.getApplicationInfo(pkg,
//                    PackageManager.GET_META_DATA);
//            channel = applicationInfo.metaData.getString("apk_channel");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        channel = BuildConfig.apkChannel;
        return channel;
    }

    public static String getChannelFromApk(Context context) {
        String channel = "";
        ApplicationInfo appInfo = context.getApplicationInfo();
        String sourceDir = appInfo.sourceDir;
        ZipFile zipfile = null;
        String searchString = "apkchannel_";
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.contains(searchString)) {
                    int index = entryName.lastIndexOf(searchString);
                    channel = entryName.substring(index + searchString.length());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

//        Log.e("getChannelFromApk", "channel:" + channel);
        return channel;
    }

    public static void initChannel(Context context) {
        String channel = AppConfigUtils.getChannel(context);
        if (TextUtils.isEmpty(channel)) {
            channel = AppUtil.getChannel(context);
            // Log.e("channel=",channel);
            AppConfigUtils.setChannel(context, channel);
        }
    }


    public static void initVersionCode(Context context) {

        int currentVCode = 0;
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            currentVCode = packInfo.versionCode;
        } catch (Exception e) {
        }

        int oldVCode = AppConfigUtils.getVersionCode(context);
        if (currentVCode > oldVCode) {
            //CocosBridgeEngineUtil.trackEvent(String.format("native_update_%s_%s", oldVCode, currentVCode));
            AppConfigUtils.setVersionCode(context, currentVCode);
            AppConfigUtils.setChannel(context, null);

        }
    }

    private static void onVersionUpgrade() {
        Cocos2dxLocalStorage.init();
        Cocos2dxLocalStorage.removeItem("_res_search_path");

    }
}