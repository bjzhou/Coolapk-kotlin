package bjzhou.coolapk.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.StrictMode


/**
 * Created by bjzhou on 14-7-29.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
//        startService(Intent(this, UpgradeService::class.java))
        context = applicationContext

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
        }
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
            private set
    }
}
