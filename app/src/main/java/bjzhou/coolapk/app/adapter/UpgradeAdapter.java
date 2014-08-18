package bjzhou.coolapk.app.adapter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import bjzhou.coolapk.app.R;
import bjzhou.coolapk.app.http.HttpHelper;
import bjzhou.coolapk.app.model.UpgradeApkExtend;
import bjzhou.coolapk.app.ui.UpgradeFragment;
import eu.chainfire.libsuperuser.Shell;

import java.io.File;
import java.util.List;

/**
 * Created by bjzhou on 14-8-13.
 */
public class UpgradeAdapter extends BaseAdapter{

    private static final String TAG = "UpgradeAdapter";
    private final FragmentActivity mActivity;
    private List<UpgradeApkExtend> mUpgradeList;

    public UpgradeAdapter(FragmentActivity activity) {
        mActivity = activity;
    }

    @Override
    public int getCount() {
        return mUpgradeList.size();
    }

    @Override
    public Object getItem(int position) {
        return mUpgradeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mUpgradeList.get(position).getApk().getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mActivity.getLayoutInflater().inflate(R.layout.list_item_upgrade_app, null);
            holder.logoView = (ImageView) convertView.findViewById(R.id.list_item_icon);
            holder.titleView = (TextView) convertView.findViewById(R.id.list_item_title);
            holder.infoView = (TextView) convertView.findViewById(R.id.list_item_info);
            holder.changelogView = (TextView) convertView.findViewById(R.id.list_item_description);
            holder.upgradeButton = (Button) convertView.findViewById(R.id.list_item_upgrade);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.titleView.setText(mUpgradeList.get(position).getTitle());
        holder.infoView.setText(mUpgradeList.get(position).getInfo());
        holder.logoView.setImageDrawable(mUpgradeList.get(position).getLogo());
        String changelog = mUpgradeList.get(position).getApk().getChangelog();
        if (!TextUtils.isEmpty(changelog)) {
            holder.changelogView.setText(changelog);
            holder.changelogView.setVisibility(View.VISIBLE);
        }
        holder.upgradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadAndInstall(holder.upgradeButton, mUpgradeList.get(position));
            }
        });

        return convertView;
    }

    private void downloadAndInstall(final Button button, final UpgradeApkExtend apkExtend) {
        button.setText("正在准备下载");
        int id = (int) apkExtend.getApk().getId();
        String downloadName = apkExtend.getApk().getApkname() + "_" + apkExtend.getApk().getApkversionname() + ".apk";
        HttpHelper.getInstance(mActivity).downloadAndInstall(id, downloadName, new HttpHelper.DownloadListener() {
            @Override
            public void onDownloading(int percent) {
                if (percent == 100) {
                    button.setText("正在安装");
                } else {
                    button.setText(percent + "%");
                }
            }

            @Override
            public void onFailure(int errCode, String... err) {
                switch (errCode) {
                    case DOWNLOAD_FAIL:
                        button.setText("下载失败,点击重试");
                        break;
                    case INSTALL_FAIL:
                        Toast.makeText(mActivity, err[0], Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(err[1])), "application/vnd.android.package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mActivity.startActivityForResult(intent, UpgradeFragment.INSTALL_REQUEST_CODE);
                        break;
                }
            }

            @Override
            public void onDownloaded() {
                button.setText("正在安装");
            }

            @Override
            public void onComplete() {
                button.setText("安装成功");
                NotificationManager nm = (NotificationManager) mActivity.getSystemService(Context.NOTIFICATION_SERVICE);
                PendingIntent pi = PendingIntent.getActivity(mActivity, 0, new Intent(), 0);
                Notification.Builder builder = new Notification.Builder(mActivity);
                //builder.setContentTitle(title + "升级成功");
                builder.setContentText(apkExtend.getTitle() + "升级成功");
                builder.setContentIntent(pi);
                builder.setSmallIcon(R.drawable.ic_stat_ok);
                Notification notification = builder.getNotification();
                nm.notify(R.drawable.ic_stat_ok, notification);
            }
        });
    }

    public void setUpgradeList(List<UpgradeApkExtend> upgradeList) {
        mUpgradeList = upgradeList;
    }

    static class ViewHolder {
        ImageView logoView;
        TextView titleView;
        TextView infoView;
        TextView changelogView;
        Button upgradeButton;
    }
}
