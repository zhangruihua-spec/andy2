package com.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import android.widget.Toast;

import com.util.observable.ViewUtil;

import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class AppUtil {

    /**
     * 通过包名获取应用程序的名称。
     *
     * @param context     Context对象。
     * @param packageName 包名。
     * @return 返回包名所对应的应用程序的名称。
     */
    public static String getProgramNameByPackageName(Context context,
                                                     String packageName) {
        PackageManager pm = context.getPackageManager();
        String name = null;
        try {
            name = pm.getApplicationLabel(
                    pm.getApplicationInfo(packageName,
                            PackageManager.GET_META_DATA)).toString();
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }


    public static void startAppByPackageName(final Context context, String pkg) {
        PackageManager pm = context.getPackageManager();
        Intent launchIntent = pm.getLaunchIntentForPackage(pkg);
        if (null != launchIntent) {
            context.startActivity(launchIntent);
        }
        else {
            ViewUtil.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "无法启动", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static boolean checkApkExist(Context context, String packageName) {
        if (null == context || TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

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

        channel = getChannelFromApk(context);
        if (!TextUtils.isEmpty(channel)) {
            return channel;
        }

        PackageManager pManager = context.getPackageManager();
        String pkg = context.getPackageName();
        try {
            ApplicationInfo applicationInfo = pManager.getApplicationInfo(pkg,
                    PackageManager.GET_META_DATA);
            channel = applicationInfo.metaData.getString("apk_channel");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
