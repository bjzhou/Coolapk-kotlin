package bjzhou.coolapk.app.ui.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import bjzhou.coolapk.app.R;
import bjzhou.coolapk.app.net.ApkDownloader;
import bjzhou.coolapk.app.ui.base.BaseActivity;
import bjzhou.coolapk.app.ui.fragments.HomepageFragment;
import bjzhou.coolapk.app.ui.fragments.SettingsFragment;
import bjzhou.coolapk.app.ui.fragments.UpgradeFragment;

public class NavActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawer;
    private MenuItem mSearchMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setActionBarTitle(R.string.title_section1);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setFragment(HomepageFragment.newInstance());
        ApkDownloader.getInstance().checkPermission(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, HomepageFragment.newInstance(query), "Search")
                    .addToBackStack("Search")
                    .commit();
            mSearchMenuItem.collapseActionView();
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav, menu);
        mSearchMenuItem = menu.findItem(R.id.action_search);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) mSearchMenuItem.getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment;

        switch (item.getItemId()) {
            case R.id.nav_home:
                fragment = HomepageFragment.newInstance();
                setActionBarTitle(R.string.title_section1);
                setFragment(fragment);
                break;
            case R.id.nav_start_page:
                Intent intent = new Intent(this, PhotoViewer.class);
                intent.putExtra("startPage", true);
                startActivity(intent);
                break;
            case R.id.nav_pictures:
                intent = new Intent(this, PicturesActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_upgrade:
                fragment = UpgradeFragment.newInstance();
                setActionBarTitle(R.string.title_section2);
                setFragment(fragment);
                break;
            case R.id.nav_settings:
                fragment = SettingsFragment.newInstance();
                setActionBarTitle(R.string.title_section3);
                setFragment(fragment);
                break;
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
