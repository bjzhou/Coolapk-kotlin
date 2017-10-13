package bjzhou.coolapk.app.ui.fragments

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
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
import bjzhou.coolapk.app.R
import bjzhou.coolapk.app.model.ApkField
import bjzhou.coolapk.app.ui.activities.PhotoViewer
import bjzhou.coolapk.app.util.TimeUtility
import bjzhou.coolapk.app.viewmodel.AppViewModel
import com.squareup.picasso.Picasso

/**
 * Created by bjzhou on 14-7-29.
 */
class AppViewFragment : LifecycleFragment(), View.OnClickListener {

    private lateinit var mViewModel: AppViewModel

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

        mViewModel = ViewModelProviders.of(this)[AppViewModel::class.java]
        mViewModel.mId = arguments.getInt("id")
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

        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        mViewModel.obtainApkField()
        mViewModel.mApkField.observe(this, Observer { apkField ->
            onObtainApkField(apkField)
        })
        mViewModel.mClickStatus.observe(this, Observer { clickStatus ->
            when (clickStatus) {
                AppViewModel.CLICK_OPEN -> mDownloadButton.setImageResource(R.drawable.ic_stat_ok)
                else -> mDownloadButton.setImageResource(R.drawable.ic_action_download)
            }
        })
    }

    override fun onClick(v: View) {
        if (v === mDownloadButton) {
            mViewModel.download(activity)
        } else {
            activity.supportFragmentManager.beginTransaction()
                    .add(R.id.container, CommentFragment.newInstance(mViewModel.mId), "Comment")
                    .addToBackStack("Comment")
                    .commit()
        }
    }

    private fun onObtainApkField(apkField: ApkField?) {
        if (apkField == null) return
        Picasso.with(activity)
                .load(apkField.meta?.logo)
                .placeholder(R.drawable.ic_default_avatar)
                .into(mAppIconView)
        mAppTitleView.text = apkField.meta?.title
        mRatingBar.rating = apkField.meta?.score ?: 0.toFloat()
        mInfoView.text = apkField.meta?.apkversionname

        screenshots = apkField.field?.screenshots?.split(",".toRegex())?.dropLastWhile(String::isEmpty)?.toTypedArray() ?: emptyArray()
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

        mLanguageView.text = "界面语言 : " + apkField.field?.language
        mApkSizeView.text = "应用大小 : " + apkField.meta?.apksize
        mRomView.text = "支持ROM : " + apkField.meta?.romversion + "及更高版本"
        mUpdateView.text = "更新日期 : " + TimeUtility.getTime(apkField.meta?.lastupdate ?: 0)
        mRemarkView.text = "酷安点评 : " + apkField.field?.remark
        var introduce = apkField.field?.introduce + "<br/>"
        if (!TextUtils.isEmpty(apkField.field?.changelog)) {
            introduce += "<br/><strong>" + apkField.meta?.apkversionname + " :</strong><br/>" +
                    apkField.field?.changelog + "<br/>"
        }
        if (!TextUtils.isEmpty(apkField.field?.changelog)) {
            introduce += "<br/><strong>更新记录 :</strong><br/>" + apkField.field?.changehistory
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
