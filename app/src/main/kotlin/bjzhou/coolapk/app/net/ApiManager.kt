package bjzhou.coolapk.app.net

import android.content.Context
import android.content.pm.ApplicationInfo
import android.net.ConnectivityManager
import android.os.Build
import android.text.Html
import android.util.Log
import bjzhou.coolapk.app.model.UpgradeApkExtend
import bjzhou.coolapk.app.util.Constant
import bjzhou.coolapk.app.retrofit.KCallAdapterFactory
import bjzhou.coolapk.app.retrofit.V6GsonConverterFactory
import bjzhou.coolapk.app.util.Utils
import com.coolapk.market.util.AuthUtils
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by bjzhou on 14-7-29.
 */
class ApiManager private constructor() {
    lateinit var mService: CoolapkService
    lateinit var mServiceV6: CoolMarketServiceV6

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
                .addCallAdapterFactory(KCallAdapterFactory())
                .addConverterFactory(V6GsonConverterFactory.create())
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
                .addCallAdapterFactory(KCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constant.COOLAPK_PREURL)
                .build()

        mService = retrofit.create(CoolapkService::class.java)
    }

    fun obtainUpgradeVersions(context: Context): Deferred<List<UpgradeApkExtend>> {
        return async {
            val pm = context.packageManager
            val all = pm.getInstalledApplications(0)
            val sb = StringBuilder()
            sb.append("sdk=").append(Build.VERSION.SDK_INT)
            sb.append("&pkgs=")
            all
                    .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
                    .forEach { sb.append(it.packageName).append(",") }
            Log.d(TAG, "subscribe: $sb")
            val apks = mService.obtainUpgradeVersions(sb.toString()).execute() ?: emptyList()
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
