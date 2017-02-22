package bjzhou.coolapk.app.net

import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import bjzhou.coolapk.app.App
import bjzhou.coolapk.app.model.Apk
import bjzhou.coolapk.app.ui.base.BaseActivity
import bjzhou.coolapk.app.util.Constant
import bjzhou.coolapk.app.util.Settings
import bjzhou.coolapk.app.util.Utils
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * author: zhoubinjia
 * date: 2017/2/22
 */
class ApkDownloader {

    private val downloadingApks = ArrayList<Apk>()
    private val downloadManager = App.context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    private val pendingApks = LinkedList<Apk>()

    private val downloadCompleteReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "action: ${intent.action}")
            when(intent.action) {
                DownloadManager.ACTION_DOWNLOAD_COMPLETE -> {
                    val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                    synchronized(lock) {
                        val removeApk: Apk? = downloadingApks.firstOrNull { it.downloadId == downloadId }
                        removeApk?.let {
                            downloadingApks.remove(it)
                        }
                        if (pendingApks.isNotEmpty()) {
                            download(pendingApks.poll())
                        }
                    }
                    if (Settings.instance.autoInstall) {
                        val q = DownloadManager.Query()
                        q.setFilterById(downloadId)

                        val cursor = downloadManager.query(q)
                        cursor.moveToFirst()
                        val localUriString = cursor.getString(cursor
                                .getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                        cursor.close()
                        val uri = Uri.parse(localUriString)
                        Utils.installApk(uri)
                    }
                }

            }
        }
    }

    private val packageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "action: ${intent.action}")
            when(intent.action) {
                Intent.ACTION_PACKAGE_ADDED, Intent.ACTION_PACKAGE_REPLACED -> {
                    if (Settings.instance.deleteFileAfterInstall) {
                        val pkgName = intent.data.schemeSpecificPart
                        val pkgInfo = App.context.packageManager.getPackageInfo(pkgName, 0)
                        val fileName = Apk.getFileName(pkgName, pkgInfo.versionName)
                        val file = File(Settings.instance.downloadDir, fileName)
                        if (file.exists() && file.delete()) {
                            Toast.makeText(App.context, "安装包已删除", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    init {
        val downloadFilter = IntentFilter()
        downloadFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        val packageFilter = IntentFilter()
        packageFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
        packageFilter.addAction(Intent.ACTION_PACKAGE_REPLACED)
        packageFilter.addDataScheme("package")
        App.context.registerReceiver(downloadCompleteReceiver, downloadFilter)
        App.context.registerReceiver(packageReceiver, packageFilter)
    }

    private var mPermissionGranted = true

    fun checkPermission(activity: BaseActivity) {
        activity.checkPermissions({ permission, succeed ->
            mPermissionGranted = succeed
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    fun download(apk: Apk?) {
        if (!Utils.networkConnected) {
            return
        }

        if (!mPermissionGranted) {
            return
        }

        if (apk == null) {
            return
        }

        if (downloadingApks.contains(apk)) {
            return
        }

        if (pendingApks.contains(apk)) {
            return
        }

        synchronized(lock) {
            if (downloadingApks.size >= Settings.instance.maxDownloadNumber) {
                pendingApks.offer(apk)
                return
            }
        }

        val apkFile = File(Settings.instance.downloadDir, apk.filename)
        if (apkFile.exists()) {
            apkFile.delete()
        }

        Log.d(TAG, apk.url)

        val request = DownloadManager.Request(Uri.parse(apk.url))
        request.setDestinationInExternalFilesDir(App.context, Environment.DIRECTORY_DOWNLOADS, apk.filename)
        request.setMimeType("application/vnd.android.package-archive")
        request.addRequestHeader("Cookie", "coolapk_did=" + Constant.COOLAPK_DID)
        val id = downloadManager.enqueue(request)
        apk.downloadId = id
        synchronized(lock) {
            downloadingApks.add(apk)
        }
    }

    fun getDownloadStatus(apk: Apk?): DownloadStatus {
        val status = DownloadStatus()
        if (apk == null) {
            return status
        }
        if (pendingApks.contains(apk)) {
            status.status = DownloadManager.STATUS_PENDING
            return status
        }

        val index = downloadingApks.indexOf(apk)
        if (index != -1) {
            apk.downloadId = downloadingApks[index].downloadId
        }
        if (apk.downloadId == -1L) {
            return status
        }
        val q = DownloadManager.Query()
        q.setFilterById(apk.downloadId)

        val cursor = downloadManager.query(q)
        cursor.moveToFirst()
        val bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
        val bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
        val column_status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
        cursor.close()

        val dl_progress = (bytes_downloaded * 100L / bytes_total).toInt()
        status.percent = dl_progress
        status.status = column_status
        return status
    }

    fun observeDownloadStatus(apk: Apk?): Observable<DownloadStatus> {
        return Observable.interval(100, TimeUnit.MILLISECONDS)
                .flatMap {
                    Observable.create { it: ObservableEmitter<DownloadStatus> ->
                        val status = getDownloadStatus(apk)
                        if (status.status == DownloadStatus.STATUS_NOT_STARTED) {
                            it.onComplete()
                            return@create
                        }
                        it.onNext(status)
                        if (status.status == DownloadManager.STATUS_SUCCESSFUL || status.status == DownloadManager.STATUS_FAILED) {
                            it.onComplete()
                        }
                    }
                }
    }



    private object Holder { val INSTANCE = ApkDownloader() }

    companion object {
        val instance: ApkDownloader by lazy { Holder.INSTANCE }
        val TAG: String = ApkDownloader::class.java.simpleName
        val lock: Any = Any()
    }
}