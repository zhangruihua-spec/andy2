package org.cocos2dx.game.util;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.widget.Toast;

import com.data.http.DownloadTask;
import com.data.http.HttpDataLoadCallback;
import com.data.http.HttpPostDataTask;
import com.util.AppConfigUtils;
import com.util.BitmapUtil;
import com.util.observable.ViewUtil;

import org.cocos2dx.game.activity.MainActivity;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.UUID;

public class ImageHelper {

    private WeakReference<Activity> activityReference;

    public ImageHelper(Activity activity) {
        activityReference = new WeakReference<Activity>(activity);
    }

    public void downloadImage2Photo(String url) {
        if (activityReference == null || activityReference.get() == null) {
            return;
        }

        int permission = PermissionChecker.checkSelfPermission(activityReference.get(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            downloadImage(url);
        } else {
            // 针对targetVersion<23,WRITE_EXTERNAL_STORAGE权限无法启动权限弹框
            // 会有一个黑影闪现，又立刻消失的现象
            // 所以这里直接Toast提示
//                ActivityCompat.requestPermissions(
//                        MainActivity.this,
//                        PERMISSIONS_STORAGE,
//                        REQUEST_PERMISSIONS_EXTERNAL_STORAGE
//                );
            showToast("保存图片功能被禁用，请打开手机权限或截屏保存");
        }
    }

    private void downloadImage(String url) {
        DownloadTask.get().download(url, "ogame", new DownloadTask.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(File saveFile) {
                if (saveFile == null) {
                    showToast("保存失败，请重新尝试");
                    return;
                }

                try {
                    Log.i("ImageHelper", "path=" + saveFile.getAbsolutePath());
                    if (activityReference != null && activityReference.get() != null) {
                        activityReference.get().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(saveFile.getPath()))));
                    }
                } catch (Exception e) {
                }
                showToast("保存图片成功");
            }

            @Override
            public void onDownloading(int progress) {

            }

            @Override
            public void onDownloadFailed() {
                showToast("保存失败，请重新尝试");
            }
        });
    }

    private void showToast(final String text) {
        ViewUtil.runOnUiThread(new Runnable() {
            public void run() {
                if (activityReference != null && activityReference.get() != null) {
                    Toast.makeText(activityReference.get(), text, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void handleUpload(Uri picUri) {
        if (picUri == null) return;
        String picPath = BitmapUtil.getRealPathFromUri(activityReference.get(), picUri);
        if (picPath == null) return;
        Log.d("cocos2d-x", "图片地址 ：" + picPath);

        File picFile = new File(picPath);
        Log.d("cocos2d-x", "图片大小 ：" + picFile.length());
        if (picFile.length() > 10 * 1024 * 1024) {
            Toast.makeText(activityReference.get(), "图片太大无法发送", Toast.LENGTH_SHORT).show();
            return;
        }

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picPath, opts);
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = 1;
        //进行处理
        //Log.d("cocos2d-x", "opts.outWidth = " + opts.outWidth + ", opts.outHeight=" + opts.outHeight);

        final Bitmap bitmap = BitmapFactory.decodeFile(picPath, opts);

        //Log.d("cocos2d-x", "bitmap.outWidth = " + bitmap.getWidth() + ", bitmap.outHeight=" + bitmap.getHeight());

        String url = Uri.parse(AppConfigUtils.getStringFromPrefenence(activityReference.get(), "_common_host_server") + "/api/chat/v1/chat/up_info")
                .buildUpon().build().toString();
        new HttpPostDataTask(activityReference.get(), url, "", new HttpDataLoadCallback() {
            @Override
            public void onDataLoadStart() {
            }

            @Override
            public void onDataLoadComplete(int status, String msg, JSONObject object) {

                if (status == 0 && null != object) {
                    String path_prefix = object.optString("path_prefix");
                    String token = object.optString("token");
                    Log.d("cocos2d-x", "token=" + token);

                    final String uploadKey = path_prefix + "/" + UUID.randomUUID() + ".png";
                    Log.i("cocos2d-x", "key=" + uploadKey);



                } else {

                }
            }
        }).execute();
    }

}
