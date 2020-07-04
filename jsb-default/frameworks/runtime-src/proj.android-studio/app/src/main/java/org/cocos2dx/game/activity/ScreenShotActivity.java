package org.cocos2dx.game.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import org.cocos2dx.game.MyApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ScreenShotActivity extends Activity {
    MediaProjectionManager mediaProjectionManager;
    MediaProjection mediaProjection;
    ImageReader mImageReader;
    VirtualDisplay virtualDisplay1;
    DisplayMetrics displayMetrics;
    WindowManager windowManager;
    String name, path, payType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusNavigationBar();
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        path = intent.getStringExtra("path");
        payType = intent.getStringExtra("payType");
        requestCapturePermission();
    }

    private void hideStatusNavigationBar() {
        if (Build.VERSION.SDK_INT > 18) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            );
        }
    }


    public void requestCapturePermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //5.0 之后才允许使用屏幕截图
            return;
        }

        mediaProjectionManager = (MediaProjectionManager)
                getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent(),
                1);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("ScreenShotActivity", "onActivityResult"+"requestCode"+requestCode+"\n resultCode="+resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
//        try {
//            Thread.sleep(1000*5);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        if(resultCode == -1){
            captureScreen();
        }else if(resultCode == 0){
            finish();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void captureScreen() {
        displayMetrics = new DisplayMetrics();
        windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        mImageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2);
        virtualDisplay1 = mediaProjection.createVirtualDisplay("screen-mirror", width, height, displayMetrics.densityDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mImageReader.getSurface(), null, null);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Image image = mImageReader.acquireLatestImage();
                if (image != null) {
                    final Image.Plane[] planes = image.getPlanes();
                    final ByteBuffer buffer = planes[0].getBuffer();
                    int width = image.getWidth();
                    int height = image.getHeight();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * width;
                    Bitmap bmp = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
                    bmp.copyPixelsFromBuffer(buffer);
                    Bitmap bitmap = Bitmap.createScaledBitmap(bmp, bmp.getWidth(), bmp.getHeight(), false);
                    if (bitmap != null) {
                        long spendtimes = System.currentTimeMillis() - MyApplication.time;
                        Log.e("ScreenShotActivity", "bitmap成功时间" + spendtimes);
                    }
                    setSavePic(bitmap);
                    bitmap.recycle();
                    image.close();
                }
            }
        }, 500);
    }

    private void setSavePic(Bitmap bitmap) {//保存截屏到本地
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            if (path == null) {
                path = Environment.getExternalStorageDirectory().getPath() + "/截屏";
            } else {
                path = Environment.getExternalStorageDirectory().getPath() + "/" + path;
            }
            File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }
            if (name == null) {
                name = System.currentTimeMillis() + "";
            }
            File file1 = new File(file, name + ".jpg");
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file1);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(file1);
                intent.setData(uri);
                sendBroadcast(intent);
                finish();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


