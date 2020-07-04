package com.keeplive.service.screen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

import com.keeplive.util.LogUtil;

public class KeepLiveActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
        LogUtil.i("KeepLiveActivityonCreate!!!");

        //显示窗体
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        LayoutParams params = window.getAttributes();
        params.height = 1;
        params.width = 1;
        params.x = 0;
        params.y = 0;
        window.setAttributes(params);

        KeepLiveActivityManager.getInstance(this).setKeepLiveActivity(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.i("KeepLiveActivityonNewIntent!!!");
        setIntent(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.i("KeepLiveActivityonDestroy!!!");
    }

}
