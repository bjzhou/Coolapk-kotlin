package bjzhou.coolapk.app.ui.activities

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import bjzhou.coolapk.app.R
import bjzhou.coolapk.app.net.ApkDownloader
import bjzhou.coolapk.app.ui.base.BaseActivity
import bjzhou.coolapk.app.ui.fragments.HomepageFragment
import bjzhou.coolapk.app.ui.fragments.PicturesRootFragment
import bjzhou.coolapk.app.ui.fragments.SettingsFragment
import bjzhou.coolapk.app.ui.fragments.UpgradeFragment
import kotlinx.android.synthetic.main.activity_nav.*
import kotlinx.android.synthetic.main.app_bar_nav.*

class NavActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var mSearchMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav)
        setSupportActionBar(toolbar)
        setActionBarTitle(R.string.title_section1)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        nav_view.setCheckedItem(R.id.nav_home)
        setFragment(HomepageFragment.newInstance())
        ApkDownloader.instance.checkPermission(this)
    }

    override fun onNewIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            supportFragmentManager.beginTransaction()
                    .add(R.id.container, HomepageFragment.newInstance(query), "Search")
                    .addToBackStack("Search")
                    .commit()
            mSearchMenuItem?.collapseActionView()
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else if (currentFragment !is HomepageFragment) {
            tabs.visibility = View.GONE
            val lp = toolbar.layoutParams as AppBarLayout.LayoutParams
            lp.scrollFlags = 0
            setActionBarTitle(R.string.title_section1)
            setFragment(HomepageFragment.newInstance())
            nav_view.setCheckedItem(R.id.nav_home)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.nav, menu)
        mSearchMenuItem = menu.findItem(R.id.action_search)

        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = mSearchMenuItem?.actionView as SearchView
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(componentName))
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val fragment: Fragment

        tabs.visibility = View.GONE
        val lp = toolbar.layoutParams as AppBarLayout.LayoutParams
        lp.scrollFlags = 0

        when (item.itemId) {
            R.id.nav_home -> {
                fragment = HomepageFragment.newInstance()
                setActionBarTitle(R.string.title_section1)
                setFragment(fragment)
            }
            R.id.nav_start_page -> {
                val intent = Intent(this, PhotoViewer::class.java)
                intent.putExtra("startPage", true)
                startActivity(intent)
                drawer_layout.closeDrawer(GravityCompat.START)
                return false
            }
            R.id.nav_pictures -> {
                fragment = PicturesRootFragment.newInstance()
                fragment.onSetupTabs = {
                    val layoutParams = toolbar.layoutParams as AppBarLayout.LayoutParams
                    layoutParams.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or
                            AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
                    tabs.visibility = View.VISIBLE
                    tabs.setupWithViewPager(it)
                }
                setActionBarTitle(R.string.title_pictures)
                setFragment(fragment)
            }
            R.id.nav_upgrade -> {
                fragment = UpgradeFragment.newInstance()
                setActionBarTitle(R.string.title_section2)
                setFragment(fragment)
            }
            R.id.nav_settings -> {
                fragment = SettingsFragment.newInstance()
                setActionBarTitle(R.string.title_section3)
                setFragment(fragment)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
