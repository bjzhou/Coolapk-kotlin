package bjzhou.coolapk.app.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import bjzhou.coolapk.app.R;
import bjzhou.coolapk.app.model.Apk;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bjzhou on 14-7-31.
 */
public class ApkListAdapter extends BaseAdapter {

    private static final String TAG = "ApkListAdapter";
    private Activity mActivity;
    private List<Apk> mApkList = new ArrayList<Apk>();

    public ApkListAdapter(FragmentActivity activity) {
        mActivity = activity;
    }

    public void setApkList(List<Apk> apkList) {
        mApkList = apkList;
    }

    @Override
    public int getCount() {
        return mApkList.size();
    }

    @Override
    public Object getItem(int position) {
        return mApkList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mApkList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mActivity.getLayoutInflater().inflate(R.layout.list_item_app, null);
            holder.logoView = (ImageView) convertView.findViewById(R.id.list_item_icon);
            holder.titleView = (TextView) convertView.findViewById(R.id.list_item_title);
            holder.infoView = (TextView) convertView.findViewById(R.id.list_item_info);
            holder.downnumView = (TextView) convertView.findViewById(R.id.list_item_downnum);
            holder.ratingBar = (RatingBar) convertView.findViewById(R.id.list_item_ratingStar);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.titleView.setText(mApkList.get(position).getTitle());
        holder.infoView.setText(mApkList.get(position).getInfo());
        holder.downnumView.setText(mApkList.get(position).getDownnum() + "");
        holder.logoView.setTag(mApkList.get(position).getTitle());
        Bitmap bitmap = mApkList.get(position).getLogoBitmap();
        if (bitmap != null) {
            holder.logoView.setImageBitmap(bitmap);
        } else {
            Picasso.with(mActivity)
                    .load(mApkList.get(position).getLogo())
                    .placeholder(R.drawable.ic_default_thumbnail)
                    .into(holder.logoView);
        }
        holder.ratingBar.setRating(mApkList.get(position).getScore());

        return convertView;
    }

    static class ViewHolder {
        ImageView logoView;
        TextView titleView;
        TextView infoView;
        TextView downnumView;
        RatingBar ratingBar;
    }
}
