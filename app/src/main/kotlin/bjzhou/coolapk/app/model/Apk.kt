package bjzhou.coolapk.app.model

import android.graphics.Bitmap
import android.text.Spanned
import bjzhou.coolapk.app.util.Constant
import bjzhou.coolapk.app.util.StringHelper

/**
 * Created by bjzhou on 14-7-29.
 */
class Apk {

    var id: Int = 0
    var catid: Int = 0
    var title: String = ""
    var logo: String = ""
    var version: String = ""
    var romversion: String = ""
    var ishot: Int = 0
    var apktype: Int = 0
    var apkname: String = ""
    var apkversionname: String = ""
    var apkversioncode: Int = 0
    var apksize: String = ""
    var sdkversion: Int = 0
    var maxsdkversion: Int = 0
    var star: Float = 0.toFloat()
    var score: Float = 0.toFloat()
    var downnum: Int = 0
    var commentnum: Int = 0
    var replynum: Int = 0
    var favnum: Int = 0
    var albumnum: Int = 0
    var votenum: Int = 0
    var description: String = ""
    var developername: String = ""
    var pubdate: Long = 0
    var status: Int = 0
    var lastupdate: Long = 0
    var apkTypeName: String = ""
    var apkTypeUrl: String = ""
    var apkUrl: String = ""
    var shorttitle: String = ""
    var updateFlag: String = ""
    var downCount: String = ""
    var adminscore: Int = 0
    var commentCount: Int = 0
    var info: Spanned? = null
    var logoBitmap: Bitmap? = null
    var changelog: String = ""

    var downloadId = -1L

    val filename: String
        get() = getFileName(apkname, apkversionname)

    val url: String
        get() {
            val sid = StringHelper.getVStr("APK", StringHelper.getN27(id), Constant.APP_COOKIE_KEY, 6).trim { it <= ' ' }
            return String.format(Constant.COOLAPK_PREURL + Constant.METHOD_ON_DOWNLOAD_APK, Constant.API_KEY, sid)
        }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + apkname.hashCode()
        result = 31 * result + apkversioncode
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Apk

        if (id != other.id) return false
        if (apkname != other.apkname) return false
        if (apkversioncode != other.apkversioncode) return false

        return true
    }


    companion object {
        fun getFileName(packageName: String, versionName: String): String {
            return packageName + "_" + versionName + ".apk"
        }
    }
}
