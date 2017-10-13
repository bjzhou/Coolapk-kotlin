/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package bjzhou.coolapk.app.util

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.preference.PreferenceManager
import android.support.v4.content.FileProvider
import android.text.Html
import bjzhou.coolapk.app.App
import java.util.*
import android.util.Log
import bjzhou.coolapk.app.BuildConfig
import java.io.File


/**
 * Class containing some static utility methods.
 */
object Utils {

    val userAgent: String
        get() {
            val stringBuilder = StringBuilder()
            val str = System.getProperty("http.agent")
            stringBuilder.append(str).append(" (#Build; ").append(Build.BRAND).append("; ").append(Build.MODEL).append("; ").append(Build.DISPLAY).append("; ").append(Build.VERSION.RELEASE).append(")")
            stringBuilder.append(" +CoolMarket/7.3")
            return Html.escapeHtml(stringBuilder.toString())
        }

    val localeString: String
        get() {
            val locale = Locale.getDefault()
            return locale.language + "-" + locale.country
        }

    val uuid: String
        get() {
            val sp = PreferenceManager.getDefaultSharedPreferences(App.context)
            if (sp.contains("uuid")) {
                return sp.getString("uuid", "")
            }
            val uuid = UUID.randomUUID().toString()
            sp.edit().putString("uuid", uuid).apply()
            return uuid
        }

    val networkConnected: Boolean
        get() {
            val cm = App.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo != null && cm.activeNetworkInfo.isConnectedOrConnecting
        }

    fun installApk(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW)
        var contentUri = uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            contentUri = FileProvider.getUriForFile(App.context, BuildConfig.APPLICATION_ID + ".fileProvider", File(uri.path))
        }
        Log.d("Utils", "" + uri + " new" + contentUri)
        intent.setDataAndType(contentUri, "application/vnd.android.package-archive")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
