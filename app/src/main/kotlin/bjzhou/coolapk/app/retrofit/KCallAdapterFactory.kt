package bjzhou.coolapk.app.retrofit

import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @author zhoubinjia
 * @date 2017/10/12
 */
class KCallAdapterFactory : CallAdapter.Factory() {
    override fun get(returnType: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): CallAdapter<*, *>? {
        val c = getRawType(returnType)
        if (c == KCall::class.java) {
            return KCallAdapter(getParameterUpperBound(0, returnType as ParameterizedType?))
        }
        return null
    }
}