package bjzhou.coolapk.app.ui.fragments

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Html
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast

import com.squareup.picasso.Picasso

import bjzhou.coolapk.app.R
import bjzhou.coolapk.app.model.ApkField
import bjzhou.coolapk.app.net.ApiManager
import bjzhou.coolapk.app.net.ApkDownloader
import bjzhou.coolapk.app.net.DownloadMonitor
import bjzhou.coolapk.app.ui.activities.PhotoViewer
import bjzhou.coolapk.app.util.TimeUtility
import io.reactivex.functions.Consumer

/**
 * Created by bjzhou on 14-7-29.
 */
class AppViewFragment : Fragment(), View.OnClickListener {
    private var mId: Int = 0

    private var mField: ApkField? = null

    // 0:not install, 1: installed, 2: should update, -1: downloadAndInstall failed, -2: install failed
    private var mInstallStatus = 0

    private val mScreenshotId = intArrayOf(R.id.app_view_screenshot0, R.id.app_view_screenshot1, R.id.app_view_screenshot2, R.id.app_view_screenshot3, R.id.app_view_screenshot4, R.id.app_view_screenshot5)

    private val mScreenshotView = arrayOfNulls<ImageView>(6)

    private var screenshots: Array<String> = emptyArray()
    private lateinit var mAppIconView: ImageView
    private lateinit var mAppTitleView: TextView
    private lateinit var mRatingBar: RatingBar
    private lateinit var mInfoView: TextView
    private lateinit var mDownloadButton: ImageButton
    private lateinit var mLanguageView: TextView
    private lateinit var mApkSizeView: TextView
    private lateinit var mRomView: TextView
    private lateinit var mUpdateView: TextView
    private lateinit var mRemarkView: TextView
    private lateinit var mIntroduceView: TextView
    private var downloadListener: DownloadMonitor.DownloadListener? = null
    private lateinit var mCommentButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments
        mId = args.getInt("id")

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_app_view, container, false)
        val headView = rootView.findViewById(R.id.app_header)

        mAppIconView = headView.findViewById(R.id.app_header_icon) as ImageView
        mAppTitleView = headView.findViewById(R.id.app_header_title) as TextView
        mRatingBar = headView.findViewById(R.id.app_header_ratingStar) as RatingBar
        mInfoView = headView.findViewById(R.id.app_header_info) as TextView
        mDownloadButton = headView.findViewById(R.id.app_header_download) as ImageButton
        mCommentButton = headView.findViewById(R.id.app_header_comment) as ImageButton

        for (i in 0..5) {
            mScreenshotView[i] = rootView.findViewById(mScreenshotId[i]) as ImageView
        }

        mLanguageView = rootView.findViewById(R.id.app_view_meta1) as TextView
        mApkSizeView = rootView.findViewById(R.id.app_view_meta2) as TextView
        mRomView = rootView.findViewById(R.id.app_view_meta3) as TextView
        mUpdateView = rootView.findViewById(R.id.app_view_meta4) as TextView
        mRemarkView = rootView.findViewById(R.id.app_view_remark) as TextView
        mIntroduceView = rootView.findViewById(R.id.app_view_introduce) as TextView

        mDownloadButton.setOnClickListener(this)
        downloadListener = object : DownloadMonitor.DownloadListener {
            override fun onDownloading(percent: Int) {
                //mDownloadButton.setText(percent + "%");
            }

            override fun onFailure(errCode: Int, vararg err: String) {
                when (errCode) {
                    DownloadMonitor.DownloadListener.DOWNLOAD_FAIL -> mInstallStatus = -1
                }//mDownloadButton.setText("下载失败,点击重试");
            }

            override fun onDownloaded() {
                if (activity == null) return
                Toast.makeText(activity, "正在安装", Toast.LENGTH_SHORT).show()
            }

            override fun onComplete() {
                mInstallStatus = 1
                //mDownloadButton.setText("安装成功,点击打开");
            }
        }
        mCommentButton.setOnClickListener(this)

        ApiManager.instance.obtainApkField(mId).subscribe { apkField -> onObtainApkField(apkField) }
        return rootView
    }

    override fun onClick(v: View) {

        if (v === mDownloadButton) {

            if (mInstallStatus == 1) {
                val intent = activity.packageManager.getLaunchIntentForPackage(mField?.meta?.apkname)
                startActivity(intent)
                return
            }

            //mDownloadButton.setText("正在准备下载");
            ApkDownloader.instance.downloadAndInstall(activity, mField?.meta, downloadListener)

        } else {

            activity.supportFragmentManager.beginTransaction()
                    .add(R.id.container, CommentFragment.newInstance(mId), "Comment")
                    .addToBackStack("Comment")
                    .commit()
        }
    }

    private fun getInstalledVersion(packageName: String?): Int {
        val pm = activity.packageManager
        try {
            val pi = pm.getPackageInfo(packageName, 0)
            return pi.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            return -1
        }

    }

    private fun onObtainApkField(apkField: ApkField?) {
        if (activity == null) return
        if (apkField == null) return
        mField = apkField
        Picasso.with(activity)
                .load(mField?.meta?.logo)
                .placeholder(R.drawable.ic_default_avatar)
                .into(mAppIconView)
        mAppTitleView.text = mField?.meta?.title
        mRatingBar.rating = mField?.meta?.score ?: 0.toFloat()
        mInfoView.text = mField?.meta?.apkversionname
        if (ApkDownloader.instance.isDownloading(mId)) {
            ApkDownloader.instance.setListener(mId, downloadListener)
        } else {
            val installedVersion = getInstalledVersion(mField?.meta?.apkname)
            if (installedVersion == -1) {
                mInstallStatus = 0
                //mDownloadButton.setText("下载(" + mField.getMeta().getApksize() + ")");
            } else {
                val version = mField?.meta?.apkversioncode ?: 0
                if (version > installedVersion) {
                    mInstallStatus = 2
                    //mDownloadButton.setText("升级(" + mField.getMeta().getApksize() + ")");
                } else {
                    mInstallStatus = 1
                    //mDownloadButton.setText("已安装");
                    mDownloadButton.setImageResource(R.drawable.ic_stat_ok)
                }
            }
        }

        screenshots = mField?.field?.screenshots?.split(",".toRegex())?.dropLastWhile(String::isEmpty)?.toTypedArray() ?: emptyArray()
        for (i in screenshots.indices) {
            if (i >= mScreenshotView.size) return
            mScreenshotView[i]?.visibility = View.VISIBLE
            Picasso.with(activity)
                    .load(screenshots[i])
                    .fit().centerCrop()
                    .placeholder(R.drawable.screenshot_small)
                    .into(mScreenshotView[i])
            mScreenshotView[i]?.tag = i
            mScreenshotView[i]?.setOnClickListener { v ->
                val position = v.tag as Int
                val intent = Intent(activity, PhotoViewer::class.java)
                intent.putExtra("screenshots", screenshots)
                intent.putExtra("index", position)
                startActivity(intent)
            }
        }

        mLanguageView.text = "界面语言 : " + mField?.field?.language
        mApkSizeView.text = "应用大小 : " + mField?.meta?.apksize
        mRomView.text = "支持ROM : " + mField?.meta?.romversion + "及更高版本"
        mUpdateView.text = "更新日期 : " + TimeUtility.getTime(mField?.meta?.lastupdate ?: 0)
        mRemarkView.text = "酷安点评 : " + mField?.field?.remark
        var introduce = mField?.field?.introduce + "<br/>"
        if (!TextUtils.isEmpty(mField?.field?.changelog)) {
            introduce += "<br/><strong>" + mField?.meta?.apkversionname + " :</strong><br/>" +
                    mField?.field?.changelog + "<br/>"
        }
        if (!TextUtils.isEmpty(mField?.field?.changelog)) {
            introduce += "<br/><strong>更新记录 :</strong><br/>" + mField?.field?.changehistory
        }
        introduce = introduce.replace("\n", "<br/>")
        mIntroduceView.text = Html.fromHtml(introduce)
        mIntroduceView.movementMethod = LinkMovementMethod.getInstance()
    }

    companion object {

        private val TAG = "AppViewFragment"

        fun newInstance(id: Int): AppViewFragment {
            val fragment = AppViewFragment()
            val args = Bundle()
            args.putInt("id", id)
            fragment.arguments = args
            return fragment
        }
    }
}
