package com.arifian.networkstatelistener.lib;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import com.util.observable.CommandObservable;

import java.lang.reflect.Field;
import java.util.Map;

import bridge.ObservableManager;


public class NetworkStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        Activity activity = getActivity();
//        if(activity != null) {
//            Log.e("isConnected", NetworkUtil.isConnected(context)+"");
//        }
//
        CommandObservable observable = ObservableManager.getInstance().getObservable(CommandObservable.class);
        if (null != observable) {
            observable.setCommand("message://command?cmd=network_state_changed");
            observable.notifyChanged();
        }
    }


    private Activity getActivity() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);

            Map<Object, Object> activities = (Map<Object, Object>) activitiesField.get(activityThread);
            if (activities == null)
                return null;

            for (Object activityRecord : activities.values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    Activity activity = (Activity) activityField.get(activityRecord);
                    return activity;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
