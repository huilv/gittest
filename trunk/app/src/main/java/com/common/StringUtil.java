package com.common;


import javax.annotation.Nullable;

/**
 * Created by hsg on 12/20/16.
 */

public final class StringUtil {

    private StringUtil() {
    }

    public static String nullToEmpty(@Nullable final String string) {
        return string == null ? "" : string;
    }

    @Nullable
    public static String emptyToNull(@Nullable final String string) {
        return isNullOrEmpty(string) ? null : string;
    }

    public static boolean isNullOrEmpty(@Nullable final String string) {
        return (string == null) || (string.length() == 0);
    }
}
