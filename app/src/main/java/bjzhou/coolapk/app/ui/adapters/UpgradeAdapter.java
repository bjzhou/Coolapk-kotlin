package bjzhou.coolapk.app.ui.adapters;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import bjzhou.coolapk.app.R;
import bjzhou.coolapk.app.model.UpgradeApkExtend;
import bjzhou.coolapk.app.net.ApkDownloader;
import bjzhou.coolapk.app.net.DownloadMonitor;

/**
 * Created by bjzhou on 14-8-13.
 */
public class UpgradeAdapter extends RecyclerView.Adapter {

    private static final String TAG = "UpgradeAdapter";
    private final FragmentActivity mActivity;
    private List<UpgradeApkExtend> mUpgradeList;

    public UpgradeAdapter(FragmentActivity activity) {
        mActivity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View convertView = mActivity.getLayoutInflater().inflate(R.layout.list_item_upgrade_app, viewGroup, false);
        return  new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        holder.titleView.setText(mUpgradeList.get(position).getTitle());
        holder.infoView.setText(mUpgradeList.get(position).getInfo());
        holder.logoView.setImageDrawable(mUpgradeList.get(position).getLogo());
        String changelog = mUpgradeList.get(position).getApk().getChangelog();
        if (!TextUtils.isEmpty(changelog)) {
            holder.changelogView.setText(changelog);
            holder.changelogView.setVisibility(View.VISIBLE);
        }

        DownloadMonitor.DownloadListener downloadListener = new DownloadMonitor.DownloadListener() {
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
        int id = mUpgradeList.get(position).getApk().getId();
        if (ApkDownloader.getInstance().isDownloading(id)) {
            ApkDownloader.getInstance().addListener(id, downloadListener);
        } else {
            holder.upgradeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadAndInstall(holder.upgradeButton, mUpgradeList.get(holder.getAdapterPosition()));
                }
            });
        }
    }

    @Override
    public long getItemId(int position) {
        return mUpgradeList.get(position).getApk().getId();
    }

    @Override
    public int getItemCount() {
        return mUpgradeList.size();
    }

    private void downloadAndInstall(final Button button, final UpgradeApkExtend apkExtend) {
        button.setText("正在准备下载");
        apkExtend.getApk().setTitle(apkExtend.getTitle());
        ApkDownloader.getInstance().downloadAndInstall(mActivity, apkExtend.getApk(), new DownloadMonitor.DownloadListener() {
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

    private static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView logoView;
        TextView titleView;
        TextView infoView;
        TextView changelogView;
        Button upgradeButton;

        public ViewHolder(View itemView) {
            super(itemView);
            logoView = (ImageView) itemView.findViewById(R.id.list_item_icon);
            titleView = (TextView) itemView.findViewById(R.id.list_item_title);
            infoView = (TextView) itemView.findViewById(R.id.list_item_info);
            changelogView = (TextView) itemView.findViewById(R.id.list_item_description);
            upgradeButton = (Button) itemView.findViewById(R.id.list_item_upgrade);
        }
    }
}
