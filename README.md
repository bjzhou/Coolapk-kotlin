Coolapk
=======

酷安开源版

### 酷市场 V7 版 API 相关

所有接口使用统一的前缀： https://api.coolapk.com/v6/

所有接口使用统一的 HTTP 头：

```
User-Agent: Dalvik/2.1.0 (Linux; U; Android 5.1.1; Nexus 4 Build/LMY48T) (#Build; google; Nexus 4; LMY48T; 5.1.1) +CoolMarket/7.3
X-Requested-With: XMLHttpRequest
X-Sdk-Int: 22
X-Sdk-Locale: zh-CN
X-App-Id: coolmarket
X-App-Token: 2a6e2adc2897c8d8133db17c2cd3b1045834ce58-d7d5-38eb-95d5-563167a1983d0x588f16cd
X-App-Version: 7.3
X-App-Code: 1701135
X-Api-Version: 7
```
具体参数获取方式可参考ApiManager.java，其中X-App-Token的生成是c++写的，直接引用了酷市场的 so 文件，不清楚具体算法，组合方式应该是`校验和+uuid+时间戳`

本项目介绍了 `main/init` 和 `picture/list` 两个接口的具体使用方法，分别是获取启动图和所有酷图的，其他接口可参考下面的 retrofit 接口文件：

