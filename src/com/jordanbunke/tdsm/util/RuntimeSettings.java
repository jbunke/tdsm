package com.jordanbunke.tdsm.util;

import com.jordanbunke.delta_time.utility.math.MathPlus;

public class RuntimeSettings {
    private enum BoolSettings {
        CAPTURE(false), UPCOMING_UPDATES(false);

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

    public static boolean isUpcomingUpdates() {
        return BoolSettings.UPCOMING_UPDATES.enabled;
    }

    public static void setUpcomingUpdates(final boolean value) {
        BoolSettings.UPCOMING_UPDATES.set(value);
    }

    public static int getScale() {
        return IntSettings.SCALE.value;
    }

    public static void setScale(final int scale) {
        IntSettings.SCALE.set(scale);
    }
}
