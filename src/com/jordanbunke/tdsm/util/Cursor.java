package com.jordanbunke.tdsm.util;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.Coord2D;

public enum Cursor {
    MAIN, POINTER, RETICLE, NONE;

    private static Cursor cursor;
    private static Coord2D mousePos;

    private final GameImage image;

    static {
        cursor = MAIN;
        mousePos = new Coord2D();
    }

    Cursor() {
        image = Graphics.readCursor(this);
    }

    public static void reset(final Coord2D mousePos) {
        cursor = MAIN;
        Cursor.mousePos = mousePos;
    }

    public static void ping(final Cursor cursor) {
        if (Cursor.cursor == MAIN)
            Cursor.cursor = cursor;
    }

    public static void render(final GameImage canvas) {
        final Coord2D crp = Layout.cursorRenderPos(cursor.image, mousePos);
        canvas.draw(cursor.image, crp.x, crp.y);
    }
}
