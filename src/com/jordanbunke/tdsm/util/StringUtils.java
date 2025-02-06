package com.jordanbunke.tdsm.util;

import java.util.Arrays;

public final class StringUtils {
    public static String nameFromID(final String id) {
        return Arrays.stream(id.split("[_\\-]"))
                .map(StringUtils::capitalizeFirstLetter)
                .reduce((a, b) -> a + " " + b)
                .orElse(capitalizeFirstLetter(id));
    }

    private static String capitalizeFirstLetter(final String word) {
        return word.charAt(0) + word.substring(1).toLowerCase();
    }
}
