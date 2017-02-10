package bjzhou.coolapk.app.net

import android.content.Context
import android.content.pm.ApplicationInfo
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
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

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
        val client = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addInterceptor({ chain ->
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
                    chain.proceed(request)
                }).build()
        val retrofit = Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constant.COOL_MARKET_PREURL_V6)
                .build()
        mServiceV6 = retrofit.create(CoolMarketServiceV6::class.java)
    }

    private fun initService() {
        val client = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addInterceptor { chain ->
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
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constant.COOLAPK_PREURL)
                .build()

        mService = retrofit.create(CoolapkService::class.java)
    }

    fun obtainHomepageApkList(page: Int): Observable<List<Apk>> {
        return rxApi(mService.obtainHomepageApkList(page), emptyList())
    }

    fun obtainApkField(id: Int): Observable<ApkField> {
        return rxApi(mService.obtainApkField(id), ApkField())
    }

    fun obtainSearchApkList(query: String, page: Int): Observable<List<Apk>> {
        return rxApi(mService.obtainSearchApkList(query, page), emptyList())
    }

    fun obtainCommentList(id: Int, page: Int): Observable<List<Comment>> {
        return rxApi(mService.obtainCommentList(id, page), emptyList())
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
                .concatMap {
                    rxApi(mService.obtainUpgradeVersions(it), emptyList())
                }
                .map { apks ->
                    Log.d(TAG, "apply: onResponse")
                    apks.filter {
                        val pi = pm.getPackageInfo(it.apkname, 0)
                        it.apkversioncode > pi.versionCode
                    }.map {
                        val pi = pm.getPackageInfo(it.apkname, 0)
                        val ext = UpgradeApkExtend()
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
        return rxApiV6(mServiceV6.init(), emptyList())
    }

    fun pictureList(type: String, page: Int): Observable<List<PictureEntity>> {
        return rxApiV6(mServiceV6.getPictureList("", type, 0, "", ""), emptyList())
    }

    private fun <T> rxApi(call: Call<T>, empty: T): Observable<T> {
        return Observable.create<T> {
            call.enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable?) {
                    it.onError(t)
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    if (response.isSuccessful) {
                        it.onNext(response.body() ?: empty)
                        it.onComplete()
                    } else {
                        it.onError(Error("networkError: code: ${response.code()}, message: ${response.message()}"))
                    }
                }
            })
        }.observeOn(AndroidSchedulers.mainThread())
    }

    private fun <T> rxApiV6(call: Call<Result<T>>, empty: T): Observable<T> {
        return Observable.create<T> {
            call.enqueue(object : Callback<Result<T>> {
                override fun onFailure(call: Call<Result<T>>, t: Throwable?) {
                    it.onError(t)
                }

                override fun onResponse(call: Call<Result<T>>, response: Response<Result<T>>) {
                    if (response.isSuccessful) {
                        val exception = response.body().checkResult()
                        if (exception != null) {
                            it.onError(exception)
                        }
                        it.onNext(response.body().data ?: empty)
                        it.onComplete()
                    } else {
                        it.onError(Error("networkError: code: ${response.code()}, message: ${response.message()}"))
                    }
                }
            })
        }.observeOn(AndroidSchedulers.mainThread())
    }

    companion object {

        private val TAG = ApiManager::class.java.simpleName

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
