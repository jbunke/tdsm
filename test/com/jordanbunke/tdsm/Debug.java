package com.jordanbunke.tdsm;

import com.jordanbunke.tdsm.util.Settings;

public class Debug {
    private static final String
            FLAG_CAPTURE = "-c",
            FLAG_WIP = "-wip";

    public static void main(final String[] args) {
        processArgs(args);
        TDSM.main(args);
    }

    private static void processArgs(final String[] args) {
        for (String arg : args)
            switch (arg) {
                case FLAG_CAPTURE -> Settings.setCapture(true);
                case FLAG_WIP -> Settings.setShowWIP(true);
            }
    }
}
