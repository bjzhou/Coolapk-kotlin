package bjzhou.coolapk.app.util

import android.os.Environment
import bjzhou.coolapk.app.App
import java.io.File

/**
 * author: zhoubinjia
 * date: 2017/2/22
 */
class Settings {

    private val defaultDownloadDir = App.context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)

    var downloadDir: File = defaultDownloadDir
    var maxDownloadNumber = 3
    var autoInstall = true
    var deleteFileAfterInstall = true

    private object Holder {
        val INSTANCE = Settings()
    }

    companion object {
        val instance: Settings by lazy { Holder.INSTANCE }
    }
}