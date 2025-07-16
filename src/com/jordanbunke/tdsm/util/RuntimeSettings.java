package com.jordanbunke.tdsm.util;

import com.jordanbunke.delta_time.utility.math.MathPlus;

public class RuntimeSettings {
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

    private enum IntSettings {
        SCALE(2, 1, 3);

        private int value;
        private final int min, max;

        IntSettings(
                final int defaultValue, final int min, final int max
        ) {
            value = defaultValue;

            this.min = min;
            this.max = max;
        }

        public void set(final int value) {
            this.value = MathPlus.bounded(min, value, max);
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

    public static int getScale() {
        return IntSettings.SCALE.value;
    }

    public static void setScale(final int scale) {
        IntSettings.SCALE.set(scale);
    }
}
