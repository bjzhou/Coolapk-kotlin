package bjzhou.coolapk.app.retrofit

import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

/**
 * @author zhoubinjia
 * @date 2017/10/12
 */
class KCallAdapter(private val returnType: Type) : CallAdapter<Any, KCall<Any>> {

    override fun responseType(): Type {
        return returnType
    }

    override fun adapt(call: Call<Any>): KCall<Any> {
        return KCall(call)
    }
}