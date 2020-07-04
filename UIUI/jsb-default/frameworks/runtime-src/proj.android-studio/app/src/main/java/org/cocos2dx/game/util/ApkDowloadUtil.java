package org.cocos2dx.game.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import com.util.observable.CommandObservable;

import org.cocos2dx.game.MyApplication;
import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxJavascriptJavaBridge;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import bridge.ObservableManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
public class ApkDowloadUtil {

    public static ApkDowloadUtil dutil;

    public static ApkDowloadUtil getInstance(){
        if(dutil == null){
            dutil = new ApkDowloadUtil();
        }
        return dutil;
    }

    /**
     *更新新的apk
     */
    private long tryagaintime = 0;
    public void updateNewApk(final String version , final String apkurl){

       String dowloadpath =  getDowloadPath();


        if(!TextUtils.isEmpty(dowloadpath)){

            //final File apkfile = new File(dowloadpath,version+".apk");
            final long startsPoint = getFileStart(version) > 0 ? getFileStart(version)-1 : getFileStart(version);

            final DownloadListener downloadListener = new DownloadListener() {
                @Override
                public void start(long max) {

                }

                @Override
                public void loading(int progress,long max) {
                    CommandObservable observable = ObservableManager.getInstance().getObservable(CommandObservable.class);
                    if (null != observable) {
                        observable.setCommand("message://updateHotProgress?progress="+progress+"&max="+max);
                        observable.notifyChanged();
                    }
                  //  Cocos2dxJavascriptJavaBridge.evalString(String.format("cc.updateHotProgress('%s,%s')", String.valueOf(progress),String.valueOf(max)));
                }

                @Override
                public void complete(String path) {
                    tryagaintime =0;
                    CommandObservable observable = ObservableManager.getInstance().getObservable(CommandObservable.class);
                    if (null != observable) {
                        observable.setCommand("message://InstallNewVersionApk?path="+path);
                        observable.notifyChanged();
                    }
                }

                @Override
                public void fail(int code, String message) {

                }

                @Override
                public void loadfail(String message) {
                    Log.e("---","reset");
                    tryagaintime = tryagaintime+1;
                    Looper.prepare();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            updateNewApk(version,apkurl);
                        }
                    },tryagaintime*1000);
                    Looper.loop();//这种情况下，Runnable对象是运行在子线程中的，可以进行联网操作，但是不能更新UI
                }
            };

            Callback callback = new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    downloadListener.loadfail("err");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if(response.isSuccessful()){
                        tryagaintime = 0;
                        long length = response.body().contentLength();
                        if (length == 0){
                            // 说明文件已经下载完，直接跳转安装就好
                            downloadListener.complete(String.valueOf(getFile(version).getAbsoluteFile()));
                            return;
                        }
                        downloadListener.start(length+startsPoint);
                        // 保存文件到本地
                        InputStream is = null;
                        RandomAccessFile randomAccessFile = null;
                        BufferedInputStream bis = null;

                        byte[] buff = new byte[2048];
                        int len = 0;
                        try {
                            is = response.body().byteStream();
                            bis  =new BufferedInputStream(is);

                            File file = getFile(version);
                            // 随机访问文件，可以指定断点续传的起始位置
                            randomAccessFile =  new RandomAccessFile(file, "rwd");
                            randomAccessFile.seek (startsPoint);
                            while ((len = bis.read(buff)) != -1) {
                                randomAccessFile.write(buff, 0, len);
                            }

                            // 下载完成
                            downloadListener.complete(String.valueOf(file.getAbsoluteFile()));
                        } catch (Exception e) {
                            e.printStackTrace();
                            downloadListener.loadfail(e.getMessage());
                        } finally {
                            try {
                                if (is != null) {
                                    is.close();
                                }
                                if (bis != null){
                                    bis.close();
                                }
                                if (randomAccessFile != null) {
                                    randomAccessFile.close();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }else{
                        downloadListener.loadfail("err");
                    }



                }
            };


         download(apkurl,downloadListener,startsPoint, callback);

        }else{
            Cocos2dxJavascriptJavaBridge.evalString("cc.UpdateApkError()");
        }


    }

    private String getDowloadPath(){
        File cachedir =  MyApplication.getInstance().getCacheDir();
        if(cachedir!=null && cachedir.isDirectory()){
            return cachedir.getAbsolutePath();
        }
        return null;
    }
    private File getFile(String name) {
       // String root = Environment.getExternalStorageDirectory().getPath();
        File file = new File(getDowloadPath(),name+".apk");
        return file;
    }

    private long getFileStart(String name){
       // String root = Environment.getExternalStorageDirectory().getPath();
        File file = new File(getDowloadPath(),name+".apk");
        return file.length();
    }


    private Call download(String url, final DownloadListener downloadListener, final long startsPoint, Callback callback){
        Request request = new Request.Builder()
                .url(url)
                .header("RANGE", "bytes=" + startsPoint + "-")//断点续传
                .build();

        // 重写ResponseBody监听请求
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .body(new DownloadResponseBody(originalResponse, startsPoint, downloadListener))
                        .build();
            }
        };

        OkHttpClient.Builder dlOkhttp = new OkHttpClient.Builder()
                .addNetworkInterceptor(interceptor);
//        // 绕开证书
//        try {
//            setSSL(dlOkhttp);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // 发起请求
        Call call = dlOkhttp.build().newCall(request);
        call.enqueue(callback);
        return call;
    }


}
