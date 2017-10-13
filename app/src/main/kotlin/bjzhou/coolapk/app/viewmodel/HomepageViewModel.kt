package bjzhou.coolapk.app.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import bjzhou.coolapk.app.model.Apk
import bjzhou.coolapk.app.net.ApiManager
import java.net.URLEncoder

/**
 * Created by zhoubinjia on 2017/5/22.
 */
class HomepageViewModel : ViewModel() {

    var mQuery = ""
    var mApkList = MutableLiveData<List<Apk>>()

    private val obtainAction: (List<Apk>) -> Unit = { apks ->
        if (mPage == 1) {
            mApkList.value = apks
        } else {
            mPage++
            if (apks.isEmpty()) {
                mStatus.value = 0
            }
            val newList = mApkList.value?.toMutableList()
            newList?.addAll(apks)
            mApkList.value = newList
        }
        mStatus.value = 2
    }

    /**
     * 0: load complete
     * 1: refresh = true
     * 2: refresh = false
     */
    var mStatus = MutableLiveData<Int>()
    var mPage = 0

    fun obtainApkList() {
        mPage++
        if (mQuery.isEmpty()) {
            ApiManager.instance.mService.obtainHomepageApkList(mPage).enqueue(obtainAction)
        } else {
            ApiManager.instance.mService.obtainSearchApkList(URLEncoder.encode(mQuery), mPage).enqueue(obtainAction)
        }
    }
}