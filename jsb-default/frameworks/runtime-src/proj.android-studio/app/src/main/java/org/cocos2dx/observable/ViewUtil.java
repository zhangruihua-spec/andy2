package org.cocos2dx.observable;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;

public class ViewUtil {

    private static final String TAG = "ViewUtil";
    private static volatile Handler sHandler;

    private static void ensureHandler() {
        if (sHandler == null) {
            synchronized (ViewUtil.class) {
                if (sHandler == null) {
                    sHandler = new Handler(Looper.getMainLooper());
                }
            }
        }
    }

    public boolean isOnUiThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    public static void runOnUiThread(Runnable runnable) {
        runOnUiThreadDelay(runnable, 0);
    }

    public static void runOnUiThreadDelay(Runnable runnable, long delayMillis) {
        try {
            ensureHandler();
            sHandler.postDelayed(new NoExceptionRunnable(runnable), delayMillis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void runOnUiThreadAtTime(Runnable runnable, long uptimeMillis) {
        try {
            ensureHandler();
            sHandler.postAtTime(new NoExceptionRunnable(runnable), uptimeMillis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Handler getUIThreadHandler() {
        ensureHandler();
        return sHandler;
    }

    /*
     * catch all exception in NoExceptionRunnable run
     * */
    private static class NoExceptionRunnable implements Runnable {

        private Runnable mRunnable;

        public NoExceptionRunnable(Runnable runnable) {
            mRunnable = runnable;
        }

        @Override
        public void run() {
            try {
                mRunnable.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void setBackground(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }


    public static void addView(View view, ViewGroup.LayoutParams params, WindowManager manager) {

        try {
            manager.addView(view, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean addViewToWindow(Context context, View view, ViewGroup.LayoutParams params) {

        if (null == view || null == context || !removeViewFromParent(view)) {
            return false;
        }

        final WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        addView(view, params, manager);
        return true;
    }

    public static boolean removeViewFromParent(View view) {

        if (null == view) {
            return false;
        }
        final ViewParent parent = view.getParent();
        if (parent != null) {
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(view);
            } else {
                return false;
            }
        }
        return true;
    }

    public static void removeView(Context context, View view) {

        final WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        if (null == manager) {
            return;
        }
        removeView(view, manager);
    }

    public static void removeView(View view, WindowManager manager) {

        try {
            manager.removeView(view);
        } catch (Exception e) {
           // Log.e(TAG, e);
        }
    }

}
