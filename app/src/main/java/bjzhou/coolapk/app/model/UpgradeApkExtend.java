package bjzhou.coolapk.app.model;

import android.graphics.drawable.Drawable;
import android.text.Spanned;

/**
 * Created by bjzhou on 14-8-13.
 */
public class UpgradeApkExtend {
    private String title;
    private Drawable logo;
    private Spanned info;
    private UpgradeApk apk;

    public UpgradeApk getApk() {
        return apk;
    }

    public void setApk(UpgradeApk apk) {
        this.apk = apk;
    }

    public Spanned getInfo() {
        return info;
    }

    public void setInfo(Spanned info) {
        this.info = info;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Drawable getLogo() {
        return logo;
    }

    public void setLogo(Drawable logo) {
        this.logo = logo;
    }
}
