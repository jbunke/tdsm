package com.jordanbunke.tdsm;

import com.jordanbunke.tdsm.util.RuntimeSettings;

public class Debug {
    private static final String
            FLAG_CAPTURE = "-c",
            FLAG_WIP = "-wip", SCALE_PREFIX = "-s:";

    public static void main(final String[] args) {
        processArgs(args);
        TDSM.main(args);
    }

    private static void processArgs(final String[] args) {
        for (String arg : args)
            switch (arg) {
                case FLAG_CAPTURE -> RuntimeSettings.setCapture(true);
                case FLAG_WIP -> RuntimeSettings.setShowWIP(true);
                default -> {
                    if (arg.startsWith(SCALE_PREFIX)) {
                        try {
                            final int value = Integer.parseInt(
                                    arg.substring(SCALE_PREFIX.length()));
                            RuntimeSettings.setScale(value);
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
    }
}
