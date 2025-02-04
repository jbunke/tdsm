package com.jordanbunke.tdsm.util;

public final class ParserUtils {
    public static final int CODE = 0, VALUE = 1, DESIRED = 2;
    private static final String
            OPEN_SETTING_VAL = "{", CLOSE_SETTING_VAL = "}",
            SETTING_SEPARATOR = ":";

    public static String[] splitIntoCodeAndValue(final String line) {
        final int oi = line.indexOf(OPEN_SETTING_VAL),
                ol = OPEN_SETTING_VAL.length(),
                ci = line.indexOf(CLOSE_SETTING_VAL),
                si = line.indexOf(SETTING_SEPARATOR);

        final boolean hasValue = oi > si && oi < ci, valid = si > 0 && hasValue;

        if (!valid)
            return new String[] {};

        final String code = line.substring(0, si),
                value = line.substring(oi + ol, ci);

        return new String[] { code, value };
    }

    public static String encloseSetting(final String code, final String value) {
        return code + SETTING_SEPARATOR + OPEN_SETTING_VAL + value +
                CLOSE_SETTING_VAL + "\n";
    }
}
