package bjzhou.coolapk.app.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Created by zhoubinjia on 16/7/13.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive: " + intent.action)
    }

    companion object {
        private val TAG = BootReceiver::class.java.simpleName
    }
}
