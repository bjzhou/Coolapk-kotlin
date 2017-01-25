package bjzhou.coolapk.app.net;

import android.support.annotation.NonNull;

import java.util.List;

import bjzhou.coolapk.app.model.CardEntity;
import bjzhou.coolapk.app.model.PictureEntity;
import bjzhou.coolapk.app.model.Result;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * author: zhoubinjia
 * date: 2017/1/24
 */
public interface CoolMarketServiceV6 {

    @GET("main/init")
    Observable<Result<List<CardEntity>>> init();

    @GET("picture/list")
    Observable<Result<List<PictureEntity>>> getPictureList(@Query("tag") @NonNull String tag, @Query("type") @NonNull String type, @Query("page") int page, @Query("firstItem") String firstItem, @Query("lastItem") String lastItem);
}
