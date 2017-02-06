package bjzhou.coolapk.app.net

import android.app.DownloadManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import bjzhou.coolapk.app.R
import bjzhou.coolapk.app.model.Apk
import bjzhou.coolapk.app.ui.activities.NavActivity
import bjzhou.coolapk.app.util.Constant
import bjzhou.coolapk.app.util.StringHelper
import eu.chainfire.libsuperuser.Shell
import java.io.File
import java.util.*

/**
 * author: zhoubinjia
 * date: 2017/1/23
 */
class DownloadMonitor(context: Context, private val apk: Apk) : Thread() {
    private val packageManager: PackageManager = context.packageManager
    private val downloadManager: DownloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    private val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    var isStarted: Boolean = false
        private set
    private val context: Context
    private val handler = Handler(Looper.getMainLooper())
    private val downloadListeners = ArrayList<DownloadListener>()
    private var finishListener: ((Apk) -> Any)? = null
    private var downloadId: Long = -1

    init {
        this.context = context.applicationContext
        isStarted = false
    }

    fun setFinishListener(listener: (Apk) -> Any) {
        this.finishListener = listener
    }

    fun setDownloadListener(listener: DownloadListener?) {
        if (listener != null) {
            downloadListeners.clear()
            downloadListeners.add(listener)
        }
    }

    override fun run() {
        isStarted = true
        val name = getDownloadName(apk.apkname, apk.apkversionname)
        val filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).absolutePath + File.separator + name
        val apkFile = File(filePath)
        val pi = packageManager.getPackageArchiveInfo(filePath, 0)
        if (apkFile.exists() && pi != null) {
            handler.post {
                for (listener in downloadListeners) {
                    listener.onDownloaded()
                }
            }
            installInternal(apk, filePath)
            return
        }

        val sid = StringHelper.getVStr("APK", StringHelper.getN27(apk.id), Constant.APP_COOKIE_KEY, 6).trim { it <= ' ' }
        val url = String.format(Constant.COOLAPK_PREURL + Constant.METHOD_ON_DOWNLOAD_APK, Constant.API_KEY, sid)
        Log.d(TAG, url)

        val request = DownloadManager.Request(Uri.parse(url))
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, name)
        request.setMimeType("application/vnd.android.package-archive")
        request.addRequestHeader("Cookie", "coolapk_did=" + Constant.COOLAPK_DID)
        downloadId = downloadManager.enqueue(request)

        var downloading = true
        var q: DownloadManager.Query
        var cursor: Cursor
        var dl_progress: Int
        while (downloading) {
            q = DownloadManager.Query()
            q.setFilterById(downloadId)

            cursor = downloadManager.query(q)
            cursor.moveToFirst()
            val bytes_downloaded = cursor.getInt(cursor
                    .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
            val bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

            dl_progress = (bytes_downloaded * 100L / bytes_total).toInt()
            val finalDl_progress = dl_progress
            handler.post {
                for (listener in downloadListeners) {
                    listener.onDownloading(finalDl_progress)
                }
            }

            if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                handler.post {
                    for (listener in downloadListeners) {
                        listener.onDownloaded()
                    }
                }
                installInternal(apk, filePath)
                downloading = false
            }

            if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_FAILED) {
                handler.post {
                    for (listener in downloadListeners) {
                        listener.onFailure(DownloadListener.DOWNLOAD_FAIL, "Download Failed")
                    }
                    finishListener?.invoke(apk)
                }
                downloading = false
            }

            cursor.close()

            SystemClock.sleep(50)
        }
    }

    private fun installInternal(apk: Apk, filePath: String) {
        showNotification(apk, false)
        val result = Shell.SU.run("pm install -r " + filePath)
        if (result == null || result.size == 0) {
            notificationManager.cancel(apk.title.hashCode())
            handler.post {
                installInternal2(filePath)
                for (listener in downloadListeners) {
                    listener.onComplete()
                }
                finishListener?.invoke(apk)
            }

        } else if (result[0] == "Success") {
            handler.post {
                showNotification(apk, true)
                for (listener in downloadListeners) {
                    listener.onComplete()
                }
                finishListener?.invoke(apk)
            }

        } else {
            notificationManager.cancel(apk.title.hashCode())
            handler.post {
                installInternal2(filePath)
                for (listener in downloadListeners) {
                    listener.onComplete()
                }
                finishListener?.invoke(apk)
            }
        }
    }

    private fun installInternal2(filePath: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.fromFile(File(filePath)), "application/vnd.android.package-archive")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun showNotification(apk: Apk, complete: Boolean) {
        var intent: Intent? = if (complete) context.packageManager.getLaunchIntentForPackage(apk.apkname) else Intent(context, NavActivity::class.java)
        if (intent == null) intent = Intent()
        val pi = PendingIntent.getActivity(context, 0, intent, 0)
        val builder = Notification.Builder(context)
        builder.setContentTitle(apk.title + if (complete) "安装成功" else "正在安装")
        builder.setContentText(apk.title + if (complete) "安装成功" else "正在安装")
        builder.setContentIntent(pi)
        builder.setSmallIcon(if (complete) R.drawable.ic_stat_ok else R.drawable.ic_stat_install)
        val notification = builder.notification
        notificationManager.notify(apk.title.hashCode(), notification)
    }

    fun stopDownload() {
        if (downloadId != -1.toLong()) {
            downloadManager.remove(downloadId)
        }
    }

    interface DownloadListener {

        fun onDownloading(percent: Int)

        fun onFailure(errCode: Int, vararg err: String)

        fun onDownloaded()

        fun onComplete()

        companion object {
            val DOWNLOAD_FAIL = -1
        }
    }

    companion object {
        private val TAG = DownloadMonitor::class.java.simpleName

        private fun getDownloadName(packageName: String, appVersion: String): String {
            return packageName + "_" + appVersion + ".apk"
        }
    }
}
