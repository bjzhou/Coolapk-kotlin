package bjzhou.coolapk.app.util;

/**
 * Created by bjzhou on 14-7-29.
 */
public class Constant {

    public static final String API_KEY = "5b90704e1db879af6f5ee08ec1e8f2a5";
    public static final String COOLAPK_DID = "d41d8cd98f00b204e9800998ecf8427e";

    public static final String COOLAPK_PREURL = "http://api.coolapk.com/market/v2/";
    public static final String METHOD_GET_HOMEPAGE_APKLIST = "&method=getHomepageApkList&p=%d&slm=1";
    public static final String METHOD_GET_APK_FIELD = "&method=getApkField&id=%d&includeMeta=0&slm=1";
    public static final String METHOD_GET_SEARCH_APKLIST = "&method=getSearchApkList&q=%s&p=%d&slm=1";
    public static final String METHOD_GET_COMMENT_LIST = "&method=getCommentList&tclass=apk&tid=%d&subrows=1&sublimit=10&p=%d&slm=1";
    public static final String METHOD_GET_UPGRADE_VERSIONS = "&method=getUpgradeVersions";
    public static final String METHOD_ON_DOWNLOAD_APK = "&method=onDownloadApk&v=%s&sn=null";


    public static final int MSG_OBTAIN_COMPLETE = 1;
    public static final int MSG_OBTAIN_MORE_COMPLETE = 2;
    public static final int MSG_OBTAIN_FAILED = -1;

    public static final String DOWNLOAD_DIR = "Download";

    public static final String APP_COOKIE_KEY = "K^G@t&Id";
}
