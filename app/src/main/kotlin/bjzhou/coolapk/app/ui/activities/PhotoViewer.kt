package bjzhou.coolapk.app.ui.activities

import android.app.Activity
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import bjzhou.coolapk.app.R
import bjzhou.coolapk.app.net.ApiManager
import com.squareup.picasso.Picasso
import uk.co.senab.photoview.PhotoView

/**
 * Created by bjzhou on 14-8-4.
 */
class PhotoViewer : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photoviewer)
        val pager: ViewPager = findViewById(R.id.photo_viewer)

        val intent = intent
        val startPage = intent.getBooleanExtra("startPage", false)
        if (startPage) {
            ApiManager.instance.init().subscribe({ entities ->
                for (entity in entities) {
                    Log.d(TAG, "accept: " + entity)
                    if ("imageCard" == entity.entityType) {
                        val screenshots = arrayOf(entity.pic)
                        pager.adapter = PhotoPagerAdapter(screenshots)
                        pager.currentItem = 0
                    }
                }
            }) { throwable -> Toast.makeText(this@PhotoViewer, throwable.toString(), Toast.LENGTH_SHORT).show() }
        } else {
            val screenshots = intent.getStringArrayExtra("screenshots")
            val index = intent.getIntExtra("index", 0)
            pager.adapter = PhotoPagerAdapter(screenshots)
            pager.currentItem = index
        }

    }

    private inner class PhotoPagerAdapter(private val mPhotos: Array<String>) : PagerAdapter() {

        override fun getCount(): Int {
            return mPhotos.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val photo = PhotoView(this@PhotoViewer)
            Picasso.with(this@PhotoViewer)
                    .load(mPhotos[position])
                    .placeholder(R.drawable.screenshot)
                    .into(photo)
            container.addView(photo)
            return photo
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as PhotoView)
        }
    }

    companion object {
        private val TAG = PhotoViewer::class.java.simpleName
    }
}
