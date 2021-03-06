package bjzhou.coolapk.app.ui.base

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import bjzhou.coolapk.app.R
import java.util.*

open class BaseActivity : AppCompatActivity() {

    private val REQUEST_PERMISSION = Random().nextInt(65535)
    private var mPermissionListener: ((permission: String, succeed: Boolean) -> Unit)? = null

    val currentFragment: Fragment
        get() = supportFragmentManager.findFragmentById(R.id.container)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
    }

    fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .add(R.id.container, fragment)
                .commit()
    }

    fun checkPermissions(listener: (permission: String, succeed: Boolean) -> Unit, vararg permissions: String) {
        this.mPermissionListener = listener
        val needRequests = ArrayList<String>()
        val needShowRationale = ArrayList<String>()
        for (permission in permissions) {
            val granted = ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
            val shouldRational = ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
            if (!granted && !shouldRational) {
                needRequests.add(permission)
            } else if (!granted) {
                needShowRationale.add(permission)
            } else {
                listener(permission, true)
            }
        }
        if (needRequests.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, needRequests.toTypedArray(), REQUEST_PERMISSION)
        }
        if (needShowRationale.isNotEmpty()) {
            Snackbar.make(window.decorView.rootView, getRequestPermissionRationaleMessage(), Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, {
                        ActivityCompat.requestPermissions(this, needShowRationale.toTypedArray(), REQUEST_PERMISSION)
                    })
                    .show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION) {
            for (i in permissions.indices) {
                mPermissionListener?.invoke(permissions[i], grantResults[i] == PackageManager.PERMISSION_GRANTED)
            }
        }
    }

    protected fun getRequestPermissionRationaleMessage(): CharSequence {
        return "Need permission granted to continue."
    }

    protected fun setActionBarTitle(resId: Int) {
        supportActionBar?.setTitle(resId)
    }

    protected fun showHome(showHome: Boolean) {
        supportActionBar?.setDisplayShowHomeEnabled(showHome)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
