package com.jordanbunke.tdsm.util;

import com.jordanbunke.delta_time.utility.math.Coord2D;

public final class Layout {
    public static final int SCALE_UP = 2, CANVAS_W = 500, CANVAS_H = 300,
            SPRITE_PREVIEW_SCALE_UP = 2, PREVIEW_RENDER_Y = 20;

    public static int width() {
        return SCALE_UP * CANVAS_W;
    }

    public static int height() {
        return SCALE_UP * CANVAS_H;
    }

    // Customization boxes: preview, sampler, top bar, layers, bottom bar

    private static final int LAYERS_W = (int) (CANVAS_W * 0.55),
            SCROLL_BAR_W = 25, LEFT_W = CANVAS_W - (LAYERS_W + SCROLL_BAR_W),
            BAR_W = LAYERS_W + SCROLL_BAR_W,
            SCROLL_BAR_X = CANVAS_W - SCROLL_BAR_W,
            HALF_H = CANVAS_H / 2, BAR_H = 30,
            LAYERS_H = CANVAS_H - (BAR_H * 2);

    public enum CustomizationBox {
        PREVIEW(0, 0, LEFT_W, HALF_H),
        SAMPLER(0, HALF_H, LEFT_W, HALF_H),
        LAYERS(LEFT_W, BAR_H, LAYERS_W, LAYERS_H),
        TOP(LEFT_W, 0, BAR_W, BAR_H),
        BOTTOM(LEFT_W, BAR_H + LAYERS_H, BAR_W, BAR_H),
        SCROLL(SCROLL_BAR_X, BAR_H, SCROLL_BAR_W, LAYERS_H);

        public final int x, y, width, height;

        CustomizationBox(
                final int x, final int y,
                final int width, final int height
        ) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public Coord2D at(
                final double percX, final double percY
        ) {
            return new Coord2D(atX(percX), atY(percY));
        }

        public int atX(final double percentage) {
            return x + (int)(percentage * width);
        }

        public int atY(final double percentage) {
            return y + (int)(percentage * height);
        }
    }

    private static final int LABEL_OFFSET_X = 10, LABEL_OFFSET_Y = -4;

    public static final int TEXT_IN_BUTTON_OFFSET_Y = -8,
            TEXT_BUTTON_H = 20, TEXT_BUTTON_EXTRA_W = 12,
            PX_PER_SCROLL = 20, DROPDOWN_EXTRA_W = 32;

    public static Coord2D labelPosFor(final int x, final int y) {
        return new Coord2D(x + LABEL_OFFSET_X, y + LABEL_OFFSET_Y);
    }
}
