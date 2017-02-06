package com.coolapk.market.util

object AuthUtils {
    external fun getAS(str: String): String

    init {
        System.loadLibrary("a")
    }
}
