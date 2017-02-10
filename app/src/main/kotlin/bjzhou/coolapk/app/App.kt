package bjzhou.coolapk.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import io.reactivex.plugins.RxJavaPlugins

/**
 * Created by bjzhou on 14-7-29.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
//        startService(Intent(this, UpgradeService::class.java))
        context = applicationContext

        RxJavaPlugins.setErrorHandler(Throwable::printStackTrace)
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
            private set
    }
}
