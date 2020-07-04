package com.util;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.View;

@SuppressWarnings("deprecation")
public class ViewCompatUtil {

    public static void setBackground(View view, Drawable background) {
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            SDK16.setBackground(view, background);
        } else {
            view.setBackgroundDrawable(background);
        }
    }
    
    @TargetApi(16)
    static class SDK16 {

        public static void setBackground(View view, Drawable background) {
            view.setBackground(background);
        }

    }
}
