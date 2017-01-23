package bjzhou.coolapk.app.net;

import android.Manifest;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.SparseArray;

import bjzhou.coolapk.app.model.Apk;
import bjzhou.coolapk.app.ui.base.BaseActivity;

/**
 * Created by bjzhou on 14-8-18.
 */
public class ApkDownloader {
    private static final String TAG = "Downloader";
    private static ApkDownloader instance;

    private SparseArray<DownloadMonitor> downloadingIds = new SparseArray<>();
    private boolean mPermissionGranted = false;

    private ApkDownloader() {
    }

    public static ApkDownloader getInstance() {
        if (instance == null) {
            instance = new ApkDownloader();
        }
        return instance;
    }

    public static boolean checkNetworkState(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }


    public void checkPermission(BaseActivity activity) {
        activity.checkPermissions(new BaseActivity.PermissionListener() {
            @Override
            public void onResult(String permission, boolean succeed) {
                mPermissionGranted = succeed;
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public void downloadAndInstall(Context context, Apk apk, DownloadMonitor.DownloadListener _listener) {
        if (!checkNetworkState(context)) {
            return;
        }

        if (!mPermissionGranted) {
            return;
        }

        if (downloadingIds.indexOfKey(apk.getId()) != -1) {
            if (_listener != null) {
                downloadingIds.get(apk.getId()).addDownloadListener(_listener);
            }
            return;
        }

        Context appContext = context.getApplicationContext();
        DownloadMonitor monitor = new DownloadMonitor(appContext, apk);
        monitor.setFinishListener(new DownloadMonitor.FinishListener() {
            @Override
            public void onFinish(Apk apk) {
                downloadingIds.remove(apk.getId());
            }
        });
        if (_listener != null) {
            monitor.addDownloadListener(_listener);
        }
        downloadingIds.put(apk.getId(), monitor);
        monitor.start();
    }

    public boolean isDownloading(int id) {
        return downloadingIds.indexOfKey(id) != -1;
    }

    public void addListener(int id, DownloadMonitor.DownloadListener listener) {
        downloadingIds.get(id).addDownloadListener(listener);
    }
}
