package bjzhou.coolapk.app.model;

/**
 * Created by bjzhou on 14-8-13.
 */
public class UpgradeApk {
    private long id; //11723
    private int apktype; //1
    private String apkname; //com.douban.frodo
    private String version; //1.0.0
    private String apkversionname; //1.0.0
    private int apkversioncode; //10
    private String apksize; //2.09 M
    private int sdkversion; //14
    private int maxsdkversion; //0
    private String changelog; //

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getApktype() {
        return apktype;
    }

    public void setApktype(int apktype) {
        this.apktype = apktype;
    }

    public String getApkname() {
        return apkname;
    }

    public void setApkname(String apkname) {
        this.apkname = apkname;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getApkversionname() {
        return apkversionname;
    }

    public void setApkversionname(String apkversionname) {
        this.apkversionname = apkversionname;
    }

    public int getApkversioncode() {
        return apkversioncode;
    }

    public void setApkversioncode(int apkversioncode) {
        this.apkversioncode = apkversioncode;
    }

    public String getApksize() {
        return apksize;
    }

    public void setApksize(String apksize) {
        this.apksize = apksize;
    }

    public int getSdkversion() {
        return sdkversion;
    }

    public void setSdkversion(int sdkversion) {
        this.sdkversion = sdkversion;
    }

    public int getMaxsdkversion() {
        return maxsdkversion;
    }

    public void setMaxsdkversion(int maxsdkversion) {
        this.maxsdkversion = maxsdkversion;
    }

    public String getChangelog() {
        return changelog;
    }

    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }

}
