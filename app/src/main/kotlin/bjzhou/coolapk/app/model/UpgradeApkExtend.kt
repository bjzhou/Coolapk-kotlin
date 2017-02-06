package bjzhou.coolapk.app.model

import android.graphics.drawable.Drawable
import android.text.Spanned

/**
 * Created by bjzhou on 14-8-13.
 */
class UpgradeApkExtend {
    var title: String? = null
    var logo: Drawable? = null
    var info: Spanned? = null
    var apk: Apk = Apk()

    override fun toString(): String {
        return "UpgradeApkExtend{" +
                "info=" + info +
                ", title='" + title + '\'' +
                ", logo=" + logo +
                '}'
    }
}
