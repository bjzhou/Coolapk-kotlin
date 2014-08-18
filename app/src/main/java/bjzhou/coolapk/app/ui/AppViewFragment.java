package bjzhou.coolapk.app.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.*;
import android.widget.*;
import bjzhou.coolapk.app.R;
import bjzhou.coolapk.app.custom.PullToRefreshFragment;
import bjzhou.coolapk.app.http.ApkDownloader;
import bjzhou.coolapk.app.http.HttpHelper;
import bjzhou.coolapk.app.model.ApkField;
import bjzhou.coolapk.app.util.TimeUtility;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by bjzhou on 14-7-29.
 */
public class AppViewFragment extends PullToRefreshFragment implements View.OnClickListener {

    private static final String TAG = "AppViewFragment";
    private int mId;

    private ApkField mField;

    // 0:not install, 1: installed, 2: should update, -1: download failed, -2: install failed
    private int mInstallStatus = 0;

    private int[] mScreenshotId = new int[]{
            R.id.app_view_screenshot0,
            R.id.app_view_screenshot1,
            R.id.app_view_screenshot2,
            R.id.app_view_screenshot3,
            R.id.app_view_screenshot4,
            R.id.app_view_screenshot5
    };

    private ImageView[] mScreenshotView = new ImageView[6];

    private String[] screenshots;
    private ImageView mAppIconView;
    private TextView mAppTitleView;
    private RatingBar mRatingBar;
    private TextView mInfoView;
    private Button mDownloadButton;
    private TextView mLanguageView;
    private TextView mApkSizeView;
    private TextView mRomView;
    private TextView mUpdateView;
    private TextView mRemarkView;
    private TextView mIntroduceView;
    private boolean showMenu = true;
    private ApkDownloader.DownloadListener downloadListener;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mField = (ApkField) msg.obj;
            Picasso.with(getActivity())
                    .load(mField.getMeta().getLogo())
                    .placeholder(R.drawable.ic_default_avatar)
                    .into(mAppIconView);
            mAppTitleView.setText(mField.getMeta().getTitle());
            mRatingBar.setRating(mField.getMeta().getScore());
            mInfoView.setText(mField.getMeta().getApkversionname());
            if (ApkDownloader.getInstance(getActivity()).isDownloading(mId)) {
                ApkDownloader.getInstance(getActivity()).setListener(mId, downloadListener);
            } else {
                int installedVersion = getInstalledVersion(mField.getMeta().getApkname());
                if (installedVersion == -1) {
                    mInstallStatus = 0;
                    mDownloadButton.setText("下载(" + mField.getMeta().getApksize() + ")");
                } else {
                    int version = mField.getMeta().getApkversioncode();
                    if (version > installedVersion) {
                        mInstallStatus = 2;
                        mDownloadButton.setText("升级(" + mField.getMeta().getApksize() + ")");
                    } else {
                        mInstallStatus = 1;
                        mDownloadButton.setText("已安装");
                    }
                }
            }

