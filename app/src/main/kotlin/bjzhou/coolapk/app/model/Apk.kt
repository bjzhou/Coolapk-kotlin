package bjzhou.coolapk.app.model

import android.graphics.Bitmap
import android.text.Spanned

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
}
