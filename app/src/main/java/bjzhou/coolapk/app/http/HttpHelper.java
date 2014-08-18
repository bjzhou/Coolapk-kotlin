package bjzhou.coolapk.app.http;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import bjzhou.coolapk.app.model.*;
import bjzhou.coolapk.app.util.Constant;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bjzhou on 14-7-29.
 */
public class HttpHelper {

    private static final String TAG = "HttpHelper";

    private static HttpHelper instance;
    private final Context mContext;

    private HttpHelper(Context context) {
        mContext = context;
    }

    public static HttpHelper getInstance(Context context) {
        if (instance == null) {
            instance = new HttpHelper(context);
        }
        return instance;
    }

    private static String inputStream2String(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuffer sb = new StringBuffer();

        String line = "";
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
        }

        try {
            in.close();
        } catch (IOException e) {
        }

        return sb.toString();
    }

    public static boolean checkNetworkState(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public void obtainHomepageApkList(final int page, final Handler handler) {
        if (!checkNetworkState(mContext)) {
            return;
        }
        final String url = String.format(Constant.COOLAPK_PREURL + Constant.METHOD_GET_HOMEPAGE_APKLIST, Constant.API_KEY, page);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = inputStream2String(coolapkHttpGet(url));
                Message msg = new Message();
                if (!TextUtils.isEmpty(result)) {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<ArrayList<Apk>>() {
                    }.getType();
                    List<Apk> list = gson.fromJson(result, listType);

                    for (Apk apk : list) {
                        String apk_info = "<font color=\"#ff35a1d4\">" + apk.getApkversionname() + "</font>";
                        apk_info += "<font color=\"black\">, " + apk.getApksize() + ", </font>";
                        if (apk.getUpdateFlag().equals("new")) {
                            apk_info += "<font color=\"red\">New</font>";
                        } else {
                            apk_info += "<font color=\"black\">Update</font>";
                        }
                        apk.setInfo(Html.fromHtml(apk_info));

                        Bitmap bitmap = null;
                        try {
                            bitmap = Picasso.with(mContext).load(apk.getLogo()).get();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        apk.setLogoBitmap(bitmap);
                    }

                    msg.obj = list;
                    if (page == 1) {
                        msg.what = Constant.MSG_OBTAIN_COMPLETE;
                    } else {
                        msg.what = Constant.MSG_OBTAIN_MORE_COMPLETE;
                    }
                } else {
                    msg.what = Constant.MSG_OBTAIN_FAILED;
                }

                handler.sendMessage(msg);
            }
        }).start();
    }

    public void obtainApkField(int id, final Handler handler) {
        if (!checkNetworkState(mContext)) {
            return;
        }
        final String url = String.format(Constant.COOLAPK_PREURL + Constant.METHOD_GET_APK_FIELD, Constant.API_KEY, id);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = inputStream2String(coolapkHttpGet(url));
                Message msg = new Message();
                if (!TextUtils.isEmpty(result)) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<ApkField>() {
                    }.getType();
                    ApkField field = gson.fromJson(result, type);
                    msg.obj = field;
                    msg.what = 1;
                } else {
                    msg.what = Constant.MSG_OBTAIN_FAILED;
                }

                handler.sendMessage(msg);
            }
        }).start();
    }

    public void obtainSearchApkList(String query, final int page, final Handler handler) {
        if (!checkNetworkState(mContext)) {
            return;
        }
        final String url = String.format(Constant.COOLAPK_PREURL + Constant.METHOD_GET_SEARCH_APKLIST,
                Constant.API_KEY, query, page);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = inputStream2String(coolapkHttpGet(url));
                Message msg = new Message();
                if (!TextUtils.isEmpty(result)) {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<ArrayList<Apk>>() {
                    }.getType();
                    List<Apk> list = gson.fromJson(result, listType);
                    msg.obj = list;
                    if (page == 1) {
                        msg.what = Constant.MSG_OBTAIN_COMPLETE;
                    } else {
                        msg.what = Constant.MSG_OBTAIN_MORE_COMPLETE;
                    }
                } else {
                    msg.what = Constant.MSG_OBTAIN_FAILED;
                }

                handler.sendMessage(msg);
            }
        }).start();
    }

    public void obtainCommentList(int id, final int page, final Handler handler) {
        if (!checkNetworkState(mContext)) {
            return;
        }
        final String url = String.format(Constant.COOLAPK_PREURL + Constant.METHOD_GET_COMMENT_LIST,
                Constant.API_KEY, id, page);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = inputStream2String(coolapkHttpGet(url));
                Message msg = new Message();
                if (!TextUtils.isEmpty(result)) {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<ArrayList<Comment>>() {
                    }.getType();
                    List<Comment> list = gson.fromJson(result, listType);
                    msg.obj = list;
                    if (page == 1) {
                        msg.what = Constant.MSG_OBTAIN_COMPLETE;
                    } else {
                        msg.what = Constant.MSG_OBTAIN_MORE_COMPLETE;
                    }
                } else {
                    msg.what = Constant.MSG_OBTAIN_FAILED;
                }

                handler.sendMessage(msg);
            }
        }).start();
    }

    public void obtainUpgradeVersions(final Handler handler) {
        final String url = String.format(Constant.COOLAPK_PREURL + Constant.METHOD_GET_UPGRADE_VERSIONS,
                Constant.API_KEY);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                PackageManager pm = mContext.getPackageManager();
                List<ApplicationInfo> all = pm.getInstalledApplications(PackageManager.GET_ACTIVITIES);
                StringBuffer sb = new StringBuffer();
                sb.append("sdk=" + Build.VERSION.SDK_INT);
                sb.append("&pkgs=");
                for (ApplicationInfo info : all) {
                    if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                        sb.append(info.packageName + ",");
                    }
                }

                String postStr = sb.substring(0, sb.length() - 1);
                String result = inputStream2String(coolapkHttpPost(url, postStr));
                Log.d(TAG, result);
                if (result != null) {
                    List<UpgradeApk> list = null;
                    try {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ArrayList<UpgradeApk>>() {
                        }.getType();
                        list = gson.fromJson(result, listType);
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                    List<UpgradeApkExtend> updateList = new ArrayList<UpgradeApkExtend>();

                    for (UpgradeApk apk : list) {
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
                } else {
                    msg.what = Constant.MSG_OBTAIN_FAILED;
                }

                handler.sendMessage(msg);
            }
        }).start();
    }

    private InputStream coolapkHttpGet(String urlstr) {
        try {
            URL url = new URL(urlstr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Cookie", "coolapk_did=" + Constant.COOLAPK_DID);
            connection.connect();
            return connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    private InputStream coolapkHttpPost(String urlstr, String param) {
        try {
            URL url = new URL(urlstr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Cookie", "coolapk_did=" + Constant.COOLAPK_DID);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", "" +
                    Integer.toString(param.getBytes().length));

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(param);
            wr.flush();
            wr.close();
            connection.connect();
            return connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

}
