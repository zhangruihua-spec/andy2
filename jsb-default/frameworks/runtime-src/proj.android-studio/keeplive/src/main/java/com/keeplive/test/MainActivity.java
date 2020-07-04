package com.keeplive.test;

import android.app.Activity;
import android.os.Bundle;

import com.keeplive.Config;
import com.keeplive.KeepLive;
import com.keeplive.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        KeepLive.init(this)
                .config(new Config.Builder()
                        .showLog(true)
                        .keepLiveInterval(15 * 1000)
                        .appendService(MyAlarmService.class)
                        .build())
                .startDaemon();

    }
}
