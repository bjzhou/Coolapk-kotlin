package bjzhou.coolapk.app.model;

/**
 * Created by bjzhou on 14-7-30.
 */
public class ApkField {

    private Apk meta;
    private Field field;
    private UserAction userAction;

    public Apk getMeta() {
        return meta;
    }

    public void setMeta(Apk meta) {
        this.meta = meta;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public UserAction getUserAction() {
        return userAction;
    }

    public void setUserAction(UserAction userAction) {
        this.userAction = userAction;
    }

    public class Field {
        private int id;
        private int votenum1;
        private int votenum2;
        private int votenum3;
        private int votenum4;
        private int votenum5;
        private String softtype;
        private String language;
        private String developername;
        private String developermail;
        private String developerurl;
        private String softfile;
        private String md5sum;
        private String extendname;
        private String extendfile;
        private String officialfile;
        private String videourl;
        private String goodwords;
        private String badwords;
        private String remark;
        private String screenshots;
        private String permissions;
        private String introduce;
        private String changelog;
        private String changehistory;
        private String cpsurl;
        private String cpslogurl;
        private String romversion;
        private String screenshot;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getVotenum1() {
            return votenum1;
        }

        public void setVotenum1(int votenum1) {
            this.votenum1 = votenum1;
        }

        public int getVotenum2() {
            return votenum2;
        }

        public void setVotenum2(int votenum2) {
            this.votenum2 = votenum2;
        }

        public int getVotenum3() {
            return votenum3;
        }

        public void setVotenum3(int votenum3) {
            this.votenum3 = votenum3;
        }

        public int getVotenum4() {
            return votenum4;
        }

        public void setVotenum4(int votenum4) {
            this.votenum4 = votenum4;
        }

        public int getVotenum5() {
            return votenum5;
        }

        public void setVotenum5(int votenum5) {
            this.votenum5 = votenum5;
        }

        public String getSofttype() {
            return softtype;
        }

        public void setSofttype(String softtype) {
            this.softtype = softtype;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getDevelopername() {
            return developername;
        }

        public void setDevelopername(String developername) {
            this.developername = developername;
        }

        public String getDevelopermail() {
            return developermail;
        }

        public void setDevelopermail(String developermail) {
            this.developermail = developermail;
        }

        public String getDeveloperurl() {
            return developerurl;
        }

        public void setDeveloperurl(String developerurl) {
            this.developerurl = developerurl;
        }

        public String getSoftfile() {
            return softfile;
        }

        public void setSoftfile(String softfile) {
            this.softfile = softfile;
        }

        public String getMd5sum() {
            return md5sum;
        }

        public void setMd5sum(String md5sum) {
            this.md5sum = md5sum;
        }

        public String getExtendname() {
            return extendname;
        }

        public void setExtendname(String extendname) {
            this.extendname = extendname;
        }

        public String getExtendfile() {
            return extendfile;
        }

        public void setExtendfile(String extendfile) {
            this.extendfile = extendfile;
        }

        public String getOfficialfile() {
            return officialfile;
        }

        public void setOfficialfile(String officialfile) {
            this.officialfile = officialfile;
        }

        public String getVideourl() {
            return videourl;
        }

        public void setVideourl(String videourl) {
            this.videourl = videourl;
        }

        public String getGoodwords() {
            return goodwords;
        }

        public void setGoodwords(String goodwords) {
            this.goodwords = goodwords;
        }

        public String getBadwords() {
            return badwords;
        }

        public void setBadwords(String badwords) {
            this.badwords = badwords;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getScreenshots() {
            return screenshots;
        }

        public void setScreenshots(String screenshots) {
            this.screenshots = screenshots;
        }

        public String getPermissions() {
            return permissions;
        }

        public void setPermissions(String permissions) {
            this.permissions = permissions;
        }

        public String getIntroduce() {
            return introduce;
        }

        public void setIntroduce(String introduce) {
            this.introduce = introduce;
        }

        public String getChangelog() {
            return changelog;
        }

        public void setChangelog(String changelog) {
            this.changelog = changelog;
        }

        public String getChangehistory() {
            return changehistory;
        }

        public void setChangehistory(String changehistory) {
            this.changehistory = changehistory;
        }

        public String getCpsurl() {
            return cpsurl;
        }

        public void setCpsurl(String cpsurl) {
            this.cpsurl = cpsurl;
        }

        public String getCpslogurl() {
            return cpslogurl;
        }

        public void setCpslogurl(String cpslogurl) {
            this.cpslogurl = cpslogurl;
        }

        public String getRomversion() {
            return romversion;
        }

        public void setRomversion(String romversion) {
            this.romversion = romversion;
        }

        public String getScreenshot() {
            return screenshot;
        }

        public void setScreenshot(String screenshot) {
            this.screenshot = screenshot;
        }
    }

    public class UserAction {
        private int fav;
        private int rate;

        public int getFav() {
            return fav;
        }

        public void setFav(int fav) {
            this.fav = fav;
        }

        public int getRate() {
            return rate;
        }

        public void setRate(int rate) {
            this.rate = rate;
        }
    }
}
