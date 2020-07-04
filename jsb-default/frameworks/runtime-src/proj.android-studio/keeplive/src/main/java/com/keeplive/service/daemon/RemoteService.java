package com.keeplive.service.daemon;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.keeplive.KeepLive;
import com.keeplive.RemoteConnection;
import com.keeplive.util.LogUtil;

/**
 * LocalService和RemoteService互相守护，通过bindService可以知道对方的存活状态。
 * 监听到对方被杀死，立马将其复活。
 */
public class RemoteService extends Service {

    private MyBinder binder;
    private MyServiceConnection connection;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;//绑定成功
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (binder == null) {
            binder = new MyBinder();//实例化MyBinder
        }
        connection = new MyServiceConnection();//实例化MyServiceConnection
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //绑定服务
        RemoteService.this.bindService(new Intent(RemoteService.this, LocalService.class),
                connection, Context.BIND_IMPORTANT);

        //通知显示，PM不赞同该做法，暂时屏蔽
//        PendingIntent contentIntent = PendingIntent.getService(this, 0, intent, 0);
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//        builder.setTicker("360")
//                .setContentIntent(contentIntent)
//                .setContentTitle("我是360，我怕谁!")
//                .setAutoCancel(true)
//                .setContentText("hehehe")
//                .setWhen(System.currentTimeMillis());
//
//        //把service设置为前台运行，避免手机系统自动杀掉改服务。
//        startForeground(startId, builder.build());

        //遇到LocalService和RemoteService重启的情况，可能是系统已经开始执行杀进程任务，需要重启注册的服务。
        KeepLive.init(this).activeConfigService();
        return START_STICKY;
    }

    /**
     * 绑定
     */
    public class MyBinder extends RemoteConnection.Stub {

        @Override
        public String getProcessName() throws RemoteException {
            return "LocalService";
        }
    }

    /**
     * 建立连接
     */
    public class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.i("建立连接成功！");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.i("LocalService服务被干掉了~~~~断开连接！");
            //Toast.makeText(RemoteService.this, "断开连接", Toast.LENGTH_SHORT).show();
            //启动被干掉的
            RemoteService.this.startService(new Intent(RemoteService.this, LocalService.class));//开启服务
            RemoteService.this.bindService(new Intent(RemoteService.this, LocalService.class),
                    connection, Context.BIND_IMPORTANT);//绑定服务
        }
    }
}
