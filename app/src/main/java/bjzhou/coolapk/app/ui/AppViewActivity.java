package bjzhou.coolapk.app.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import bjzhou.coolapk.app.R;
import bjzhou.coolapk.app.custom.BaseActivity;

/**
 * Created by bjzhou on 14-8-19.
 */
public class AppViewActivity extends BaseActivity {
    private AppViewFragment fragment;
    private static final String TAG = "AppViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appview);

        ActionBar actionBar = getActionBar();
        actionBar.hide();

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);

        fragment = AppViewFragment.newInstance(id);
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();

    }

}
