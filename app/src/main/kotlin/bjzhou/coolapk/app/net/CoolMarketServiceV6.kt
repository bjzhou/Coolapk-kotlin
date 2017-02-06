package bjzhou.coolapk.app.net

import bjzhou.coolapk.app.model.CardEntity
import bjzhou.coolapk.app.model.PictureEntity
import bjzhou.coolapk.app.model.Result
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * author: zhoubinjia
 * date: 2017/1/24
 */
interface CoolMarketServiceV6 {

    @GET("main/init")
    fun init(): Observable<Result<List<CardEntity>>>

    @GET("picture/list")
    fun getPictureList(@Query("tag") tag: String, @Query("type") type: String, @Query("page") page: Int, @Query("firstItem") firstItem: String, @Query("lastItem") lastItem: String): Observable<Result<List<PictureEntity>>>
}
