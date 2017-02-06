package bjzhou.coolapk.app.ui.activities

import android.os.Bundle
import bjzhou.coolapk.app.R
import bjzhou.coolapk.app.ui.base.BaseActivity
import bjzhou.coolapk.app.ui.fragments.AppViewFragment

/**
 * Created by bjzhou on 14-8-19.
 */
class AppViewActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appview)

        val id = intent.getIntExtra("id", 0)
        setFragment(AppViewFragment.newInstance(id))
    }

    companion object {
        private val TAG = "AppViewActivity"
    }

}
