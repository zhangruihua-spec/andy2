package org.cocos2dx.game.util;

import android.content.Context;
import android.content.Intent;

import org.cocos2dx.game.activity.ScreenShotActivity;


public class ShotClass {
    private static class SingleHolder {
        static final ShotClass shot = new ShotClass();
    }

    public static ShotClass getInstance() {
        return SingleHolder.shot;
    }

    public void createShot(Context context, String path, String name) {
        Intent intent = new Intent(context, ScreenShotActivity.class);
        intent.putExtra("path", path);
        intent.putExtra("name", name);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}

