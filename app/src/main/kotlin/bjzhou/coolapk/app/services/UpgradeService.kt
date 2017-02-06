package bjzhou.coolapk.app.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import bjzhou.coolapk.app.net.ApiManager
import bjzhou.coolapk.app.net.ApkDownloader

/**
 * Created by bjzhou on 14-8-15.
 */
class UpgradeService : Service() {

    override fun onCreate() {
        super.onCreate()

        ApiManager.instance.obtainUpgradeVersions(this).subscribe { upgradeApkExtends ->
            for (upgradeApkExtend in upgradeApkExtends) {
                upgradeApkExtend.apk.title = upgradeApkExtend.title ?: ""
                ApkDownloader.instance.downloadAndInstall(this@UpgradeService, upgradeApkExtend.apk, null)
            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val action = intent.action
        if (ACTION_UPGRADE_SILENT == action) {
            if (ApiManager.isWifiConnected(this)) {
                ApiManager.instance.obtainUpgradeVersions(this).subscribe { upgradeApkExtends ->
                    for (upgradeApkExtend in upgradeApkExtends) {
                        upgradeApkExtend.apk.title = upgradeApkExtend.title ?: ""
                        ApkDownloader.instance.downloadAndInstall(this@UpgradeService, upgradeApkExtend.apk, null)
                    }
                }
            }
        }
        return Service.START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        ApkDownloader.instance.stopDownload()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {
        private val TAG = "UpgradeService"
        private val SCAN_INTERVAL = (24 * 60 * 60 * 1000).toLong()

        private val ACTION_UPGRADE_SILENT = "bjzhou.coolapk.app.action.upgrade_silent"
    }
}
