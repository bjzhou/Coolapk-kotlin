package bjzhou.coolapk.app.services;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.List;

import bjzhou.coolapk.app.http.ApkDownloader;
import bjzhou.coolapk.app.http.HttpHelper;
import bjzhou.coolapk.app.model.UpgradeApkExtend;

/**
 * Created by bjzhou on 14-8-15.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class UpgradeService extends JobService implements Handler.Callback {
    private static final String TAG = "UpgradeService";

    private Handler mHandler = new Handler(this);

    @Override
    public boolean onStartJob(JobParameters params) {
        HttpHelper.getInstance(this).obtainUpgradeVersions(this, mHandler);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mHandler.removeCallbacksAndMessages(null);
        return false;
    }

    @Override
    public boolean handleMessage(Message msg) {
        List<UpgradeApkExtend> apks = (List<UpgradeApkExtend>) msg.obj;
        if (apks != null && apks.size() > 0) {
            for (final UpgradeApkExtend apk : apks) {
                int id = (int) apk.getApk().getId();
                ApkDownloader.getInstance(UpgradeService.this).download(id, apk.getApk().getApkname(),
                        apk.getTitle(), apk.getApk().getApkversionname(), new ApkDownloader.DownloadListener() {
                            @Override
                            public void onDownloading(int percent) {
                            }

                            @Override
                            public void onFailure(int errCode, String... err) {
                                Log.e(TAG, errCode + ":" + err[0]);
                            }

                            @Override
                            public void onDownloaded() {
                            }

                            @Override
                            public void onComplete() {
                                jobFinished(null, false);
                            }
                        });
            }
        }
        return true;
    }
}
