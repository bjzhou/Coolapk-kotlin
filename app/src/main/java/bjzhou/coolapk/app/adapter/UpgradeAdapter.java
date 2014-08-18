package bjzhou.coolapk.app.adapter;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import bjzhou.coolapk.app.R;
import bjzhou.coolapk.app.http.ApkDownloader;
import bjzhou.coolapk.app.model.UpgradeApkExtend;
import bjzhou.coolapk.app.ui.UpgradeFragment;

import java.io.File;
import java.util.List;

/**
 * Created by bjzhou on 14-8-13.
 */
public class UpgradeAdapter extends BaseAdapter {

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

        ApkDownloader.DownloadListener downloadListener = new ApkDownloader.DownloadListener() {
            @Override
            public void onDownloading(int percent) {
                holder.upgradeButton.setText(percent + "%");
            }

            @Override
            public void onFailure(int errCode, String... err) {
                switch (errCode) {
                    case DOWNLOAD_FAIL:
                        holder.upgradeButton.setText("下载失败,点击重试");
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
                holder.upgradeButton.setText("正在安装");
            }

            @Override
            public void onComplete() {
                holder.upgradeButton.setText("安装成功");
            }
        };
        int id = (int) mUpgradeList.get(position).getApk().getId();
        if (ApkDownloader.getInstance(mActivity).isDownloading(id)) {
            ApkDownloader.getInstance(mActivity).setListener(id, downloadListener);
        } else {
            holder.upgradeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadAndInstall(holder.upgradeButton, mUpgradeList.get(position));
                }
            });
        }

        return convertView;
    }

    private void downloadAndInstall(final Button button, final UpgradeApkExtend apkExtend) {
        button.setText("正在准备下载");
        int id = (int) apkExtend.getApk().getId();
        ApkDownloader.getInstance(mActivity).download(id, apkExtend.getApk().getApkname(), apkExtend.getTitle(),
                apkExtend.getApk().getApkversionname(), new ApkDownloader.DownloadListener() {
                    @Override
                    public void onDownloading(int percent) {
                        button.setText(percent + "%");
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
