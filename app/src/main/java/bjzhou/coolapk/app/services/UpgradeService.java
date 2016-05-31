package bjzhou.coolapk.app.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.util.List;

import bjzhou.coolapk.app.http.ApkDownloader;
import bjzhou.coolapk.app.http.HttpHelper;
import bjzhou.coolapk.app.model.UpgradeApkExtend;

/**
 * Created by bjzhou on 14-8-15.
 */
public class UpgradeService extends Service implements Handler.Callback {
    private static final String TAG = "UpgradeService";

    private Handler mHandler = new Handler(this);

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State state = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if (state != NetworkInfo.State.CONNECTED) {
            stopSelf();
            return;
        }

        HttpHelper.getInstance(this).obtainUpgradeVersions(this, mHandler);
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
                                stopSelf();
                            }
                        });
            }
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
