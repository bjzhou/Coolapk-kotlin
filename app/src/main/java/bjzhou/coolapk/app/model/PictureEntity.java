package bjzhou.coolapk.app.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PictureEntity {

    private long id;
    private long uid;
    private int forwardid;
    private int sourceId;
    private String username;
    private int type;
    private int ttype;
    private int tid;
    private String tkey;
    private String ttitle;
    private String tpic;
    private String turl;
    private String tinfo;
    private int messagelength;
    private String message;
    private String pic;
    private List<String> relateddata = null;
    private int issummary;
    private int istag;
    private String tags;
    private String label;
    private String videothumb;
    private String videourl;
    private int fromid;
    private String fromname;
    private String ip;
    private int likenum;
    private int replynum;
    private int forwardnum;
    private int reportnum;
    private int relatednum;
    private int favnum;
    private long rankScore;
    private int recommend;
    private int status;
    private long dateline;
    private long lastupdate;
    private String deviceTitle;
    private String indexName;
    private String fetchType;
    private String avatarFetchType;
    private String userAvatar;
    private String entityType;
    private String url;
    private String feedType;
    private String feedTypeName;
    private String infoHtml;
    private String info;
    private String turlTarget;
    private String title;
    private List<String> picArr = new ArrayList<>();
    private String sourceFeed;
    private Map<String, String> additionalProperties = new HashMap<>();

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

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
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

    public int getTtype() {
        return ttype;
    }

    public void setTtype(int ttype) {
        this.ttype = ttype;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public String getTkey() {
        return tkey;
    }

    public void setTkey(String tkey) {
        this.tkey = tkey;
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

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public List<String> getRelateddata() {
        return relateddata;
    }

    public void setRelateddata(List<String> relateddata) {
        this.relateddata = relateddata;
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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getVideothumb() {
        return videothumb;
    }

    public void setVideothumb(String videothumb) {
        this.videothumb = videothumb;
    }

    public String getVideourl() {
        return videourl;
    }

    public void setVideourl(String videourl) {
        this.videourl = videourl;
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

    public int getRelatednum() {
        return relatednum;
    }

    public void setRelatednum(int relatednum) {
        this.relatednum = relatednum;
    }

    public int getFavnum() {
        return favnum;
    }

    public void setFavnum(int favnum) {
        this.favnum = favnum;
    }

    public long getRankScore() {
        return rankScore;
    }

    public void setRankScore(long rankScore) {
        this.rankScore = rankScore;
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

    public String getDeviceTitle() {
        return deviceTitle;
    }

    public void setDeviceTitle(String deviceTitle) {
        this.deviceTitle = deviceTitle;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getFetchType() {
        return fetchType;
    }

    public void setFetchType(String fetchType) {
        this.fetchType = fetchType;
    }

    public String getAvatarFetchType() {
        return avatarFetchType;
    }

    public void setAvatarFetchType(String avatarFetchType) {
        this.avatarFetchType = avatarFetchType;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFeedType() {
        return feedType;
    }

    public void setFeedType(String feedType) {
        this.feedType = feedType;
    }

    public String getFeedTypeName() {
        return feedTypeName;
    }

    public void setFeedTypeName(String feedTypeName) {
        this.feedTypeName = feedTypeName;
    }

    public String getInfoHtml() {
        return infoHtml;
    }

    public void setInfoHtml(String infoHtml) {
        this.infoHtml = infoHtml;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getTurlTarget() {
        return turlTarget;
    }

    public void setTurlTarget(String turlTarget) {
        this.turlTarget = turlTarget;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getPicArr() {
        return picArr;
    }

    public void setPicArr(List<String> picArr) {
        this.picArr = picArr;
    }

    public String getSourceFeed() {
        return sourceFeed;
    }

    public void setSourceFeed(String sourceFeed) {
        this.sourceFeed = sourceFeed;
    }

    public Map<String, String> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Map<String, String> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }
}