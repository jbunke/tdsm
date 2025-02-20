package com.jordanbunke.tdsm.util;

public class Settings {
    private enum BoolSettings {
        CAPTURE(false), SHOW_WIP(false);

        private boolean enabled;

        BoolSettings(final boolean defaultVal) {
            enabled = defaultVal;
        }

        public void set(final boolean value) {
            enabled = value;
        }
    }

    public static boolean isCapture() {
        return BoolSettings.CAPTURE.enabled;
    }

    public static void setCapture(final boolean value) {
        BoolSettings.CAPTURE.set(value);
    }

    public static boolean isShowWIP() {
        return BoolSettings.SHOW_WIP.enabled;
    }

    public static void setShowWIP(final boolean value) {
        BoolSettings.SHOW_WIP.set(value);
    }
}
