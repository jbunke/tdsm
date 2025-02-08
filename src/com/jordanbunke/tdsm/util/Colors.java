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
            CYAN = new Color(0, 0xb0, 0xb0);

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
        return new Color(0x80, 0x80, 0x80, 0x80);
    }
}
