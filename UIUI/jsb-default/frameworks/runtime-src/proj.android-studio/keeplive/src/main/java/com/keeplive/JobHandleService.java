package com.keeplive;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

import com.keeplive.util.LogUtil;

@SuppressLint("NewApi")
public class JobHandleService extends JobService {

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i("JobService create");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.i("JobService start");
        return START_NOT_STICKY;
    }


    @Override
    public boolean onStartJob(JobParameters params) {
        LogUtil.i("Job start: id=" + params.getJobId());
        //Toast.makeText(getBaseContext(), "Job start: id=" + params.getJobId(), Toast.LENGTH_LONG).show();
        //启动注册的服务
        KeepLive.init(this).activeConfigService();

        jobFinished(params, false);
        KeepLive.init(this).startDaemon();
        return true;
    }


    @Override
    public boolean onStopJob(JobParameters params) {
        LogUtil.i("Job stop: id=" + params.getJobId());
        //Toast.makeText(getBaseContext(), "Job stop: id=" + params.getJobId(), Toast.LENGTH_LONG).show();
        //scheduleJob(getJobInfo(JOB_ID));
        return true;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i("JobService destroy");
    }
}
