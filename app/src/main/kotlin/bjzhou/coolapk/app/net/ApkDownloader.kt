package bjzhou.coolapk.app.net

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.util.SparseArray

import bjzhou.coolapk.app.model.Apk
import bjzhou.coolapk.app.ui.base.BaseActivity

/**
 * Created by bjzhou on 14-8-18.
 */
class ApkDownloader private constructor() {

    private val downloadingIds = SparseArray<DownloadMonitor>()
    private var mPermissionGranted = false


    fun checkPermission(activity: BaseActivity) {
        activity.checkPermissions({ permission, succeed ->
            mPermissionGranted = succeed
            mPermissionGranted
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    fun downloadAndInstall(context: Context, apk: Apk?, _listener: DownloadMonitor.DownloadListener?) {
        if (!checkNetworkState(context)) {
            return
        }

        if (!mPermissionGranted) {
            return
        }

        if (apk == null) {
            return
        }

        if (downloadingIds.indexOfKey(apk.id) != -1) {
            if (_listener != null) {
                downloadingIds.get(apk.id).setDownloadListener(_listener)
            }
            return
        }

        val appContext = context.applicationContext
        val monitor = DownloadMonitor(appContext, apk)
        monitor.setFinishListener { apk ->
            downloadingIds.remove(apk.id)
            checkDownloadThread()
        }
        if (_listener != null) {
            monitor.setDownloadListener(_listener)
        }
        downloadingIds.put(apk.id, monitor)
        if (downloadingIds.size() <= 3) {
            monitor.start()
        }
    }

    @Synchronized private fun checkDownloadThread() {
        for (i in 0..downloadingIds.size() - 1) {
            val m = downloadingIds.valueAt(i)
            if (!m.isStarted) {
                m.start()
                break
            }
        }
    }

    fun isDownloading(id: Int): Boolean {
        return downloadingIds.indexOfKey(id) != -1 && downloadingIds.get(id).isStarted
    }

    fun setListener(id: Int, listener: DownloadMonitor.DownloadListener?) {
        downloadingIds.get(id).setDownloadListener(listener)
    }

    fun stopDownload() {
        for (i in 0..downloadingIds.size() - 1) {
            downloadingIds.valueAt(i).stopDownload()
        }
    }

    companion object {
        private val TAG = "Downloader"
        var instance: ApkDownloader = ApkDownloader()
            private set

        fun checkNetworkState(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo != null && cm.activeNetworkInfo.isConnectedOrConnecting
        }
    }
}
