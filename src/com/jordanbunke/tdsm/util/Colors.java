package com.jordanbunke.tdsm.util;

import java.awt.*;

public final class Colors {
    private static final Color BLACK = new Color(0, 0, 0),
            WHITE = new Color(0xff, 0xff, 0xff),
            GREY = new Color(0x80, 0x80, 0x80),
            RED = new Color(0xe0, 0x20, 0x20),
            GREEN = new Color(0x20, 0xe0, 0x20),
            BLUE = new Color(0x20, 0x20, 0xe0),
            ORANGE = new Color(0xc0, 0x60, 0),
            BROWN = new Color(0x60, 0x30, 0x10),
            YELLOW = new Color(0xe0, 0xc0, 0),
            PURPLE = new Color(0xb0, 0, 0xb0),
            PINK = new Color(0xff, 0x80, 0xc0),
            CYAN = new Color(0, 0xb0, 0xb0),
            VEIL = new Color(0x80, 0x80, 0x80, 0x80),
            HIGHLIGHT_OVERLAY = new Color(0x80, 0x80, 0xff, 0x80),
            INVALID = new Color(0x80, 0, 0);

    public static final Color[] DEFAULT_SWATCHES = new Color[] {
            BLACK, WHITE, GREY, RED, GREEN, BLUE,
            ORANGE, BROWN, YELLOW, PURPLE, PINK, CYAN
    };

    public static Color def() {
        return BLACK;
    }

    public static Color highlight() {
        return WHITE;
    }

    public static Color selected() {
        return BLUE;
    }

    public static Color black() {
        return BLACK;
    }

    public static Color veil() {
        return VEIL;
    }

    public static Color highlightOverlay() {
        return HIGHLIGHT_OVERLAY;
    }

    public static Color invalid() {
        return INVALID;
    }

    public static Color shiftRGB(final Color base, final int shift) {
        return new Color(
                shiftChannel(base.getRed(), Math.abs(shift)),
                shiftChannel(base.getGreen(), Math.abs(shift)),
                shiftChannel(base.getBlue(), Math.abs(shift)));
    }

    private static int shiftChannel(final int c, final int shift) {
        final int MIDDLE = 0x80;
        final boolean increase = Math.signum((double) (MIDDLE - c)) >= 0.0;

        return c + (increase ? shift : -shift);
    }
}
