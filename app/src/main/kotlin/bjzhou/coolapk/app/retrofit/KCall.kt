package bjzhou.coolapk.app.retrofit

import android.util.Log
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import okhttp3.Request
import retrofit2.Call

/**
 * @author zhoubinjia
 * @date 2017/10/12
 */
class KCall<out T>(private var call: Call<T>) {

    private var job: Job? = null

    fun cancel() {
        job?.cancel()
    }

    fun isCanceled(): Boolean {
        return job?.isCancelled ?: false
    }

    fun request(): Request {
        return call.request()
    }

    fun execute(): T? {
        try {
            val res = call.execute()
            if (!res.isSuccessful) {
                Log.e(TAG, call.request().toString() + ": " + res.message())
            }
            return res.body()
        } catch(e: Exception) {
            Log.e(TAG, call.request().toString() + ": " + e.toString())
            e.printStackTrace()
        }
        return null
    }

    fun enqueue(action: (T) -> Unit) {
        job = launch(UI) {
            try {
                val res = async { call.execute() }.await()
                res.body()?.let(action)
                if (!res.isSuccessful) {
                    Log.e(TAG, call.request().toString() + ": " + res.message())
                }
            } catch(e: Exception) {
                Log.e(TAG, call.request().toString() + ": " + e.toString())
                e.printStackTrace()
            }
        }
    }

    companion object {
        val TAG = "KCall"
    }
}