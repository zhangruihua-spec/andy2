package com.keeplive.util;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        try {
            ActivityManager myAM = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(100);
            int size = myList.size();
            if (size <= 0) {
                return false;
            }
            for (int i = 0; i < size; i++) {
                String mName = myList.get(i).service.getClassName();
                if (mName.equals(serviceName)) {
                    isWork = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isWork;
    }


    public static String joinString(List<String> stringList, String separator) {
        if (TextUtils.isEmpty(separator) || null == stringList || stringList.size() == 0) {
            return "";
        }
        StringBuilder joinText = new StringBuilder();
        for (String string : stringList) {
            joinText.append(string);
            joinText.append(separator);
        }
        return joinText.substring(0, joinText.length() - separator.length());
    }

    public static List<String> splitString(String content, String separator) {
        List<String> stringArray = new ArrayList<>();
        if (!TextUtils.isEmpty(content)) {
            String[] array = content.split(separator);
            if (null != array) {
                stringArray = Arrays.asList(array);
            }
        }
        return stringArray;
    }
}
