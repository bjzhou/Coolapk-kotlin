package bjzhou.coolapk.app.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import bjzhou.coolapk.app.model.Apk
import bjzhou.coolapk.app.net.ApiManager
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.net.URLEncoder

/**
 * Created by zhoubinjia on 2017/5/22.
 */
class HomepageViewModel : ViewModel(), Observer<List<Apk>> {

    var mQuery = ""
    var mApkList = MutableLiveData<List<Apk>>()

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
            ApiManager.instance.obtainHomepageApkList(mPage).subscribeWith(this)
        } else {
            ApiManager.instance.obtainSearchApkList(URLEncoder.encode(mQuery), mPage).subscribeWith(this)
        }
    }

    override fun onSubscribe(d: Disposable) {}

    override fun onNext(apks: List<Apk>?) {
        if (mPage == 1) {
            mApkList.value = apks ?: emptyList<Apk>()
        } else {
            mPage++
            if (apks == null || apks.isEmpty()) {
                mStatus.value = 0
            }
            val newList = mApkList.value?.toMutableList()
            newList?.addAll(apks ?: emptyList())
            mApkList.value = newList
        }
    }

    override fun onError(e: Throwable) {
        e.printStackTrace()
        onComplete()
    }

    override fun onComplete() {
        mStatus.value = 2
    }
}