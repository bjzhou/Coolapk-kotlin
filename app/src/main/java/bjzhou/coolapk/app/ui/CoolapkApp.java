package bjzhou.coolapk.app.ui;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Build;

import bjzhou.coolapk.app.services.UpgradeService;

/**
 * Created by bjzhou on 14-7-29.
 */
public class CoolapkApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            silentUpgrade();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void silentUpgrade() {
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(new JobInfo.Builder(0, new ComponentName(this, UpgradeService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPeriodic(60 * 60 * 1000)
                .setPersisted(true)
                .build());
    }
}
