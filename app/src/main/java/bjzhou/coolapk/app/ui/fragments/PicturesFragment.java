package bjzhou.coolapk.app.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import bjzhou.coolapk.app.R;
import bjzhou.coolapk.app.model.PictureEntity;
import bjzhou.coolapk.app.net.ApiManager;
import io.reactivex.functions.Consumer;

/**
 * author: zhoubinjia
 * date: 2017/1/25
 */
public class PicturesFragment extends Fragment {

    private String type;
    private PicturesAdapter mAdapter;

    enum PictureType {
        recommend, hot, splash, newest, _2k
    }

    private static final String ARG_SECTION_NUMBER = "ARG_SECTION_NUMBER";

    private RecyclerView mPicturesView;

    public PicturesFragment() {
    }

    public static PicturesFragment newInstance(int sectionNumber) {
        PicturesFragment fragment = new PicturesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int position = getArguments().getInt(ARG_SECTION_NUMBER);
        if (position == 4) {
            type = "2k";
        } else {
            type = PictureType.values()[position].name();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pictures, container, false);
    }

    private void findViews(View parent) {
        mPicturesView = (RecyclerView) parent.findViewById(R.id.picturesView);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        findViews(view);
        mAdapter = new PicturesAdapter();
        mPicturesView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mPicturesView.setAdapter(mAdapter);
        ApiManager.getInstance().pictureList(type, 0)
                .subscribe(new Consumer<List<PictureEntity>>() {
                    @Override
                    public void accept(List<PictureEntity> pictureEntities) throws Exception {
                        mAdapter.setEntities(pictureEntities);
                    }
                });
    }

    class PicturesAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<PictureEntity> entities = new ArrayList<>();

        public void setEntities(List<PictureEntity> entities) {
            this.entities = entities;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_picture, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Picasso.with(holder.itemView.getContext())
                    .load(entities.get(position).getPic())
                    .fit()
                    .into(holder.image);
        }

        @Override
        public int getItemCount() {
            return entities.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
