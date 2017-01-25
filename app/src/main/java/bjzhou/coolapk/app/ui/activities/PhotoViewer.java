package bjzhou.coolapk.app.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import bjzhou.coolapk.app.R;
import bjzhou.coolapk.app.model.CardEntity;
import bjzhou.coolapk.app.net.ApiManager;
import io.reactivex.functions.Consumer;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by bjzhou on 14-8-4.
 */
public class PhotoViewer extends Activity {
    private static final String TAG = PhotoViewer.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoviewer);
        final ViewPager pager = (ViewPager) findViewById(R.id.photo_viewer);

        final Intent intent = getIntent();
        boolean startPage = intent.getBooleanExtra("startPage", false);
        if (startPage) {
            ApiManager.getInstance().init().subscribe(new Consumer<List<CardEntity>>() {
                @Override
                public void accept(List<CardEntity> entities) throws Exception {
                    for (CardEntity entity : entities) {
                        Log.d(TAG, "accept: " + entity);
                        if ("imageCard".equals(entity.getEntityType())) {
                            String[] screenshots = new String[] { entity.getPic() };
                            pager.setAdapter(new PhotoPagerAdapter(screenshots));
                            pager.setCurrentItem(0);
                        }
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    Toast.makeText(PhotoViewer.this, throwable.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            String[] screenshots = intent.getStringArrayExtra("screenshots");
            int index = intent.getIntExtra("index", 0);
            pager.setAdapter(new PhotoPagerAdapter(screenshots));
            pager.setCurrentItem(index);
        }

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
            return view == object;
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
            container.removeView((PhotoView) object);
        }
    }
}
