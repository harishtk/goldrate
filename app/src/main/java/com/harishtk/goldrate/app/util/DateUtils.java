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
package com.harishtk.goldrate.app.util;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Utility methods to help display dates in a nice, easily readable way.
 */
public class DateUtils extends android.text.format.DateUtils {

  private static final String           TAG                = DateUtils.class.getSimpleName();

  /**
   * e.g. 2020-09-04T19:17:51Z
   * https://www.iso.org/iso-8601-date-and-time-format.html
   *
   * Note: SDK_INT == 0 check needed to pass unit tests due to JVM date parser differences.
   *
   * @return The timestamp if able to be parsed, otherwise -1.
   */
  @SuppressLint("ObsoleteSdkInt")
  public static long parseIso8601(@Nullable String date) {
    SimpleDateFormat format;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault());
    } else {
      format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
    }

    if (Util.isEmpty(date)) {
      return -1;
    }

    try {
      return format.parse(date).getTime();
    } catch (ParseException | NullPointerException e) {
      Log.w(TAG, "Failed to parse date.", e);
      return -1;
    }
  }
}
