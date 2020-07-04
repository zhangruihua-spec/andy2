package org.cocos2dx.game.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cocos2dx.game.R;
import com.example.lib.QRCodeUtil.QRCodeUtil;
import com.util.EngineUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by WangYi on 2018/12/1.
 */

public class SaveImgAvtivity extends Activity implements View.OnClickListener{

    private ImageView mImageView;
    private ImageView mBg;
    private ImageView mLoading;
    private TextView mText;
    private ObjectAnimator anim;
    public static boolean isSuccess;
    public String bgToUrl;
    public String viewToUrl;
    public String url ;
    public String logoUrl ;

    private Context mContext;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 1;


    public SaveImgAvtivity() {
        mContext = this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_logic_christmas_active);

        anim = ObjectAnimator.ofFloat(mLoading, "rotation", 0, 360);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(-1);
        anim.setDuration(1000);
        anim.start();


        Intent intent = getIntent();
        if (null != intent) {
            bgToUrl = intent.getStringExtra("bgToUrl");
            viewToUrl = intent.getStringExtra("viewToUrl");
        }

        mImageView = (ImageView) findViewById(R.id.erweima);
        mBg = (ImageView) findViewById(R.id.bg);

        mLoading = (ImageView) findViewById(R.id.loading);
        mText = (TextView) findViewById(R.id.textView);


        //生成二维码
        mImageView.setImageBitmap(QRCodeUtil.createQRCodeBitmap(viewToUrl, 400, getLocalBitmap(logoUrl), 40));


        findViewById(R.id.root_view).setOnClickListener(this);

        loadView();

    }

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {


            takeScreenShot(SaveImgAvtivity.this);
            android.util.Log.e("TestLog", "permission failed.....");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                Log.i("SaveImgAvtivity", "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
                if (grantResults[i] == 0) {
                    takeScreenShot(SaveImgAvtivity.this);
                } else {
                    Toast.makeText(mContext, "请打开相册访问权限,才能保存图片。", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public void loadView() {

        new ScreenShotAsyncTask().execute();
    }

    private boolean canClickFinish = false;
    @Override
    public void onClick(View v) {
        if(canClickFinish){
            finish();
        }
    }

    private class ScreenShotAsyncTask extends AsyncTask<Void, Void, Void> {


        private Bitmap bitmap;

        @Override
        protected Void doInBackground(Void... voids) {


            if (!TextUtils.isEmpty(bgToUrl)) {
                bitmap = getHttpBitmap(bgToUrl);
            } else {
                //从本地取图片
                bitmap = getLocalBitmap(url);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            mBg.setImageBitmap(bitmap);

            if (viewToUrl == null) {
                Toast.makeText(mContext, "保存图片失败，请检查网络。", Toast.LENGTH_SHORT).show();
            } else {
                mLoading.setVisibility(View.INVISIBLE);
                mText.setVisibility(View.INVISIBLE);
                anim.cancel();
//                takeScreenShot(SaveImgAvtivity.this);
                verifyStoragePermissions(SaveImgAvtivity.this);
            }
        }
    }

    /**
     * 加载本地图片
     *
     * @param url
     * @return
     */
    public Bitmap getLocalBitmap(String url) {
        try {
            InputStream is = getAssets().open(url);
            return BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            Log.e("TAG", "MainActivity 1e = " + e);
        }
        return null;
    }

    /**
     * 从服务器取图片
     *
     * @param url
     * @return
     */
    public Bitmap getHttpBitmap(String url) {
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            Log.d("SaveImgAvtivity", url);
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setConnectTimeout(0);
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 进行截取屏幕
     *
     * @param pActivity
     * @return
     */
    public void takeScreenShot(Activity pActivity) {
        Bitmap bitmap = null;
        View view = pActivity.getWindow().getDecorView();


//        // 设置是否可以进行绘图缓存
//        view.setDrawingCacheEnabled(true);
//        // 如果绘图缓存无法，强制构建绘图缓存
//        view.buildDrawingCache();
//        // 返回这个缓存视图
//        bitmap = view.getDrawingCache();
//
        bitmap = getViewBitmap(view);

        if (null == bitmap) {
            android.util.Log.e("TestLog", "create screen shot error.......");
            return;
        }
        // 获取状态栏高度
        Rect frame = new Rect();
        // 测量屏幕宽和高
        view.getWindowVisibleDisplayFrame(frame);
        int stautsHeight = frame.top;
        int width = pActivity.getWindowManager().getDefaultDisplay().getWidth();
        int height = pActivity.getWindowManager().getDefaultDisplay().getHeight();
        // 根据坐标点和需要的宽和高创建bitmap
        if (width > bitmap.getWidth() || height > bitmap.getHeight()) {
            width = bitmap.getWidth();
            height = bitmap.getHeight();
        }
        bitmap = Bitmap.createBitmap(bitmap, 0, stautsHeight, width, height);

        mBg.setImageBitmap(bitmap);


        saveImage(bitmap);
    }

    public Bitmap getViewBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;


    }

    //保存文件到指定路径
    public void saveImage(Bitmap bmp) {
        // 首先保存图片
//        String storePath = "sdcard/";
//        File appDir = new File(storePath);
//        if (!appDir.exists()) {
//            appDir.mkdir();
//        }
        String fileName = System.currentTimeMillis() + ".jpg";

        String path = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                + "/" + fileName;

        File extDownloadFolderPath = new java.io.File(path);


        File file = extDownloadFolderPath;//new File(appDir, fileName);

        try {
            FileOutputStream fos;
            fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();

            //把文件插入到系统图库
            //MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);

            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            if (isSuccess) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        canClickFinish = true;
                        EngineUtil.startWechatEvent("");
                    }
                }, 3000);
            } else {
                Toast.makeText(mContext, "保存图片失败，请检查网络。", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
