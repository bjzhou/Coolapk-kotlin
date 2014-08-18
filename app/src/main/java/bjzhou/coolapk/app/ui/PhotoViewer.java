package bjzhou.coolapk.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import bjzhou.coolapk.app.R;
import com.squareup.picasso.Picasso;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by bjzhou on 14-8-4.
 */
public class PhotoViewer extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoviewer);

        Intent intent = getIntent();
        String[] screenshots = intent.getStringArrayExtra("screenshots");
        int index = intent.getIntExtra("index", 0);

        ViewPager pager = (ViewPager) findViewById(R.id.photo_viewer);
        pager.setAdapter(new PhotoPagerAdapter(screenshots));

        pager.setCurrentItem(index);

    }

    private class PhotoPagerAdapter extends PagerAdapter {
        private String[] mPhotos;

        public PhotoPagerAdapter(String[] screenshots) {
            mPhotos = screenshots;
        }

        @Override
        public int getCount() {
            return mPhotos.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((PhotoView) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoView photo = new PhotoView(PhotoViewer.this);
            Picasso.with(PhotoViewer.this)
                    .load(mPhotos[position])
                    .placeholder(R.drawable.screenshot)
                    .into(photo);
            container.addView(photo);
            return photo;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((PhotoView) object);
        }
    }
}
