package bjzhou.coolapk.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import bjzhou.coolapk.app.R;
import bjzhou.coolapk.app.ui.fragments.AppViewFragment;
import bjzhou.coolapk.app.ui.base.BaseActivity;

/**
 * Created by bjzhou on 14-8-19.
 */
public class AppViewActivity extends BaseActivity {
    private static final String TAG = "AppViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appview);

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);
        setFragment(AppViewFragment.newInstance(id));
    }

}
