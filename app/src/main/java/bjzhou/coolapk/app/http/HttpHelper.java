package bjzhou.coolapk.app.http;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.Html;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bjzhou.coolapk.app.model.Apk;
import bjzhou.coolapk.app.model.ApkField;
import bjzhou.coolapk.app.model.Comment;
import bjzhou.coolapk.app.model.UpgradeApk;
import bjzhou.coolapk.app.model.UpgradeApkExtend;
import bjzhou.coolapk.app.util.Constant;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by bjzhou on 14-7-29.
 */
public class HttpHelper {

    private static final String TAG = "HttpHelper";

    private static HttpHelper instance;
    private final CoolapkService mService;

    private HttpHelper() {
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
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constant.COOLAPK_PREURL)
                .build();

        mService = retrofit.create(CoolapkService.class);
    }

    public static HttpHelper getInstance(Context context) {
        if (instance == null) {
            instance = new HttpHelper();
        }
        return instance;
    }

    public static boolean checkNetworkState(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public void obtainHomepageApkList(final int page, final Handler handler) {
        mService.obtainHomepageApkList(page).enqueue(new Callback<List<Apk>>() {
            @Override
            public void onResponse(Call<List<Apk>> call, retrofit2.Response<List<Apk>> response) {
                Message msg = new Message();
                msg.obj = response.body();
                if (page == 1) {
                    msg.what = Constant.MSG_OBTAIN_COMPLETE;
                } else {
                    msg.what = Constant.MSG_OBTAIN_MORE_COMPLETE;
                }
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<List<Apk>> call, Throwable t) {
                Message msg = new Message();
                msg.what = Constant.MSG_OBTAIN_FAILED;
                handler.sendMessage(msg);
            }
        });
    }

    public void obtainApkField(int id, final Handler handler) {
        mService.obtainApkField(id).enqueue(new Callback<ApkField>() {
            @Override
            public void onResponse(Call<ApkField> call, retrofit2.Response<ApkField> response) {
                Message msg = new Message();
                msg.obj = response.body();
                msg.what = 1;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<ApkField> call, Throwable t) {
                Message msg = new Message();
                msg.what = Constant.MSG_OBTAIN_FAILED;
                handler.sendMessage(msg);
            }
        });
    }

    public void obtainSearchApkList(String query, final int page, final Handler handler) {
        mService.obtainSearchApkList(query, page).enqueue(new Callback<List<Apk>>() {
            @Override
            public void onResponse(Call<List<Apk>> call, retrofit2.Response<List<Apk>> response) {
                Message msg = new Message();
                msg.obj = response.body();
                if (page == 1) {
                    msg.what = Constant.MSG_OBTAIN_COMPLETE;
                } else {
                    msg.what = Constant.MSG_OBTAIN_MORE_COMPLETE;
                }
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<List<Apk>> call, Throwable t) {
                Message msg = new Message();
                msg.what = Constant.MSG_OBTAIN_FAILED;
                handler.sendMessage(msg);
            }
        });
    }

    public void obtainCommentList(int id, final int page, final Handler handler) {
        mService.obtainCommentList(id, page).enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, retrofit2.Response<List<Comment>> response) {
                Message msg = new Message();
                msg.obj = response.body();
                if (page == 1) {
                    msg.what = Constant.MSG_OBTAIN_COMPLETE;
                } else {
                    msg.what = Constant.MSG_OBTAIN_MORE_COMPLETE;
                }
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Message msg = new Message();
                msg.what = Constant.MSG_OBTAIN_FAILED;
                handler.sendMessage(msg);
            }
        });
    }

    public void obtainUpgradeVersions(final Context context, final Handler handler) {
        if (context == null) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                final PackageManager pm = context.getPackageManager();
                List<ApplicationInfo> all = pm.getInstalledApplications(0);
                StringBuilder sb = new StringBuilder();
                sb.append("sdk=").append(Build.VERSION.SDK_INT);
                sb.append("&pkgs=");
                for (ApplicationInfo info : all) {
                    if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                        sb.append(info.packageName).append(",");
                    }
                }
                mService.obtainUpgradeVersions(sb.substring(0, sb.length() - 1)).enqueue(new Callback<List<UpgradeApk>>() {
                    @Override
                    public void onResponse(Call<List<UpgradeApk>> call, retrofit2.Response<List<UpgradeApk>> response) {
                        Message msg = new Message();
                        List<UpgradeApkExtend> updateList = new ArrayList<UpgradeApkExtend>();
                        for (UpgradeApk apk : response.body()) {
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
                            } catch (PackageManager.NameNotFoundException e) {
                            }
                        }
                        msg.obj = updateList;
                        msg.what = Constant.MSG_OBTAIN_COMPLETE;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onFailure(Call<List<UpgradeApk>> call, Throwable t) {
                        Message msg = new Message();
                        msg.what = Constant.MSG_OBTAIN_FAILED;
                        handler.sendMessage(msg);
                    }
                });
            }
        }).start();
    }
}
