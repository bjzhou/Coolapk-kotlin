package bjzhou.coolapk.app.ui.fragments

import android.app.DownloadManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Html
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import bjzhou.coolapk.app.App
import bjzhou.coolapk.app.R
import bjzhou.coolapk.app.model.ApkField
import bjzhou.coolapk.app.net.ApiManager
import bjzhou.coolapk.app.net.ApkDownloader
import bjzhou.coolapk.app.net.DownloadStatus
import bjzhou.coolapk.app.ui.activities.PhotoViewer
import bjzhou.coolapk.app.util.Settings
import bjzhou.coolapk.app.util.TimeUtility
import bjzhou.coolapk.app.util.Utils
import com.squareup.picasso.Picasso
import java.io.File

/**
 * Created by bjzhou on 14-7-29.
 */
class AppViewFragment : Fragment(), View.OnClickListener {
    private var mId: Int = 0

    private var mField: ApkField? = null
    private var mClickStatus = CLICK_DOWNLOAD

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
    private lateinit var mCommentButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments
        mId = args.getInt("id")

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_app_view, container, false)
        val headView: View = rootView.findViewById(R.id.app_header)

        mAppIconView = headView.findViewById(R.id.app_header_icon)
        mAppTitleView = headView.findViewById(R.id.app_header_title)
        mRatingBar = headView.findViewById(R.id.app_header_ratingStar)
        mInfoView = headView.findViewById(R.id.app_header_info)
        mDownloadButton = headView.findViewById(R.id.app_header_download)
        mCommentButton = headView.findViewById(R.id.app_header_comment)

        for (i in 0..5) {
            mScreenshotView[i] = rootView.findViewById(mScreenshotId[i])
        }

        mLanguageView = rootView.findViewById(R.id.app_view_meta1)
        mApkSizeView = rootView.findViewById(R.id.app_view_meta2)
        mRomView = rootView.findViewById(R.id.app_view_meta3)
        mUpdateView = rootView.findViewById(R.id.app_view_meta4)
        mRemarkView = rootView.findViewById(R.id.app_view_remark)
        mIntroduceView = rootView.findViewById(R.id.app_view_introduce)

        mDownloadButton.setOnClickListener(this)
        mCommentButton.setOnClickListener(this)

        ApiManager.instance.obtainApkField(mId).subscribe { apkField -> onObtainApkField(apkField) }
        return rootView
    }

    override fun onClick(v: View) {

        if (v === mDownloadButton) {
            if (mClickStatus == CLICK_OPEN) {
                val intent = activity.packageManager.getLaunchIntentForPackage(mField?.meta?.apkname)
                startActivity(intent)
            }
            if (mClickStatus == CLICK_INSTALL) {
                val file = File(Settings.instance.downloadDir, mField?.meta?.filename)
                val pkgInfo = App.context.packageManager.getPackageArchiveInfo(file.absolutePath, 0)
                if (file.exists() && pkgInfo != null) {
                    Utils.installApk(Uri.fromFile(file))
                } else {
                    downloadApk()
                }
            }
            if (mClickStatus == CLICK_DOWNLOAD) {
                downloadApk()
            }
        } else {
            activity.supportFragmentManager.beginTransaction()
                    .add(R.id.container, CommentFragment.newInstance(mId), "Comment")
                    .addToBackStack("Comment")
                    .commit()
        }
    }

    private fun downloadApk() {
        mField?.meta?.let {
            val apk = it
            ApkDownloader.instance.download(apk)
            ApkDownloader.instance.observeDownloadStatus(apk)
                    .subscribe {
                        when (it.status) {
                            DownloadManager.STATUS_FAILED -> {
                                Toast.makeText(App.context, "下载失败,点击重试", Toast.LENGTH_SHORT).show()
                                mClickStatus = CLICK_DOWNLOAD
                            }
                            DownloadManager.STATUS_SUCCESSFUL -> {
                                mClickStatus = CLICK_INSTALL
                            }
                            else -> {
                                mClickStatus = CLICK_DO_NOTHING
                            }
                        }
                    }
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
        val installedVersion = getInstalledVersion(mField?.meta?.apkname)
        if (installedVersion == -1) {
            mClickStatus = CLICK_DOWNLOAD
        } else {
            val version = mField?.meta?.apkversioncode ?: 0
            if (version > installedVersion) {
                mClickStatus = CLICK_DOWNLOAD
            } else {
                mClickStatus = CLICK_OPEN
                mDownloadButton.setImageResource(R.drawable.ic_stat_ok)
            }
        }
        if (mClickStatus == CLICK_DOWNLOAD) {
            val status = ApkDownloader.instance.getDownloadStatus(mField?.meta)
            if (status.status == DownloadStatus.STATUS_NOT_STARTED) {
                mClickStatus = CLICK_DOWNLOAD
            } else if (status.status == DownloadManager.STATUS_SUCCESSFUL) {
                mClickStatus = CLICK_INSTALL
            } else if (status.status != DownloadManager.STATUS_FAILED) {
                mClickStatus = CLICK_DO_NOTHING
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
        private val CLICK_DOWNLOAD = 0
        private val CLICK_INSTALL = 1
        private val CLICK_OPEN = 2
        private val CLICK_DO_NOTHING = 3

        fun newInstance(id: Int): AppViewFragment {
            val fragment = AppViewFragment()
            val args = Bundle()
            args.putInt("id", id)
            fragment.arguments = args
            return fragment
        }
    }
}
