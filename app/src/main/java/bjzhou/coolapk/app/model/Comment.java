package bjzhou.coolapk.app.model;

import java.util.List;

/**
 * Created by bjzhou on 14-8-7.
 */
public class Comment {

    private long id; //694703
    private long uid; //311581
    private int forwardid; //0
    private String username; //\u6cdb\u6cdb
    private int type; //9
    private String ttype; //apk
    private int tid; //3504
    private String ttitle; //PPTV
    private String tpic; //http; //\/\/image.coolapk.com\/apk_logo\/2013\/0126\/com.pplive.androidphone.png
    private String turl; //\/apk\/com.pplive.androidphone
    private String tinfo; //4.0.0
    private int messagelength; //18
    private String message; //4.0.0VIP\u7248\uff0c\u90a3\u4f4d\u9ad8\u624b\u5c3d\u663e\u795e\u901a
    //pic; //
    private int issummary; //0
    private int istag; //0
    //tags; //
    //videothumb; //
    //videourl; //
    private int fromid; //3504
    private String fromname; //\u9177\u5e02\u573a
    private String ip; //113.57.190.18
    private int likenum; //0
    private int replynum; //3
    private int forwardnum; //0
    private int reportnum; //0
    private int recommend; //0
    private int status; //1
    private long dateline; //1406832065
    private long lastupdate; //1406868014
    private String feedUrl; //\/feed\/694703
    //linkTarget; //
    private String title; //\u6cdb\u6cdb \u6765\u81ea\u5e94\u7528 PPTV \u7684\u8bc4\u8bba
    private String info; //\u6765\u81ea\u5e94\u7528 <a href=\u0022\/apk\/com.pplive.androidphone\u0022>PPTV<\/a> \u7684\u8bc4\u8bba
    //pics; //[]
    private int replyRowsCount; //3
    private int replyRowsMore; //0
    private int queryTid; //3504
    private int queryCid; //0
    private int queryPage; //1
    private String fromtype; //apk
    //fromtitle; //
    //frompic; //
    //fromurl; //
    //userurl; //
    private String useravatar; //http; //\/\/avatar.coolapk.com\/avatar.php?uid=311581&type=virtual&size=small
    //from; //
    private List<Reply> subrows;

    public List<Reply> getSubrows() {
        return subrows;
    }

    public void setSubrows(List<Reply> subrows) {
        this.subrows = subrows;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public int getForwardid() {
        return forwardid;
    }

    public void setForwardid(int forwardid) {
        this.forwardid = forwardid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTtype() {
        return ttype;
    }

    public void setTtype(String ttype) {
        this.ttype = ttype;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public String getTtitle() {
        return ttitle;
    }

    public void setTtitle(String ttitle) {
        this.ttitle = ttitle;
    }

    public String getTpic() {
        return tpic;
    }

    public void setTpic(String tpic) {
        this.tpic = tpic;
    }

    public String getTurl() {
        return turl;
    }

    public void setTurl(String turl) {
        this.turl = turl;
    }

    public String getTinfo() {
        return tinfo;
    }

    public void setTinfo(String tinfo) {
        this.tinfo = tinfo;
    }

    public int getMessagelength() {
        return messagelength;
    }

    public void setMessagelength(int messagelength) {
        this.messagelength = messagelength;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getIssummary() {
        return issummary;
    }

    public void setIssummary(int issummary) {
        this.issummary = issummary;
    }

    public int getIstag() {
        return istag;
    }

    public void setIstag(int istag) {
        this.istag = istag;
    }

    public int getFromid() {
        return fromid;
    }

    public void setFromid(int fromid) {
        this.fromid = fromid;
    }

    public String getFromname() {
        return fromname;
    }

    public void setFromname(String fromname) {
        this.fromname = fromname;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getLikenum() {
        return likenum;
    }

    public void setLikenum(int likenum) {
        this.likenum = likenum;
    }

    public int getReplynum() {
        return replynum;
    }

    public void setReplynum(int replynum) {
        this.replynum = replynum;
    }

    public int getForwardnum() {
        return forwardnum;
    }

    public void setForwardnum(int forwardnum) {
        this.forwardnum = forwardnum;
    }

    public int getReportnum() {
        return reportnum;
    }

    public void setReportnum(int reportnum) {
        this.reportnum = reportnum;
    }

    public int getRecommend() {
        return recommend;
    }

    public void setRecommend(int recommend) {
        this.recommend = recommend;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getDateline() {
        return dateline;
    }

    public void setDateline(long dateline) {
        this.dateline = dateline;
    }

    public long getLastupdate() {
        return lastupdate;
    }

    public void setLastupdate(long lastupdate) {
        this.lastupdate = lastupdate;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getReplyRowsCount() {
        return replyRowsCount;
    }

    public void setReplyRowsCount(int replyRowsCount) {
        this.replyRowsCount = replyRowsCount;
    }

    public int getReplyRowsMore() {
        return replyRowsMore;
    }

    public void setReplyRowsMore(int replyRowsMore) {
        this.replyRowsMore = replyRowsMore;
    }

    public int getQueryTid() {
        return queryTid;
    }

    public void setQueryTid(int queryTid) {
        this.queryTid = queryTid;
    }

    public int getQueryCid() {
        return queryCid;
    }

    public void setQueryCid(int queryCid) {
        this.queryCid = queryCid;
    }

    public int getQueryPage() {
        return queryPage;
    }

    public void setQueryPage(int queryPage) {
        this.queryPage = queryPage;
    }

    public String getFromtype() {
        return fromtype;
    }

    public void setFromtype(String fromtype) {
        this.fromtype = fromtype;
    }

    public String getUseravatar() {
        return useravatar;
    }

    public void setUseravatar(String useravatar) {
        this.useravatar = useravatar;
    }

}
