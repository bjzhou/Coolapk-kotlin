package bjzhou.coolapk.app.viewmodel

import android.app.DownloadManager
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.widget.Toast
import bjzhou.coolapk.app.App
import bjzhou.coolapk.app.model.ApkField
import bjzhou.coolapk.app.net.ApiManager
import bjzhou.coolapk.app.net.ApkDownloader
import bjzhou.coolapk.app.net.DownloadStatus
import bjzhou.coolapk.app.util.Settings
import bjzhou.coolapk.app.util.Utils
import java.io.File

/**
 * Created by zhoubinjia on 2017/5/23.
 */
class AppViewModel : ViewModel() {

    companion object {
        val CLICK_DOWNLOAD = 0
        val CLICK_INSTALL = 1
        val CLICK_OPEN = 2
        val CLICK_DO_NOTHING = 3
    }

    var mId = 0
    var mApkField = MutableLiveData<ApkField>()
    var mClickStatus = MutableLiveData<Int>()

    fun obtainApkField() {
        ApiManager.instance.mService.obtainApkField(mId).enqueue { apkField ->
            mApkField.value = apkField
            val installedVersion = getInstalledVersion(apkField.meta?.apkname)
            val version = apkField.meta?.apkversioncode ?: 0
            if (installedVersion == -1) {
                mClickStatus.value = CLICK_DOWNLOAD

                val file = File(Settings.instance.downloadDir, mApkField.value?.meta?.filename)
                val pkgInfo = App.context.packageManager.getPackageArchiveInfo(file.absolutePath, 0)
                if (pkgInfo != null && version <= pkgInfo.versionCode) {
                    mClickStatus.value = CLICK_INSTALL
                }
            } else {
                if (version > installedVersion) {
                    mClickStatus.value = CLICK_DOWNLOAD
                } else {
                    mClickStatus.value = CLICK_OPEN
                }
            }
            if (mClickStatus.value == CLICK_DOWNLOAD) {
                val status = ApkDownloader.instance.getDownloadStatus(apkField.meta)
                if (status.status == DownloadStatus.STATUS_NOT_STARTED) {
                    mClickStatus.value = CLICK_DOWNLOAD
                } else if (status.status == DownloadManager.STATUS_SUCCESSFUL) {
                    mClickStatus.value = CLICK_INSTALL
                } else if (status.status != DownloadManager.STATUS_FAILED) {
                    mClickStatus.value = CLICK_DO_NOTHING
                }
            }
        }
    }

    private fun getInstalledVersion(packageName: String?): Int {
        val pm = App.context.packageManager
        try {
            val pi = pm.getPackageInfo(packageName, 0)
            return pi.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            return -1
        }
    }

    fun download(activity: FragmentActivity) {
        if (mClickStatus.value == CLICK_OPEN) {
            val intent = activity.packageManager.getLaunchIntentForPackage(mApkField.value?.meta?.apkname)
            activity.startActivity(intent)
        }
        if (mClickStatus.value == CLICK_INSTALL) {
            val file = File(Settings.instance.downloadDir, mApkField.value?.meta?.filename)
            val pkgInfo = App.context.packageManager.getPackageArchiveInfo(file.absolutePath, 0)
            Log.d("App", file.path + " pkgInfo: " + pkgInfo)
            if (file.exists() && pkgInfo != null) {
                Utils.installApk(activity, Uri.fromFile(file))
            } else {
                downloadApk()
            }
        }
        if (mClickStatus.value == CLICK_DOWNLOAD) {
            downloadApk()
        }
    }

    private fun downloadApk() {
        mApkField.value?.meta?.let {
            val apk = it
            ApkDownloader.instance.download(apk)
            ApkDownloader.instance.observeDownloadStatus(apk) {
                        when (it.status) {
                            DownloadManager.STATUS_FAILED -> {
                                Toast.makeText(App.context, "下载失败,点击重试", Toast.LENGTH_SHORT).show()
                                mClickStatus.value = CLICK_DOWNLOAD
                            }
                            DownloadManager.STATUS_SUCCESSFUL -> {
                                mClickStatus.value = CLICK_INSTALL
                            }
                            else -> {
                                mClickStatus.value = CLICK_DO_NOTHING
                            }
                        }
                    }
        }
    }
}