package bjzhou.coolapk.app.model

import bjzhou.coolapk.app.exceptions.ClientException

class Result<T>(val status: Int?, str: String?) {
    var data: T? = null
    var message: String = str ?: "Empty error message"
        private set

    constructor(i: Int, str: String, t: T) : this(i, str) {
        data = t
    }

    val isSuccess: Boolean
        get() = status == null || status === 1

    val statusCode: Int
        get() = status ?: 1

    fun checkResult(): ClientException? {
        if (isSuccess) {
            return null
        }
        return ClientException(status ?: 1, message)
    }
}
