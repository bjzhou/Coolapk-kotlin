package bjzhou.coolapk.app.net

import bjzhou.coolapk.app.model.CardEntity
import bjzhou.coolapk.app.model.PictureEntity
import bjzhou.coolapk.app.retrofit.KCall
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * author: zhoubinjia
 * date: 2017/1/24
 */
interface CoolMarketServiceV6 {

    @GET("main/init")
    fun init(): KCall<List<CardEntity>>

    @GET("picture/list")
    fun getPictureList(@Query("tag") tag: String = "", @Query("type") type: String, @Query("page") page: Int = 0, @Query("firstItem") firstItem: String = "", @Query("lastItem") lastItem: String = ""): KCall<List<PictureEntity>>
}