            screenshots = mField.getField().getScreenshots().split(",");
            for (int i = 0; i < screenshots.length; i++) {
                mScreenshotView[i].setVisibility(View.VISIBLE);
                Picasso.with(getActivity())
                        .load(screenshots[i])
                        .fit().centerCrop()
                        .placeholder(R.drawable.screenshot_small)
                        .into(mScreenshotView[i]);
                mScreenshotView[i].setTag(i);
                mScreenshotView[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (screenshots == null) {
                            return;
                        }

                        int position = (Integer) v.getTag();
                        Intent intent = new Intent(getActivity(), PhotoViewer.class);
                        intent.putExtra("screenshots", screenshots);
                        intent.putExtra("index", position);
                        startActivity(intent);
                    }
                });
            }

            mLanguageView.setText("界面语言 : " + mField.getField().getLanguage());
            mApkSizeView.setText("应用大小 : " + mField.getMeta().getApksize());
            mRomView.setText("支持ROM : " + mField.getMeta().getRomversion() + "及更高版本");
            mUpdateView.setText("更新日期 : " + TimeUtility.getTime(mField.getMeta().getLastupdate()));
            mRemarkView.setText("酷安点评 : " + mField.getField().getRemark());
            String introduce = mField.getField().getIntroduce() + "<br/>";
            if (!TextUtils.isEmpty(mField.getField().getChangelog())) {
                introduce += "<br/><strong>" + mField.getMeta().getApkversionname() + " :</strong><br/>"
                        + mField.getField().getChangelog() + "<br/>";
            }
            if (!TextUtils.isEmpty(mField.getField().getChangelog())) {
                introduce += "<br/><strong>更新记录 :</strong><br/>"
                        + mField.getField().getChangehistory();
            }
            introduce = introduce.replace("\n", "<br/>");
            mIntroduceView.setText(Html.fromHtml(introduce));
            mIntroduceView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    };

    public AppViewFragment() {
    }

    public static AppViewFragment newInstance(int id) {
        AppViewFragment fragment = new AppViewFragment();
        Bundle args = new Bundle();
        args.putInt("id", id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mId = args.getInt("id");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_app_view, container, false);
        View headView = rootView.findViewById(R.id.app_header);

        mAppIconView = (ImageView) headView.findViewById(R.id.app_header_icon);
        mAppTitleView = (TextView) headView.findViewById(R.id.app_header_title);
        mRatingBar = (RatingBar) headView.findViewById(R.id.app_header_ratingStar);
        mInfoView = (TextView) headView.findViewById(R.id.app_header_info);
        mDownloadButton = (Button) headView.findViewById(R.id.app_header_button);

        for (int i = 0; i < 6; i++) {
            mScreenshotView[i] = (ImageView) rootView.findViewById(mScreenshotId[i]);
        }

        mLanguageView = (TextView) rootView.findViewById(R.id.app_view_meta1);
        mApkSizeView = (TextView) rootView.findViewById(R.id.app_view_meta2);
        mRomView = (TextView) rootView.findViewById(R.id.app_view_meta3);
        mUpdateView = (TextView) rootView.findViewById(R.id.app_view_meta4);
        mRemarkView = (TextView) rootView.findViewById(R.id.app_view_remark);
        mIntroduceView = (TextView) rootView.findViewById(R.id.app_view_introduce);

        mDownloadButton.setOnClickListener(this);
        downloadListener = new ApkDownloader.DownloadListener() {
            @Override
            public void onDownloading(int percent) {
                mDownloadButton.setText(percent + "%");
            }

            @Override
            public void onFailure(int errCode, String... err) {
                switch (errCode) {
                    case DOWNLOAD_FAIL:
                        mInstallStatus = -1;
                        mDownloadButton.setText("下载失败,点击重试");
                        break;
                    case INSTALL_FAIL:
                        Toast.makeText(getActivity(), err[0], Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(err[1])), "application/vnd.android.package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;
                }
            }

            @Override
            public void onDownloaded() {
                mDownloadButton.setText("正在安装");
            }

            @Override
            public void onComplete() {
                mInstallStatus = 1;
                mDownloadButton.setText("安装成功,点击打开");
            }
        };

        HttpHelper.getInstance(getActivity()).obtainApkField(mId, mHandler);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle("应用详情");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (showMenu) {
            inflater.inflate(R.menu.comment, menu);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        showMenu = true;
        getActivity().supportInvalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_comment:
                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, CommentFragment.newInstance(mId), "Comment")
                        .addToBackStack("Comment")
                        .commit();
                showMenu = false;
                getActivity().supportInvalidateOptionsMenu();
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {

        if (mInstallStatus == 1) {
            Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(mField.getMeta().getApkname());
            startActivity(intent);
            return;
        }

        mDownloadButton.setText("正在准备下载");
        ApkDownloader.getInstance(getActivity()).download(mId, mField.getMeta().getApkname(),
                mField.getMeta().getTitle(), mField.getMeta().getApkversionname(), downloadListener);
    }

    private int getInstalledVersion(String packageName) {
        PackageManager pm = getActivity().getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }

    @Override
    protected void onActionBarClick() {
    }

    @Override
    public void onRefreshStarted(View view) {
    }
}
