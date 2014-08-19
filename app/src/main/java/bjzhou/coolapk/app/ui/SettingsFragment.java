package bjzhou.coolapk.app.ui;

import android.view.View;
import bjzhou.coolapk.app.R;
import bjzhou.coolapk.app.custom.PullToRefreshFragment;

/**
 * Created by bjzhou on 14-8-19.
 */
public class SettingsFragment extends PullToRefreshFragment {
    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onActionBarClick() {

    }

    @Override
    public void onRefreshStarted(View view) {

    }
}
