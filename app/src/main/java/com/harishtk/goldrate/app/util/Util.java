package com.harishtk.goldrate.app.util;

import android.content.Context;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;

public class Util {

    private Util() {}

    public static boolean isEmpty(EncodedStringValue[] value) {
        return value == null || value.length == 0;
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(@Nullable String value) {
        return value == null || value.length() == 0;
    }

    public static boolean hasItems(@Nullable Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

    public static String getFirstNonEmpty(String... values) {
        for (String value : values) {
            if (!isEmpty(value)) {
                return value;
            }
        }
        return "";
    }

    private static final String PRE_DOMAIN_PATTERN = "http(?:s)?://";
    public static boolean urlMatches(@NonNull String s1, @NonNull String s2) {
        return s1.replaceAll(PRE_DOMAIN_PATTERN, "")
                .equalsIgnoreCase(s2.replaceAll(PRE_DOMAIN_PATTERN, ""));
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    @SuppressWarnings({"unused", "RedundantSuppression"})
    public static float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}