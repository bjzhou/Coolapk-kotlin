package bjzhou.coolapk.app.retrofit

import android.util.Log
import bjzhou.coolapk.app.model.Result
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okio.Buffer
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.OutputStreamWriter
import java.lang.reflect.Type
import java.nio.charset.Charset

/**
 * @author zhoubinjia
 * @date 2017/10/13
 */
class V6GsonConverterFactory(private val gson: Gson): Converter.Factory() {

    override fun responseBodyConverter(type: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *>? {
        return ResponseConverter<Any>(type)
    }

    @Suppress("UNCHECKED_CAST")
    override fun requestBodyConverter(type: Type?, parameterAnnotations: Array<out Annotation>?, methodAnnotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<*, RequestBody>? {
        val adapter = gson.getAdapter(TypeToken.get(type)) as TypeAdapter<Any>
        return Converter<Any, RequestBody> {
            val buffer = Buffer()
            val writer = OutputStreamWriter(buffer.outputStream(), Charset.forName("UTF-8"))
            val jsonWriter = gson.newJsonWriter(writer)
            adapter.write(jsonWriter, it)
            jsonWriter.close()
            return@Converter RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), buffer.readByteString())
        }
    }

    companion object {
        val TAG = "V6GsonConverterFactory"
        fun create(): V6GsonConverterFactory {
            return V6GsonConverterFactory(Gson())
        }
    }

    @Suppress("UNCHECKED_CAST")
    inner class ResponseConverter<T>(type: Type?): Converter<ResponseBody, T> {

        private val adapter: TypeAdapter<Result<T>>
                = gson.getAdapter(TypeToken.getParameterized(Result::class.java, type)) as TypeAdapter<Result<T>>

        override fun convert(value: ResponseBody): T? {
            val jsonReader = gson.newJsonReader(value.charStream())
            value.use {
                val r = adapter.read(jsonReader)
                if (!r.isSuccess) {
                    Log.e(TAG, r.message)
                }
                r.data?.let {
                    return it
                }
            }
            return null
        }
    }
}