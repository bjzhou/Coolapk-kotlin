package bjzhou.coolapk.app.exceptions

import bjzhou.coolapk.app.model.Result

class ClientException(val statusCode: Int, str: String) : Exception(str) {

    constructor(result: Result<*>) : this(result.statusCode, result.message ?: "")
}
