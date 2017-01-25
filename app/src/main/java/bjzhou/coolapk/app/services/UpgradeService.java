package bjzhou.coolapk.app.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.List;

import bjzhou.coolapk.app.model.UpgradeApkExtend;
import bjzhou.coolapk.app.net.ApiManager;
import bjzhou.coolapk.app.net.ApkDownloader;
import io.reactivex.functions.Consumer;

/**
 * Created by bjzhou on 14-8-15.
 */
public class UpgradeService extends Service {
    private static final String TAG = "UpgradeService";
    private static final long SCAN_INTERVAL = 24 * 60 * 60 * 1000;

    private final static String ACTION_UPGRADE_SILENT = "bjzhou.coolapk.app.action.upgrade_silent";

    @Override
    public void onCreate() {
        super.onCreate();
/*        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent startSelf = new Intent(this, UpgradeService.class);
        startSelf.setAction(ACTION_UPGRADE_SILENT);
        PendingIntent pi = PendingIntent.getService(this, 0, startSelf,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pi);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + 500, SCAN_INTERVAL, pi);*/

        ApiManager.getInstance().obtainUpgradeVersions(this).subscribe(new Consumer<List<UpgradeApkExtend>>() {
            @Override
            public void accept(List<UpgradeApkExtend> upgradeApkExtends) throws Exception {
                for (UpgradeApkExtend upgradeApkExtend : upgradeApkExtends) {
                    upgradeApkExtend.getApk().setTitle(upgradeApkExtend.getTitle());
                    ApkDownloader.getInstance().downloadAndInstall(UpgradeService.this, upgradeApkExtend.getApk(), null);
                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (ACTION_UPGRADE_SILENT.equals(action)) {
            if (ApiManager.isWifiConnected(this)) {
                ApiManager.getInstance().obtainUpgradeVersions(this).subscribe(new Consumer<List<UpgradeApkExtend>>() {
                    @Override
                    public void accept(List<UpgradeApkExtend> upgradeApkExtends) throws Exception {
                        for (UpgradeApkExtend upgradeApkExtend : upgradeApkExtends) {
                            upgradeApkExtend.getApk().setTitle(upgradeApkExtend.getTitle());
                            ApkDownloader.getInstance().downloadAndInstall(UpgradeService.this, upgradeApkExtend.getApk(), null);
                        }
                    }
                });
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ApkDownloader.getInstance().stopDownload();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
