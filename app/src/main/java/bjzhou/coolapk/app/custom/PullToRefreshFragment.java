package bjzhou.coolapk.app.custom;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import bjzhou.coolapk.app.R;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by bjzhou on 14-8-15.
 */
public abstract class PullToRefreshFragment extends Fragment implements OnRefreshListener {

    private PullToRefreshLayout mPullToRefreshLayout;
    private TextView mTitleView;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View customView = getActivity().getLayoutInflater().inflate(R.layout.actionbar, null);
        customView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onActionBarClick();
            }
        });
        mTitleView = (TextView) customView.findViewById(R.id.action_bar_title);
        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setCustomView(customView);
        actionBar.setDisplayShowCustomEnabled(true);

        ViewGroup viewGroup = (ViewGroup) view;
        View pullable = viewGroup.findViewWithTag("Pullable");
        mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());
        ActionBarPullToRefresh.from(getActivity())
                .insertLayoutInto(viewGroup)
                .theseChildrenArePullable(pullable)
                .listener(this)
                .setup(mPullToRefreshLayout);
    }

    protected abstract void onActionBarClick();

    public PullToRefreshLayout getPullToRefreshLayout() {
        return mPullToRefreshLayout;
    }

    @Override
    public abstract void onRefreshStarted(View view);

    public void setTitle(String title) {
        mTitleView.setText(title);
    }
    public void setTitle(int resId) {
        mTitleView.setText(resId);
    }
}
