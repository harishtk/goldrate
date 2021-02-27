/*
 * Copyright (C) 2014 Open Whisper Systems
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.harishtk.goldrate.app.util

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import com.harishtk.goldrate.app.util.Util.isEmpty
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility methods to help display dates in a nice, easily readable way.
 */
object DateUtils : android.text.format.DateUtils() {
    private val TAG = DateUtils::class.java.simpleName

    /**
     * e.g. 2020-09-04T19:17:51Z
     * https://www.iso.org/iso-8601-date-and-time-format.html
     *
     * Note: SDK_INT == 0 check needed to pass unit tests due to JVM date parser differences.
     *
     * @return The timestamp if able to be parsed, otherwise -1.
     */
    @JvmStatic
    @SuppressLint("ObsoleteSdkInt")
    fun parseIso8601(date: String?): Long? {
        val format: SimpleDateFormat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())
        } else {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
        }
        return if (isEmpty(date)) {
            -1
        } else try {
            format.parse(date!!)?.time
        } catch (e: ParseException) {
            Log.w(TAG, "Failed to parse date.", e)
            -1
        } catch (e: NullPointerException) {
            Log.w(TAG, "Failed to parse date.", e)
            -1
        }
    }
}