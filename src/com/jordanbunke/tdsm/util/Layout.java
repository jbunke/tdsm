package com.jordanbunke.tdsm.util;

public final class Layout {
    public static final int SCALE_UP = 2, CANVAS_W = 500, CANVAS_H = 300;

    public static int width() {
        return SCALE_UP * CANVAS_W;
    }

    public static int height() {
        return SCALE_UP * CANVAS_H;
    }
}
