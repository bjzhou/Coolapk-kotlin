package bjzhou.coolapk.app.net

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.text.Html
import android.util.Log
import android.widget.Toast
import bjzhou.coolapk.app.App
import bjzhou.coolapk.app.model.*
import bjzhou.coolapk.app.util.Constant
import bjzhou.coolapk.app.util.Utils
import com.coolapk.market.util.AuthUtils
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

/**
 * Created by bjzhou on 14-7-29.
 */
class ApiManager private constructor() {
    private lateinit var mService: CoolapkService
    private lateinit var mServiceV6: CoolMarketServiceV6

    init {
        initService()
        initServiceV6()
    }

    private fun initServiceV6() {
        val client = OkHttpClient.Builder().addInterceptor({ chain ->
            try {
                val original = chain.request()

                Log.d(TAG, "url: " + original.url())

                val requestBuilder = original.newBuilder()
                        .header("User-Agent", Utils.userAgent)
                        .header("X-Requested-With", "XMLHttpRequest")
                        .header("X-Sdk-Int", Build.VERSION.SDK_INT.toString())
                        .header("X-Sdk-Locale", Utils.localeString)
                        .header("X-App-Id", "coolmarket")
                        .header("X-App-Token", AuthUtils.getAS(Utils.uuid))
                        .header("X-App-Version", "7.3")
                        .header("X-App-Code", "1701135")
                        .header("X-Api-Version", "7")

                val request = requestBuilder.build()
                //                Log.d(TAG, "headers: " + request.headers());
                chain.proceed(request)
            } catch (e: IOException) {
                //FIXME: call observer.onError if time out.
                e.printStackTrace()
                null
            }
        }).build()
        val retrofit = Retrofit.Builder()
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constant.COOL_MARKET_PREURL_V6)
                .build()

        mServiceV6 = retrofit.create(CoolMarketServiceV6::class.java)
    }

    private fun initService() {
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val original = chain.request()
            val url = original.url().newBuilder()
                    .addQueryParameter("apikey", Constant.API_KEY)
                    .build()

            val requestBuilder = original.newBuilder()
                    .header("Cookie", "coolapk_did=" + Constant.COOLAPK_DID)
                    .url(url)

            val request = requestBuilder.build()
            chain.proceed(request)
        }.build()
        val retrofit = Retrofit.Builder()
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constant.COOLAPK_PREURL)
                .build()

        mService = retrofit.create(CoolapkService::class.java)
    }

    fun obtainHomepageApkList(page: Int): Observable<List<Apk>> {
        return mService.obtainHomepageApkList(page)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun obtainApkField(id: Int): Observable<ApkField> {
        return mService.obtainApkField(id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun obtainSearchApkList(query: String, page: Int): Observable<List<Apk>> {
        return mService.obtainSearchApkList(query, page)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun obtainCommentList(id: Int, page: Int): Observable<List<Comment>> {
        return mService.obtainCommentList(id, page)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun obtainUpgradeVersions(context: Context): Observable<List<UpgradeApkExtend>> {
        val pm = context.packageManager
        return Observable
                .create(ObservableOnSubscribe<String> { e ->
                    val all = pm.getInstalledApplications(0)
                    val sb = StringBuilder()
                    sb.append("sdk=").append(Build.VERSION.SDK_INT)
                    sb.append("&pkgs=")
                    all
                            .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
                            .forEach { sb.append(it.packageName).append(",") }
                    Log.d(TAG, "subscribe: $sb")
                    e.onNext(sb.toString())
                    e.onComplete()
                })
                .subscribeOn(Schedulers.io())
                .concatMap { s ->
                    Log.d(TAG, "apply: obtainUpgradeVersions")
                    mService.obtainUpgradeVersions(s)
                }
                .map { apks ->
                    Log.d(TAG, "apply: onResponse")
                    apks.map {
                        val ext = UpgradeApkExtend()
                        try {
                            val pi = pm.getPackageInfo(it.apkname, 0)
                            if (it.apkversioncode > pi.versionCode) {
                                ext.title = pi.applicationInfo.loadLabel(pm).toString()
                                ext.logo = pi.applicationInfo.loadIcon(pm)
                                val info = "<font color=\"#ff35a1d4\">" +
                                        pi.versionName +
                                        "</font>" + ">>" +
                                        "<font color=\"red\">" +
                                        it.apkversionname + "</font>" +
                                        "<font color=\"black\">(" + it.apksize + ")</font>"
                                ext.info = Html.fromHtml(info)
                                ext.apk = it
                            }
                        } catch (ignored: PackageManager.NameNotFoundException) {
                        }
                        ext
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn { throwable ->
                    Toast.makeText(App.context, throwable.toString(), Toast.LENGTH_SHORT).show()
                    emptyList()
                }
    }

    fun init(): Observable<List<CardEntity>> {
        return mServiceV6.init()
                .map(ResultHandlerV6<List<CardEntity>>())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun pictureList(type: String, page: Int): Observable<List<PictureEntity>> {
        return mServiceV6.getPictureList("", type, 0, "", "")
                .map(ResultHandlerV6<List<PictureEntity>>())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
    }

    private inner class ResultHandlerV6<T> : Function<Result<T>, T> {

        @Throws(Exception::class)
        override fun apply(tResult: Result<T>): T? {
            val exception = tResult.checkResult()
            if (exception != null) {
                throw exception
            }
            return tResult.data
        }
    }

    companion object {

        private val TAG = "ApiManager"

        var instance: ApiManager = ApiManager()
            private set

        fun isWifiConnected(context: Context): Boolean {
            val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            return wifi.isConnected
        }

        fun checkNetworkState(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo != null && cm.activeNetworkInfo.isConnectedOrConnecting
        }
    }
}
