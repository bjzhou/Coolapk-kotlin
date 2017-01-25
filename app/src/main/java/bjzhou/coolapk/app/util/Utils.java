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

package bjzhou.coolapk.app.util;

import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.Html;

import java.util.Locale;
import java.util.UUID;

import bjzhou.coolapk.app.App;

/**
 * Class containing some static utility methods.
 */
public class Utils {

    public static String getUserAgent() {
        StringBuilder stringBuilder = new StringBuilder();
        String str = System.getProperty("http.agent");
        stringBuilder.append(str).append(" (#Build; ").append(Build.BRAND).append("; ").append(Build.MODEL).append("; ").append(Build.DISPLAY).append("; ").append(Build.VERSION.RELEASE).append(")");
        stringBuilder.append(" +CoolMarket/7.3");
        return Html.escapeHtml(stringBuilder.toString());
    }

    public static String getLocaleString() {
        Locale locale = Locale.getDefault();
        return locale.getLanguage() + "-" + locale.getCountry();
    }

    public static String getUUID() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        if (sp.contains("uuid")) {
            return sp.getString("uuid", "");
        }
        String uuid = UUID.randomUUID().toString();
        sp.edit().putString("uuid", uuid).apply();
        return uuid;
    }
}
