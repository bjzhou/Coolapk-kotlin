package bjzhou.coolapk.app.custom;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;
import bjzhou.coolapk.app.R;

import java.util.List;

/**
 * Created by bjzhou on 14-8-19.
 */
public class BaseActivity extends ActionBarActivity {
    private TextView mTitleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View customView = getLayoutInflater().inflate(R.layout.actionbar, null);
        customView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onActionBarClick();
            }
        });
        mTitleView = (TextView) customView.findViewById(R.id.action_bar_title);
        mTitleView.setText(R.string.app_name);
        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(customView);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    public void onActionBarClick() {
    }

    public void setActionBarTitle(String title) {
        mTitleView.setText(title);
    }

    public void setActionBarTitle(int title) {
        mTitleView.setText(title);
    }

    public String getActionBarTitle() {
        return mTitleView.getText().toString();
    }

    public Fragment getActiveFragment() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm == null) {
            return null;
        }

        if (fm.getBackStackEntryCount() == 0) {
            return null;
        }
        String tag = fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1).getName();
        return fm.findFragmentByTag(tag);
    }

    public Fragment getVisibleFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for(Fragment fragment : fragments){
            if(fragment != null && fragment.isVisible())
                return fragment;
        }
        return null;
    }
}
