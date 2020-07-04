package com.keeplive;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.keeplive.service.daemon.LocalService;
import com.keeplive.util.LogUtil;
import com.keeplive.service.daemon.RemoteService;
import com.keeplive.util.Utils;
import com.keeplive.service.screen.ScreenListeningService;

import java.util.List;

public class KeepLive {

    private static final int JOB_ID = 9900;
    private static final long SERVICE_ACTIVE_INTERVAL = 5 * 1000;//最小启动间隔，防止外部密集调用。
    private static KeepLive sIntance;
    private Context context;
    private Preference mPreference;

    public static final KeepLive init(Context context) {
        if (null == sIntance) {
            sIntance = new KeepLive(context);
        }
        return sIntance;
    }

    public static final KeepLive getInstance() {
        if (null == sIntance) {
            throw new NullPointerException("Please call KeepLive.init() first");
        }
        return sIntance;
    }

    private KeepLive(Context context) {
        this.context = context;
        mPreference = Preference.init(context);
    }


    public KeepLive config(Config config) {
        mPreference.saveServiceNames(config.getServiceNames());
        mPreference.saveInterval(config.getInterval());
        LogUtil.setTag("KeepLive");
        LogUtil.setDebug(config.isShowLog());
        return this;
    }

    /**
     * JobScheduler来执行一些需要满足特定条件但不紧急的后台任务，APP利用JobScheduler来执行这些特殊的后台任务时来减少电量的消耗。
     */
    public void startDaemon() {
        //自动自身守护服务
        activeKeepLiveService();
        //计划下一个Job
        scheduleJob();
    }

    /**
     * Send job to the JobScheduler.
     */
    private void scheduleJob() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                if (null != jobScheduler) {
                    JobInfo jobInfo = buildJobInfo();
                    jobScheduler.cancel(jobInfo.getId());
                    int result = jobScheduler.schedule(jobInfo);
                    if (result == JobScheduler.RESULT_SUCCESS) {
                        LogUtil.i("Schedule job success: id=" + jobInfo.getId());
                    } else {
                        LogUtil.e("Schedule job fail, id=" + jobInfo.getId());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean hasPendingJob(int jobId) {
        boolean hasPendingJob = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            if (null != jobScheduler) {
                List<JobInfo> pendingJobs = jobScheduler.getAllPendingJobs();
                for (int i = 0; i < pendingJobs.size(); i++) {
                    if (pendingJobs.get(i).getId() == jobId) {
                        hasPendingJob = true;
                        break;
                    }
                }
            }
        }
        return hasPendingJob;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private JobInfo buildJobInfo() {
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, new ComponentName(context, JobHandleService.class));
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE);//有无网络都执行
        builder.setPersisted(true);//设备重启后执行
        builder.setRequiresCharging(true);//充电时执行
        builder.setRequiresDeviceIdle(true);//设备空闲时执行
        long interval = mPreference.getInterval();
        builder.setMinimumLatency(interval);
        builder.setOverrideDeadline(interval);
        //builder.setPeriodic(PERIOD_INTERVAL);//间隔时间--周期。7.0以上需要设置15分钟以上，那就所有平台都用15分钟把。
        return builder.build();
    }


    /**
     * 启动外部调用注册进来的服务
     */
    public void activeConfigService() {
        long lastKeepLiveTime = Preference.getInstance().getKeepLiveLastTime();
        if (System.currentTimeMillis() - lastKeepLiveTime > SERVICE_ACTIVE_INTERVAL) {
            List<String> serviceNames = Preference.getInstance().getServiceNames();
            for (int i = 0; i < serviceNames.size(); i++) {
                String servicename = serviceNames.get(i);
                try {
                    Class service = this.context.getClassLoader().loadClass(servicename);
                    if (service != null) {
                        //if (!Utils.isServiceWork(this.context, servicename)) {
                        //重启service
                        this.context.startService(new Intent(this.context, service));
                        //}
                    }
                } catch (Exception e) {
                    LogUtil.e(e);
                }
            }
        }
        Preference.getInstance().saveKeepLiveLastTime(System.currentTimeMillis());
    }

    /**
     * 启动自身守护服务
     */
    public void activeKeepLiveService() {
        activeDaemonService();
        activeScreenListeningService();
    }


    /**
     * 确保双进程守护运行
     */
    private void activeDaemonService() {
        try {
            boolean isLocalServiceWork = Utils.isServiceWork(context, LocalService.class.getName());
            boolean isRemoteServiceWork = Utils.isServiceWork(context, RemoteService.class.getName());
            if (!isLocalServiceWork || !isRemoteServiceWork) {
                context.startService(new Intent(context, LocalService.class));
                context.startService(new Intent(context, RemoteService.class));
            }
        } catch (Exception e) {
            LogUtil.e(e);
        }
    }

    /**
     * 确保1像素Activity的开关屏服务正常运行
     */
    private void activeScreenListeningService() {
        try {
            boolean isScreenListeningServiceWork = Utils.isServiceWork(context, ScreenListeningService.class.getName());
            if (!isScreenListeningServiceWork) {
                context.startService(new Intent(context, ScreenListeningService.class));
            }
        } catch (Exception e) {
            LogUtil.e(e);
        }
    }

    /**
     * 安卓8.0之后，不允许在后台直接调用startService，需要用startForegroundService方法。
     * 并且被启动的Service必须调用startForeground方法把自己设置位前台服务，否则会出现ANR。
     *
     * @param intent
     */
    private void startService(Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

}
