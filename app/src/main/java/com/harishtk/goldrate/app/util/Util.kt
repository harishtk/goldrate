package com.harishtk.goldrate.app.util

import android.content.Context
import android.util.DisplayMetrics

object Util {
    fun isEmpty(value: Array<EncodedStringValue?>?): Boolean {
        return value == null || value.isEmpty()
    }

    fun isEmpty(collection: Collection<*>?): Boolean {
        return collection == null || collection.isEmpty()
    }

    @JvmStatic
    fun isEmpty(value: String?): Boolean {
        return value == null || value.length == 0
    }

    fun hasItems(collection: Collection<*>?): Boolean {
        return collection != null && !collection.isEmpty()
    }

    @JvmStatic
    fun getFirstNonEmpty(vararg values: String?): String? {
        for (value in values) {
            if (!isEmpty(value)) {
                return value
            }
        }
        return ""
    }

    private const val PRE_DOMAIN_PATTERN = "http(?:s)?://"
    fun urlMatches(s1: String, s2: String): Boolean {
        return s1.replace(PRE_DOMAIN_PATTERN.toRegex(), "")
                .equals(s2.replace(PRE_DOMAIN_PATTERN.toRegex(), ""), ignoreCase = true)
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    fun convertDpToPixel(dp: Float, context: Context): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    fun convertPixelsToDp(px: Float, context: Context): Float {
        return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
}