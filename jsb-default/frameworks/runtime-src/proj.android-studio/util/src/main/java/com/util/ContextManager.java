package com.util;

import android.content.Context;

/**
 * Created by WangYi on 2018/5/17
 */
public class ContextManager {

    private static Context sContext = null;

    public static Context getAppContext() {
        if (sContext == null) {
            throw new IllegalStateException("please call setAppContext when application onCreate!");
        }
        return sContext;
    }

    public static void setAppContext(Context context) {
        sContext = context;
    }
}
