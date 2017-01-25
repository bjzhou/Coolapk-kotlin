package com.coolapk.market.util;

public class AuthUtils {
    public static native String getAS(String str);

    static {
        System.loadLibrary("a");
    }
}
