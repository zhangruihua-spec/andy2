package org.cocos2dx.game.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;


import com.util.observable.ViewUtil;

import org.cocos2dx.lib.Cocos2dxActivity;

import org.json.JSONObject;

import java.util.ArrayList;



/**
 * Created by WangYi on 2017/5/8.
 */

public class BaseActivity extends Cocos2dxActivity {

    protected static final int REQUEST_CODE_PAYMENT = 0x00abc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }




    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {


            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }




    protected void showLoading(final String text) {
        ViewUtil.getUIThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.getContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }



    //支付结果的回调,0 成功 1 失败
   // public static native void nativePaymentCallback(int state);
}
