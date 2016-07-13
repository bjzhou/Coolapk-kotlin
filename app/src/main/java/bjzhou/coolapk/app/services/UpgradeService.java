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
    private JobParameters mParams;
    private int mCompleteCount = 0;

    @Override
    public boolean onStartJob(JobParameters params) {
        mParams = params;
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
        final List<UpgradeApkExtend> apks = (List<UpgradeApkExtend>) msg.obj;
        if (apks != null && apks.size() > 0) {
            for (final UpgradeApkExtend apk : apks) {
                ApkDownloader.getInstance(UpgradeService.this).download(apk.getApk(), new ApkDownloader.DownloadListener() {
                            @Override
                            public void onDownloading(int percent) {
                            }

                            @Override
                            public void onFailure(int errCode, String... err) {
                                Log.e(TAG, errCode + ":" + err[0]);
                                mCompleteCount++;
                                if (mCompleteCount >= apks.size()) {
                                    jobFinished(mParams, false);
                                }
                            }

                            @Override
                            public void onDownloaded() {
                            }

                            @Override
                            public void onComplete() {
                                mCompleteCount++;
                                if (mCompleteCount >= apks.size()) {
                                    jobFinished(mParams, false);
                                }
                            }
                        });
            }
        }
        return true;
    }
}
