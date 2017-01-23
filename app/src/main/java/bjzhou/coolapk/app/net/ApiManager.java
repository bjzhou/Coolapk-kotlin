package bjzhou.coolapk.app.net;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.Html;
import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bjzhou.coolapk.app.model.Apk;
import bjzhou.coolapk.app.model.ApkField;
import bjzhou.coolapk.app.model.Comment;
import bjzhou.coolapk.app.model.UpgradeApkExtend;
import bjzhou.coolapk.app.util.Constant;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by bjzhou on 14-7-29.
 */
public class ApiManager {

    private static final String TAG = "ApiManager";

    private static ApiManager instance;
    private final CoolapkService mService;

    private ApiManager() {
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl url = original.url().newBuilder()
                        .addQueryParameter("apikey", Constant.API_KEY)
                        .build();

                Request.Builder requestBuilder = original.newBuilder()
                        .header("Cookie", "coolapk_did=" + Constant.COOLAPK_DID)
                        .url(url);

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        }).build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constant.COOLAPK_PREURL)
                .build();

        mService = retrofit.create(CoolapkService.class);
    }

    public static ApiManager getInstance() {
        if (instance == null) {
            instance = new ApiManager();
        }
        return instance;
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifi.isConnected();
    }

    public static boolean checkNetworkState(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public Observable<List<Apk>> obtainHomepageApkList(int page) {
        return mService.obtainHomepageApkList(page)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<ApkField> obtainApkField(int id) {
        return mService.obtainApkField(id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Apk>> obtainSearchApkList(String query, int page) {
        return mService.obtainSearchApkList(query, page)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Comment>> obtainCommentList(int id, int page) {
        return mService.obtainCommentList(id, page)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<UpgradeApkExtend>> obtainUpgradeVersions(Context context) {
        final PackageManager pm = context.getPackageManager();
        return Observable
                .create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> e) throws Exception {
                        List<ApplicationInfo> all = pm.getInstalledApplications(0);
                        StringBuilder sb = new StringBuilder();
                        sb.append("sdk=").append(Build.VERSION.SDK_INT);
                        sb.append("&pkgs=");
                        for (ApplicationInfo info : all) {
                            if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                                sb.append(info.packageName).append(",");
                            }
                        }
                        Log.d(TAG, "subscribe: " + sb);
                        e.onNext(sb.toString());
                        e.onComplete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .concatMap(new Function<String, ObservableSource<List<Apk>>>() {
                    @Override
                    public ObservableSource<List<Apk>> apply(String s) throws Exception {
                        Log.d(TAG, "apply: obtainUpgradeVersions");
                        return mService.obtainUpgradeVersions(s);
                    }
                })
                .map(new Function<List<Apk>, List<UpgradeApkExtend>>() {
                    @Override
                    public List<UpgradeApkExtend> apply(List<Apk> apks) throws Exception {
                        List<UpgradeApkExtend> updateList = new ArrayList<UpgradeApkExtend>();
                        Log.d(TAG, "apply: onResponse");
                        for (Apk apk : apks) {
                            try {
                                PackageInfo pi = pm.getPackageInfo(apk.getApkname(), 0);
                                if (apk.getApkversioncode() > pi.versionCode) {
                                    UpgradeApkExtend ext = new UpgradeApkExtend();
                                    ext.setTitle(pi.applicationInfo.loadLabel(pm).toString());
                                    ext.setLogo(pi.applicationInfo.loadIcon(pm));
                                    String info = "<font color=\"#ff35a1d4\">" + pi.versionName + "</font>"
                                            + ">>"
                                            + "<font color=\"red\">" + apk.getApkversionname() + "</font>"
                                            + "<font color=\"black\">(" + apk.getApksize() + ")</font>";
                                    ext.setInfo(Html.fromHtml(info));
                                    ext.setApk(apk);
                                    updateList.add(ext);
                                }
                            } catch (PackageManager.NameNotFoundException ignored) {
                            }
                        }
                        return updateList;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }
}
