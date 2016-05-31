package bjzhou.coolapk.app.ui.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import bjzhou.coolapk.app.R;
import bjzhou.coolapk.app.model.Apk;
import bjzhou.coolapk.app.ui.activities.AppViewActivity;

/**
 * Created by bjzhou on 14-7-31.
 */
public class ApkListAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    private static final String TAG = "ApkListAdapter";
    private final RecyclerView mRecyclerView;
    private Activity mActivity;
    private List<Apk> mApkList = new ArrayList<Apk>();

    public ApkListAdapter(FragmentActivity activity, RecyclerView recyclerView) {
        mActivity = activity;
        mRecyclerView = recyclerView;
    }

    public void setApkList(List<Apk> apkList) {
        mApkList = apkList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View convertView = mActivity.getLayoutInflater().inflate(R.layout.list_item_app, viewGroup, false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        ViewHolder holder = (ViewHolder) viewHolder;
        holder.titleView.setText(mApkList.get(i).getTitle());
        if (mApkList.get(i).getInfo() == null) {
            String apk_info = "<font color=\"#ff35a1d4\">" + mApkList.get(i).getApkversionname() + "</font>";
            apk_info += "<font color=\"black\">, " + mApkList.get(i).getApksize() + ", </font>";
            if (mApkList.get(i).getUpdateFlag().equals("new")) {
                apk_info += "<font color=\"red\">New</font>";
            } else {
                apk_info += "<font color=\"black\">Update</font>";
            }
            mApkList.get(i).setInfo(Html.fromHtml(apk_info));
        }
        holder.infoView.setText(mApkList.get(i).getInfo());
        holder.downnumView.setText(String.valueOf(mApkList.get(i).getDownnum()));
        holder.logoView.setTag(mApkList.get(i).getTitle());
        Picasso.with(mActivity)
                .load(mApkList.get(i).getLogo())
                .placeholder(R.drawable.ic_default_thumbnail)
                .into(holder.logoView);
        holder.ratingBar.setRating(mApkList.get(i).getScore());
    }

    @Override
    public long getItemId(int position) {
        return mApkList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return mApkList.size();
    }

    @Override
    public void onClick(View v) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView logoView;
        public TextView titleView;
        public TextView infoView;
        public TextView downnumView;
        public RatingBar ratingBar;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            logoView = (ImageView) itemView.findViewById(R.id.list_item_icon);
            titleView = (TextView) itemView.findViewById(R.id.list_item_title);
            infoView = (TextView) itemView.findViewById(R.id.list_item_info);
            downnumView = (TextView) itemView.findViewById(R.id.list_item_downnum);
            ratingBar = (RatingBar) itemView.findViewById(R.id.list_item_ratingStar);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mActivity, AppViewActivity.class);
            intent.putExtra("id", mApkList.get(getPosition()).getId());
            mActivity.startActivity(intent);
        }
    }
}
