package bjzhou.coolapk.app.model

class Result<T>(val status: Int?, str: String?) {
    var data: T? = null
    var message: String = str ?: "Empty error message"
        private set

    val isSuccess: Boolean
        get() = status == null || status == 1

    val statusCode: Int
        get() = status ?: 1

}
