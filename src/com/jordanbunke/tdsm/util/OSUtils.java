package com.jordanbunke.tdsm.util;

public class OSUtils {
    private static final String NAME;

    static {
        NAME = System.getProperty("os.name").toLowerCase();
    }

    public static boolean isWindows() {
        return NAME.startsWith("win");
    }

    public static boolean isMacOS() {
        return NAME.startsWith("mac");
    }
}
