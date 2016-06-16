package bjzhou.coolapk.app.http;

import android.Manifest;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bjzhou.coolapk.app.R;
import bjzhou.coolapk.app.ui.base.BaseActivity;
import bjzhou.coolapk.app.util.Constant;
import bjzhou.coolapk.app.util.StringHelper;
import eu.chainfire.libsuperuser.Shell;

/**
 * Created by bjzhou on 14-8-18.
 */
public class ApkDownloader {
    private static final String TAG = "Downloader";
    private static ApkDownloader instance;
    private final Context mContext;
    private final DownloadManager downloadManager;

    private Map<Integer, DownloadListener> downloadingIds = new HashMap<>();
    private boolean mPermissionGranted = false;

    private ApkDownloader(Context context) {
        mContext = context;
        downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public static ApkDownloader getInstance(Context context) {
        if (instance == null) {
            instance = new ApkDownloader(context);
        }
        return instance;
    }

    public void checkPermission(BaseActivity activity) {
        activity.checkPermissions(new BaseActivity.PermissionListener() {
            @Override
            public void onResult(String permission, boolean succeed) {
                mPermissionGranted = succeed;
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public static boolean checkNetworkState(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public void download(final int id, String packageName, final String appName, String appVersion, final DownloadListener _listener) {
        if (!checkNetworkState(mContext)) {
            return;
        }

        if (!mPermissionGranted) {
            return;
        }

        if (downloadingIds.containsKey(id)) {
            return;
        }

        final String name = getDownloadName(packageName, appVersion);
        final String filePath = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                + File.separator + name;

        final Handler handler = new Handler();
        new Thread(new Runnable() {

            @Override
            public void run() {

                downloadingIds.put(id, _listener);

                PackageInfo pi = mContext.getPackageManager().getPackageArchiveInfo(filePath, 0);
                if (new File(filePath).exists() && (pi != null)) {
                    installInternal(id, filePath, appName, handler);
                    return;
                }

                String sid = StringHelper.getVStr("APK", StringHelper.getN27(id), Constant.APP_COOKIE_KEY, 6).trim();
                String url = String.format(Constant.COOLAPK_PREURL + Constant.METHOD_ON_DOWNLOAD_APK, Constant.API_KEY, sid);
                //Log.d(TAG, url);

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS, name);
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

                    final int dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            downloadingIds.get(id).onDownloading(dl_progress);
                        }
                    });

                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                downloadingIds.get(id).onDownloaded();
                            }
                        });
                        downloading = false;
                        installInternal(id, filePath, appName, handler);
                    }

                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_FAILED) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                downloadingIds.get(id).onFailure(DownloadListener.DOWNLOAD_FAIL, "Download Failed");
                                downloadingIds.remove(id);
                            }
                        });
                        downloading = false;
                    }

                    cursor.close();
                }

            }
        }).start();
    }

    public static String getDownloadName(String packageName, String appVersion) {
        return packageName + "_" + appVersion + ".apk";
    }

    private void installInternal(final int id, final String filePath, final String appName, Handler handler) {
        if (downloadingIds.get(id) == null) return;
        final List<String> result = Shell.SU.run("pm install -r " + filePath);
        if (result == null || result.size() == 0) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    installInternal2(filePath);
                    downloadingIds.get(id).onFailure(DownloadListener.INSTALL_FAIL, "root problem", filePath);
                    downloadingIds.remove(id);
                }
            });

        } else if (result.get(0).equals("Success")) {
            Log.d(TAG, "Install Success");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    showNotification(appName);
                    downloadingIds.get(id).onComplete();
                    downloadingIds.remove(id);
                }
            });

        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    installInternal2(filePath);
                    downloadingIds.get(id).onFailure(DownloadListener.INSTALL_FAIL, result.get(0), filePath);
                    downloadingIds.remove(id);
                }
            });
        }
    }

    private void installInternal2(String filePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public boolean isDownloading(int id) {
        return downloadingIds.containsKey(id);
    }

    public void setListener(int id, DownloadListener listener) {
        downloadingIds.put(id, listener);
    }

    public void showNotification(String title) {
        NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pi = PendingIntent.getActivity(mContext, 0, new Intent(), 0);
        Notification.Builder builder = new Notification.Builder(mContext);
        builder.setContentTitle(title + "安装成功");
        builder.setContentText(title + "安装成功");
        builder.setContentIntent(pi);
        builder.setSmallIcon(R.drawable.ic_stat_ok);
        Notification notification = builder.getNotification();
        nm.notify(title.hashCode(), notification);
    }

    public interface DownloadListener {
        public static final int DOWNLOAD_FAIL = -1;
        public static final int INSTALL_FAIL = -2;

        public void onDownloading(int percent);

        public void onFailure(int errCode, String... err);

        public void onDownloaded();

        public void onComplete();
    }
}