```java
public interface CoolMarketService {
        @POST("feed/favorite")
        Observable<Result<Integer>> m3639A(@Query("id") String str);

        @POST("user/follow")
        Observable<Result<Integer>> m3640B(@Query("uid") String str);

        @POST("user/unfollow")
        Observable<Result<Integer>> m3641C(@Query("uid") String str);

        @POST("feed/cancelRecommend")
        Observable<Result<String>> m3642D(@Query("id") String str);

        @POST("feed/Recommend")
        Observable<Result<String>> m3643E(@Query("id") String str);

        @POST("discovery/loadDiscoveryInfo")
        Observable<Result<DiscoveryQuery>> m3644F(@Query("query") String str);

        @GET("topic/tagDetail")
        Observable<Result<Topic>> m3645G(@Query("tag") String str);

        @GET("feed/followTag")
        Observable<Result<Integer>> m3646H(@Query("tag") String str);

        @GET("feed/unFollowTag")
        Observable<Result<Integer>> m3647I(@Query("tag") String str);

        @GET("message/receive")
        Observable<Result<Message>> m3648J(@Query("id") String str);

        @GET("message/read")
        Observable<Result<NotifyCount>> m3649K(@Query("ukey") String str);

        @GET("cloudInstall/task")
        Observable<Result<ServiceApp>> m3650L(@Query("id") String str);

        @GET("apk/qr")
        Observable<Result<ServiceApp>> m3651M(@Query("id") String str);

        @GET("main/index")
        Observable<Result<List<Entity>>> m3652a();

        @GET("main/headline")
        Observable<Result<List<Entity>>> m3653a(@Query("page") int i, @Query("firstItem") String str, @Query("lastItem") String str2);

        @GET("album/list")
        Observable<Result<List<Entity>>> m3654a(@Query("page") int i, @Query("firstItem") String str, @Query("lastItem") String str2, @Query("listType") String str3);

        @POST("account/changeAvatar")
        Observable<Result<String>> m3655a(@Body RequestBody requestBody);

        @GET("main/checkHeadlineCount")
        Observable<Result<Integer>> m3656a(@Query("firstItem") String str);

        @GET("apk/detail")
        Observable<Result<ServiceApp>> m3657a(@Query("id") String str, @Query("installed") int i);

        @POST("message/send")
        @Multipart
        Observable<Result<Message>> m3658a(@Query("uid") String str, @Query("quick_reply") int i, @Part("message") String str2);

        @GET("apk/index")
        Observable<Result<List<Entity>>> m3659a(@Query("apkType") String str, @Query("page") int i, @Query("firstItem") String str2, @Query("lastItem") String str3);

        @GET("feed/replyList")
        Observable<Result<List<FeedReply>>> m3660a(@Query("id") String str, @Query("page") int i, @Query("firstItem") String str2, @Query("lastItem") String str3, @Query("discussMode") int i2, @Query("feedType") String str4);

        @GET("topic/tagFeedList")
        Observable<Result<List<Entity>>> m3661a(@Query("tag") String str, @Query("page") int i, @Query("firstItem") String str2, @Query("lastItem") String str3, @Query("listType") String str4);

        @POST("album/addApk")
        Observable<Result<Integer>> m3662a(@Query("id") String str, @Body RequestBody requestBody);

        @POST("apk/checkUpdate")
        @Multipart
        Observable<Result<List<PatchInfo>>> m3663a(@Part("pkgs") String str, @Query("coolmarket_beta") String str2);

        @GET("apk/search")
        Observable<Result<List<Entity>>> m3664a(@Query("q") String str, @Query("apkType") String str2, @Query("page") int i, @Query("firstItem") String str3, @Query("lastItem") String str4);

        @POST("feed/uploadImage")
        Observable<Result<String>> m3665a(@Query("fieldName") String str, @Query("uploadDir") String str2, @Body RequestBody requestBody);

        @GET("album/index")
        Observable<Result<List<Entity>>> m3666a(@Query("page") String str, @Query("firstItem") String str2, @Query("lastItem") String str3);

        @GET("apk/index?listType=cat")
        Observable<Result<List<Entity>>> m3667a(@Query("catId") String str, @Query("apkType") String str2, @Query("rankType") String str3, @Query("page") int i, @Query("firstItem") String str4, @Query("lastItem") String str5);

        @GET("apk/recommendList")
        Observable<Result<List<Entity>>> m3668a(@Query("apkType") String str, @Query("title") String str2, @Query("subTitle") String str3, @Query("action") String str4, @Query("page") int i, @Query("firstItem") String str5, @Query("lastItem") String str6);

        @GET("apk/downloadStat")
        Observable<Result<Integer>> m3669a(@Query("pn") String str, @Query("aid") String str2, @Query("extra") String str3, @Query("ni") String str4, @Query("uninstall") String str5);

        @FormUrlEncoded
        @POST("album/addApk")
        Observable<Result<Integer>> m3670a(@Query("id") String str, @Field("packageName") String str2, @Field("title") String str3, @Field("url") String str4, @Field("note") String str5, @Field("displayOrder") int i, @Field("logo") String str6);

        @GET("main/init")
        Observable<Result<List<Entity>>> init();

        @GET("main/updateList")
        Observable<Result<List<Entity>>> m3672b(@Query("page") int i, @Query("firstItem") String str, @Query("lastItem") String str2);

        @POST("feed/newFeed")
        Observable<Result<Feed>> m3673b(@Body RequestBody requestBody);

        @GET("apk/realRankList")
        Observable<Result<List<ServiceApp>>> m3674b(@Query("apkType") String str);

        @GET("apk/rating")
        Observable<Result<Map<String, String>>> m3675b(@Query("id") String str, @Query("value") int i);

        @GET("apk/search?searchType=developer")
        Observable<Result<List<ServiceApp>>> m3676b(@Query("developer") String str, @Query("page") int i, @Query("firstItem") String str2, @Query("lastItem") String str3);

        @POST("apk/comment")
        Observable<Result<Feed>> m3677b(@Query("id") String str, @Body RequestBody requestBody);

        @FormUrlEncoded
        @POST("apk/unFavorite")
        Observable<Result<Integer>> m3678b(@Field("id") String str, @Field("targetType") String str2);

        @GET("picture/list")
        Observable<Result<List<Entity>>> m3679b(@Query("tag") @NonNull String str, @Query("type") @NonNull String str2, @Query("page") int i, @Query("firstItem") String str3, @Query("lastItem") String str4);

        @POST("feed/reply")
        Observable<Result<FeedReply>> m3680b(@Query("id") String str, @Query("type") String str2, @Body RequestBody requestBody);

        @FormUrlEncoded
        @POST("album/edit")
        Observable<Result<Integer>> m3681b(@Query("id") String str, @Field("title") String str2, @Field("intro") String str3);

        @GET("apk/search")
        Observable<Result<List<Entity>>> m3682b(@Query("q") String str, @Query("apkType") String str2, @Query("rankType") String str3, @Query("page") int i, @Query("firstItem") String str4, @Query("lastItem") String str5);

        @GET("device/ip")
        Observable<Result<String>> m3683c();

        @GET("apk/newestList")
        Observable<Result<List<Entity>>> m3684c(@Query("page") int i, @Query("firstItem") String str, @Query("lastItem") String str2);

        @POST("picture/newPicture")
        Observable<Result<Feed>> m3685c(@Body RequestBody requestBody);

        @GET("apk/categoryList")
        Observable<Result<List<Entity>>> m3686c(@Query("apkType") String str);

        @GET("user/albumlist")
        Observable<Result<List<Entity>>> m3687c(@Query("uid") String str, @Query("pageSize") int i);

        @GET("apk/discovererList")
        Observable<Result<List<RelatedData>>> m3688c(@Query("id") String str, @Query("page") int i, @Query("firstItem") String str2, @Query("lastItem") String str3);

        @GET("user/delGift")
        Observable<Result<String>> m3689c(@Query("docId") String str, @Query("gift") String str2);

        @GET("apk/commentList")
        Observable<Result<List<Feed>>> m3690c(@Query("id") String str, @Query("listType") String str2, @Query("page") int i, @Query("firstItem") String str3, @Query("lastItem") String str4);

        @POST("user/block")
        Observable<Result<String>> m3691c(@Query("uid") String str, @Query("action") String str2, @Query("clearTypes") String str3);

        @GET("apk/search?searchType=tag")
        Observable<Result<List<Entity>>> m3692c(@Query("tag") String str, @Query("apkType") String str2, @Query("rankType") String str3, @Query("page") int i, @Query("firstItem") String str4, @Query("lastItem") String str5);

        @GET("notification/checkCount")
        Observable<Result<NotifyCount>> m3693d();

        @GET("discovery/index")
        Observable<Result<List<Entity>>> m3694d(@Query("page") int i, @Query("firstItem") String str, @Query("lastItem") String str2);

        @POST("discovery/newDiscovery")
        Observable<Result<Integer>> m3695d(@Body RequestBody requestBody);

        @GET("apk/offline")
        Observable<Result<String>> m3696d(@Query("id") String str);

        @FormUrlEncoded
        @POST("user/deleteAvatar")
        Observable<Result<String>> m3697d(@Query("uid") String str, @Field("lock") int i);

        @GET("apk/ratingUserList")
        Observable<Result<List<RelatedData>>> m3698d(@Query("id") String str, @Query("page") int i, @Query("firstItem") String str2, @Query("lastItem") String str3);

        @POST("album/create")
        @Multipart
        Observable<Result<Integer>> m3699d(@Part("title") String str, @Part("intro") String str2);

        @GET("{feedType}/search")
        Observable<Result<List<Entity>>> m3700d(@Path("feedType") String str, @Query("q") String str2, @Query("page") int i, @Query("firstItem") String str3, @Query("lastItem") String str4);

        @GET("apk/giftList")
        Observable<Result<List<Gift>>> m3701e(@Query("page") int i, @Query("firstItem") String str, @Query("lastItem") String str2);

        @GET("picture/addToSplash")
        Observable<Result<String>> m3702e(@Query("id") String str);

        @POST("feed/like")
        Observable<Result<LikeResult>> m3703e(@Query("id") String str, @Query("detail") int i);

        @GET("picture/userPictures")
        Observable<Result<List<Entity>>> m3704e(@Query("uid") @NonNull String str, @Query("page") int i, @Query("firstItem") String str2, @Query("lastItem") String str3);

        @POST("album/delApk")
        @Multipart
        Observable<Result<String>> m3705e(@Query("id") String str, @Part("packageName") String str2);

        @GET("user/blackList")
        Observable<Result<List<User>>> m3706f(@Query("page") int i, @Query("firstItem") String str, @Query("lastItem") String str2);

        @GET("apk/downloadVersionList")
        Observable<Result<List<VersionApp>>> m3707f(@Query("id") String str);

        @POST("feed/unlike")
        Observable<Result<LikeResult>> m3708f(@Query("id") String str, @Query("detail") int i);

        @GET("apk/developerAppList")
        Observable<Result<List<Entity>>> m3709f(@Query("uid") String str, @Query("page") int i, @Query("firstItem") String str2, @Query("lastItem") String str3);

        @FormUrlEncoded
        @POST("album/editApkDisplayOrder")
        Observable<Result<String>> m3710f(@Query("id") String str, @Field("displayOrder") String str2);

        @GET("user/ignoreList")
        Observable<Result<List<User>>> m3711g(@Query("page") int i, @Query("firstItem") String str, @Query("lastItem") String str2);

        @GET("apk/follow")
        Observable<Result<Map<String, String>>> m3712g(@Query("id") String str);

        @POST("feed/deleteFeed")
        Observable<Result<String>> m3713g(@Query("id") String str, @Query("notNotify") int i);

        @GET("apk/giftList")
        Observable<Result<List<Gift>>> m3714g(@Query("apkId") String str, @Query("page") int i, @Query("firstItem") String str2, @Query("lastItem") String str3);

        @POST("album/unFavorite")
        Observable<Result<Integer>> m3715g(@Query("id") String str, @Query("targetType") String str2);

        @GET("user/limitList")
        Observable<Result<List<User>>> m3716h(@Query("page") int i, @Query("firstItem") String str, @Query("lastItem") String str2);

        @GET("apk/url")
        Observable<Result<String>> m3717h(@Query("id") String str);

        @POST("feed/deleteReply")
        Observable<Result<String>> m3718h(@Query("id") String str, @Query("notNotify") int i);

        @GET("user/giftList")
        Observable<Result<List<Gift>>> m3719h(@Query("uid") String str, @Query("page") int i, @Query("firstItem") String str2, @Query("lastItem") String str3);

        @GET("feed/detail")
        Observable<Result<Feed>> m3720h(@Query("id") String str, @Query("rid") String str2);

        @GET("topic/recentFeedList")
        Observable<Result<List<Entity>>> m3721i(@Query("page") int i, @Query("firstItem") String str, @Query("lastItem") String str2);

        @GET("apk/unfollow")
        Observable<Result<Map<String, String>>> m3722i(@Query("id") String str);

        @GET("album/search")
        Observable<Result<List<Entity>>> m3723i(@Query("q") String str, @Query("page") int i, @Query("firstItem") String str2, @Query("lastItem") String str3);

        @POST("feed/unFavorite")
        Observable<Result<Integer>> m3724i(@Query("id") String str, @Query("targetType") String str2);

        @GET("topic/hotFeedList")
        Observable<Result<List<Entity>>> m3725j(@Query("page") int i, @Query("firstItem") String str, @Query("lastItem") String str2);

        @FormUrlEncoded
        @POST("apk/favorite")
        Observable<Result<Integer>> m3726j(@Field("id") String str);

        @GET("user/albumlist")
        Observable<Result<List<Entity>>> m3727j(@Query("uid") String str, @Query("page") int i, @Query("firstItem") String str2, @Query("lastItem") String str3);

        @POST("message/send")
        @Multipart
        Observable<Result<Message>> m3728j(@Query("uid") String str, @Part("message") String str2);

        @GET("topic/tagList")
        Observable<Result<List<Topic>>> m3729k(@Query("page") int i, @Query("firstItem") String str, @Query("lastItem") String str2);

        @GET("apk/unrating")
        Observable<Result<Map<String, String>>> m3730k(@Query("id") String str);

        @GET("album/replyList")
        Observable<Result<List<FeedReply>>> m3731k(@Query("id") String str, @Query("page") int i, @Query("firstItem") String str2, @Query("lastItem") String str3);

        @POST("account/changeProfile")
        @Multipart
        Observable<Result<UserProfile>> m3732k(@Part("key") String str, @Part("value") String str2);

        @GET("user/replyToMeList")
        Observable<Result<List<Entity>>> m3733l(@Query("page") int i, @Query("firstItem") String str, @Query("lastItem") String str2);

        @GET("apk/getGift")
        Observable<Result<String>> m3734l(@Query("docId") String str);

        @GET("user/apkRatingList")
        Observable<Result<List<Entity>>> m3735l(@Query("uid") String str, @Query("page") int i, @Query("firstItem") String str2, @Query("lastItem") String str3);

        @GET("feed/editorChoiceList")
        Observable<Result<List<Entity>>> m3736m(@Query("page") int i, @Query("firstItem") String str, @Query("lastItem") String str2);

        @GET("album/detail")
        Observable<Result<Album>> m3737m(@Query("id") String str);

        @GET("topic/tagFeedList")
        Observable<Result<List<Entity>>> m3738m(@Query("tag") String str, @Query("page") int i, @Query("firstItem") String str2, @Query("lastItem") String str3);

        @GET("user/followTagList")
        Observable<Result<List<Topic>>> m3739n(@Query("page") int i, @Query("firstItem") String str, @Query("lastItem") String str2);

        @POST("album/recommend")
        Observable<Result<String>> m3740n(@Query("id") String str);

        @GET("user/feedList")
        Observable<Result<List<Entity>>> m3741n(@Query("uid") String str, @Query("page") int i, @Query("firstItem") String str2, @Query("lastItem") String str3);

        @GET("notification/list")
        Observable<Result<List<Entity>>> m3742o(@Query("page") int i, @Query("firstItem") String str, @Query("lastItem") String str2);

        @FormUrlEncoded
        @POST("album/favorite")
        Observable<Result<Integer>> m3743o(@Field("id") String str);

        @GET("topic/feedList")
        Observable<Result<List<Entity>>> m3744o(@Query("type") String str, @Query("page") int i, @Query("firstItem") String str2, @Query("lastItem") String str3);

        @GET("notification/atCommentMeList")
        Observable<Result<List<Entity>>> m3745p(@Query("page") int i, @Query("firstItem") String str, @Query("lastItem") String str2);

        @GET("account/accessToken")
        Observable<Result<LoginInfo>> m3746p(@Query("code") String str);

        @GET("user/likeList")
        Observable<Result<List<Entity>>> m3747p(@Query("uid") String str, @Query("page") int i, @Query("firstItem") String str2, @Query("lastItem") String str3);

        @GET("notification/atMeList")
        Observable<Result<List<Entity>>> m3748q(@Query("page") int i, @Query("firstItem") String str, @Query("lastItem") String str2);

        @GET("user/profile")
        Observable<Result<UserProfile>> m3749q(@Query("uid") String str);

        @GET("user/apkCommentList")
        Observable<Result<List<Entity>>> m3750q(@Query("uid") String str, @Query("page") int i, @Query("firstItem") String str2, @Query("lastItem") String str3);

        @GET("notification/commentMeList")
        Observable<Result<List<Entity>>> m3751r(@Query("page") int i, @Query("firstItem") String str, @Query("lastItem") String str2);

        @POST("user/addToBlackList")
        Observable<Result<String>> m3752r(@Query("uid") String str);

        @GET("user/discoveryList")
        Observable<Result<List<Entity>>> m3753r(@Query("uid") String str, @Query("page") int i, @Query("firstItem") String str2, @Query("lastItem") String str3);

        @GET("notification/feedLikeList")
        Observable<Result<List<Entity>>> m3754s(@Query("page") int i, @Query("firstItem") String str, @Query("lastItem") String str2);

        @POST("user/removeFromBlackList")
        Observable<Result<String>> m3755s(@Query("uid") String str);

        @GET("user/apkFollowList")
        Observable<Result<List<Entity>>> m3756s(@Query("uid") String str, @Query("page") int i, @Query("firstItem") String str2, @Query("lastItem") String str3);

        @GET("message/list")
        Observable<Result<List<Message>>> m3757t(@Query("page") int i, @Query("firstItem") String str, @Query("lastItem") String str2);

        @POST("user/addToIgnoreList")
        Observable<Result<String>> m3758t(@Query("uid") String str);

        @GET("user/replyList")
        Observable<Result<List<Entity>>> m3759t(@Query("uid") String str, @Query("page") int i, @Query("firstItem") String str2, @Query("lastItem") String str3);

        @GET("feed/newestList")
        Observable<Result<List<Entity>>> m3760u(@Query("page") int i, @Query("firstItem") String str, @Query("lastItem") String str2);

        @POST("user/removeFromIgnoreList")
        Observable<Result<String>> m3761u(@Query("uid") String str);

        @GET("user/followList")
        Observable<Result<List<Entity>>> m3762u(@Query("uid") String str, @Query("page") int i, @Query("firstItem") String str2, @Query("lastItem") String str3);

        @GET("feed/newestReplyList")
        Observable<Result<List<Entity>>> m3763v(@Query("page") int i, @Query("firstItem") String str, @Query("lastItem") String str2);

        @POST("user/addToLimitList")
        Observable<Result<String>> m3764v(@Query("uid") String str);

        @GET("user/fansList")
        Observable<Result<List<Entity>>> m3765v(@Query("uid") String str, @Query("page") int i, @Query("firstItem") String str2, @Query("lastItem") String str3);

        @POST("user/removeFromLimitList")
        Observable<Result<String>> m3766w(@Query("uid") String str);

        @GET("user/search")
        Observable<Result<List<Entity>>> m3767w(@Query("q") String str, @Query("page") int i, @Query("firstItem") String str2, @Query("lastItem") String str3);

        @GET("feed/replyDetail")
        Observable<Result<FeedReply>> m3768x(@Query("id") String str);

        @GET("message/chat")
        Observable<Result<List<Message>>> m3769x(@Query("ukey") String str, @Query("page") int i, @Query("firstItem") String str2, @Query("lastItem") String str3);

        @GET("feed/addToHeadline")
        Observable<Result<String>> m3770y(@Query("feedId") String str);

        @GET("favorite/list")
        Observable<Result<List<Entity>>> m3771y(@Query("type") String str, @Query("page") int i, @Query("firstItem") String str2, @Query("lastItem") String str3);

        @GET("feed/removeFromHeadline")
        Observable<Result<String>> m3772z(@Query("feedId") String str);
}
```
### TODO

* 全部使用新版 API
* 分类
* 排行
* UI
