package com.jordanbunke.tdsm.util.hardware;

import com.jordanbunke.delta_time.utility.math.MathPlus;

import java.awt.*;

public final class GBAUtils {
    private static final Integer[] FIVE_BITS_AS_EIGHT_BITS = new Integer[] {
            0, 8, 16, 25, 33, 41, 49, 58, 66, 74,
            82, 90, 99, 107, 115, 123, 132, 140,
            148, 156, 165, 173, 181, 189, 197,
            206, 214, 222, 230, 239, 247, 255
    };

    public static Color quantize(final Color input) {
        if (input.getAlpha() == 0) return input;

        return new Color(quantizeChannel(input.getRed()),
                quantizeChannel(input.getGreen()),
                quantizeChannel(input.getBlue()));
    }

    private static int quantizeChannel(final int channel) {
        return MathPlus.findBest(channel, 0, n -> n,
                (i1, i2) -> Math.abs(i1 - channel) < Math.abs(i2 - channel),
                FIVE_BITS_AS_EIGHT_BITS);
    }
}
