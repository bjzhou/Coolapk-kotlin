package bjzhou.coolapk.app.model

/**
 * Created by bjzhou on 14-7-30.
 */
class ApkField {

    var meta: Apk? = null
    var field: Field? = null
    var userAction: UserAction? = null

    inner class Field {
        var id: Int = 0
        var votenum1: Int = 0
        var votenum2: Int = 0
        var votenum3: Int = 0
        var votenum4: Int = 0
        var votenum5: Int = 0
        var softtype: String? = null
        var language: String? = null
        var developername: String? = null
        var developermail: String? = null
        var developerurl: String? = null
        var softfile: String? = null
        var md5sum: String? = null
        var extendname: String? = null
        var extendfile: String? = null
        var officialfile: String? = null
        var videourl: String? = null
        var goodwords: String? = null
        var badwords: String? = null
        var remark: String? = null
        var screenshots: String? = null
        var permissions: String? = null
        var introduce: String? = null
        var changelog: String? = null
        var changehistory: String? = null
        var cpsurl: String? = null
        var cpslogurl: String? = null
        var romversion: String? = null
        var screenshot: String? = null
    }

    inner class UserAction {
        var fav: Int = 0
        var rate: Int = 0
    }
}
