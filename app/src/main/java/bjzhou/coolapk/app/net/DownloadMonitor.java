package bjzhou.coolapk.app.net;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bjzhou.coolapk.app.R;
import bjzhou.coolapk.app.model.Apk;
import bjzhou.coolapk.app.ui.activities.NavActivity;
import bjzhou.coolapk.app.util.Constant;
import bjzhou.coolapk.app.util.StringHelper;
import eu.chainfire.libsuperuser.Shell;

/**
 * author: zhoubinjia
 * date: 2017/1/23
 */
public class DownloadMonitor extends Thread {
    private static final String TAG = DownloadMonitor.class.getSimpleName();
    private final PackageManager packageManager;
    private final DownloadManager downloadManager;
    private final NotificationManager notificationManager;
    private Apk apk;
    private Context context;
    private Handler handler = new Handler(Looper.getMainLooper());
    private List<DownloadListener> downloadListeners = new ArrayList<>();
    private FinishListener finishListener;

    public DownloadMonitor(Context context, Apk apk) {
        this.context = context.getApplicationContext();
        this.apk = apk;
        packageManager = context.getPackageManager();
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void setFinishListener(FinishListener listener) {
        this.finishListener = listener;
    }

    public void addDownloadListener(DownloadListener listener) {
        downloadListeners.add(listener);
    }

    private static String getDownloadName(String packageName, String appVersion) {
        return packageName + "_" + appVersion + ".apk";
    }

    @Override
    public void run() {
        final String name = getDownloadName(apk.getApkname(), apk.getApkversionname());
        final String filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                + File.separator + name;
        File apkFile = new File(filePath);
        PackageInfo pi = packageManager.getPackageArchiveInfo(filePath, 0);
        if (apkFile.exists() && (pi != null)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    for (DownloadListener listener : downloadListeners) {
                        listener.onDownloaded();
                    }
                }
            });
            installInternal(apk, filePath);
            return;
        }

        String sid = StringHelper.getVStr("APK", StringHelper.getN27(apk.getId()), Constant.APP_COOKIE_KEY, 6).trim();
        String url = String.format(Constant.COOLAPK_PREURL + Constant.METHOD_ON_DOWNLOAD_APK, Constant.API_KEY, sid);
        Log.d(TAG, url);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, name);
        request.setMimeType("application/vnd.android.package-archive");
        request.addRequestHeader("Cookie", "coolapk_did=" + Constant.COOLAPK_DID);
        long downloadId = downloadManager.enqueue(request);

        boolean downloading = true;
        while (downloading) {

            DownloadManager.Query q = new DownloadManager.Query();
            q.setFilterById(downloadId);

            Cursor cursor = downloadManager.query(q);
            cursor.moveToFirst();
            int bytes_downloaded = cursor.getInt(cursor
                    .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

            final int dl_progress = (int) ((bytes_downloaded * 100L) / bytes_total);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    for (DownloadListener listener : downloadListeners) {
                        listener.onDownloading(dl_progress);
                    }
                }
            });

            if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (DownloadListener listener : downloadListeners) {
                            listener.onDownloaded();
                        }
                    }
                });
                installInternal(apk, filePath);
                downloading = false;
            }

            if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_FAILED) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (DownloadListener listener : downloadListeners) {
                            listener.onFailure(DownloadListener.DOWNLOAD_FAIL, "Download Failed");
                        }
                        finishListener.onFinish(apk);
                    }
                });
                downloading = false;
            }

            cursor.close();
        }
    }

    private void installInternal(final Apk apk, final String filePath) {
        showNotification(apk, false);
        final List<String> result = Shell.SU.run("pm install -r " + filePath);
        if (result == null || result.size() == 0) {
            notificationManager.cancel(apk.getTitle().hashCode());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    installInternal2(filePath);
                    for (DownloadListener listener : downloadListeners) {
                        listener.onComplete();
                    }
                    finishListener.onFinish(apk);
                }
            });

        } else if (result.get(0).equals("Success")) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    showNotification(apk, true);
                    for (DownloadListener listener : downloadListeners) {
                        listener.onComplete();
                    }
                    finishListener.onFinish(apk);
                }
            });

        } else {
            notificationManager.cancel(apk.getTitle().hashCode());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    installInternal2(filePath);
                    for (DownloadListener listener : downloadListeners) {
                        listener.onComplete();
                    }
                    finishListener.onFinish(apk);
                }
            });
        }
    }

    private void installInternal2(String filePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void showNotification(Apk apk, boolean complete) {
        Intent intent = complete ? context.getPackageManager().getLaunchIntentForPackage(apk.getApkname()) : new Intent(context, NavActivity.class);
        if (intent == null) intent = new Intent();
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle(apk.getTitle() + (complete ? "安装成功" : "正在安装"));
        builder.setContentText(apk.getTitle() + (complete ? "安装成功" : "正在安装"));
        builder.setContentIntent(pi);
        builder.setSmallIcon(complete ? R.drawable.ic_stat_ok : R.drawable.ic_stat_install);
        Notification notification = builder.getNotification();
        notificationManager.notify(apk.getTitle().hashCode(), notification);
    }

    public interface DownloadListener {
        int DOWNLOAD_FAIL = -1;

        void onDownloading(int percent);

        void onFailure(int errCode, String... err);

        void onDownloaded();

        void onComplete();
    }

    interface FinishListener {
        void onFinish(Apk apk);
    }
}
