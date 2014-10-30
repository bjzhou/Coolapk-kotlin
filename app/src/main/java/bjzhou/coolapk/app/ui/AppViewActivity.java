package bjzhou.coolapk.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import bjzhou.coolapk.app.R;

/**
 * Created by bjzhou on 14-8-19.
 */
public class AppViewActivity extends ActionBarActivity {
    private AppViewFragment fragment;
    private static final String TAG = "AppViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appview);

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);

        fragment = AppViewFragment.newInstance(id);
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();

    }

}
