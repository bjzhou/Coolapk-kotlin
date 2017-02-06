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

import android.os.Build
import android.preference.PreferenceManager
import android.text.Html
import bjzhou.coolapk.app.App
import java.util.*

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
}
