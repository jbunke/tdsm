package com.jordanbunke.tdsm.util;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
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

    private static final int RIGHT_W = (int) (CANVAS_W * 0.6),
            LEFT_W = CANVAS_W - RIGHT_W,
            PREVIEW_H = (CANVAS_H / 2) - 10,
            SAMPLER_H = CANVAS_H - PREVIEW_H,
            BAR_H = 30, LAYERS_H = CANVAS_H - (BAR_H * 2);

    public enum ScreenBox {
        PREVIEW(0, 0, LEFT_W, PREVIEW_H),
        SAMPLER(0, PREVIEW_H, LEFT_W, SAMPLER_H),
        LAYERS(LEFT_W, BAR_H, RIGHT_W, LAYERS_H),
        TOP(LEFT_W, 0, RIGHT_W, BAR_H),
        BOTTOM(LEFT_W, BAR_H + LAYERS_H, RIGHT_W, BAR_H),
        LAYOUT(LEFT_W, 0, RIGHT_W, BAR_H + LAYERS_H),
        INCLUSION(0, PREVIEW_H, LEFT_W, SAMPLER_H);

        public final int x, y, width, height;

        ScreenBox(
                final int x, final int y,
                final int width, final int height
        ) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public static ScreenBox[] customizationBoxes() {
            return new ScreenBox[] {
                    PREVIEW, SAMPLER, LAYERS, TOP, BOTTOM
            };
        }

        public static ScreenBox[] configurationBoxes() {
            return new ScreenBox[] {
                    PREVIEW, INCLUSION, LAYOUT, BOTTOM
            };
        }

        public Coord2D pos() {
            return new Coord2D(x, y);
        }

        public Bounds2D dims() {
            return new Bounds2D(width, height);
        }

        public Coord2D at(
                final double percX, final double percY
        ) {
            return new Coord2D(atX(percX), atY(percY));
        }

        public Coord2D at(
                final int deltaX, final int deltaY
        ) {
            return pos().displace(deltaX, deltaY);
        }

        public int atX(final double percentage) {
            return x + (int)(percentage * width);
        }

        public int atY(final double percentage) {
            return y + (int)(percentage * height);
        }
    }

    private static final int LABEL_OFFSET_X = 10, LABEL_OFFSET_Y = -3,
            TOOLTIP_OFFSET_LEFT = -7, TOOLTIP_OFFSET_RIGHT = 5;

    public static final int TEXT_IN_BUTTON_OFFSET_Y = -8,
            TEXT_BUTTON_H = 20, TEXT_BUTTON_EXTRA_W = 12,
            TEXT_BUTTON_RENDER_BUFFER_X = 4, DD_ICON_LEFT_NUDGE = 2,
            PX_PER_SCROLL = 20, DROPDOWN_EXTRA_W = 32,
            VERT_SCROLL_BAR_W = 20, DD_ELEMENT_ALLOWANCE = 3,
            POST_LABEL_BUFFER_X = 4, POST_LABEL_OFFSET_Y = 8,
            SWATCH_BUTTON_DIM = TEXT_BUTTON_H,
            SWATCH_BUTTON_INC = SWATCH_BUTTON_DIM + 4,
            SWATCH_BUTTON_ROW = 2,
            COL_SEL_BUTTON_DIM = SWATCH_BUTTON_DIM,
            COLOR_TEXTBOX_W = 78, BUFFER = 10,
            TEXTBOX_SEG_INC = 1, HUE_SLIDER_W = SWATCH_BUTTON_DIM,
            TEXT_BUTTON_INC_Y = TEXT_BUTTON_H + 8,
            TOOLTIP_LINE_INC_Y = 8, TOOLTIP_INITIAL_OFFSET_Y = -4,
            TOOLTIP_PADDING_W = 2,
            COL_SEL_DROPOFF = 13, MIN_VERT_SCROLL_BAR_H = 10,
            ICON_TEXTBOX_RELATIVE_DIFF_Y = 2,
            PADDING_TEXTBOX_W = 60, STANDARD_FOLLOW_X = 48,
            CHECKERBOARD_SQUARE = 10;

    public static Coord2D labelPosFor(final int x, final int y) {
        return new Coord2D(x + LABEL_OFFSET_X, y + LABEL_OFFSET_Y);
    }

    public static Coord2D miniLabelPosFor(final int x, final int y) {
        return new Coord2D(x + LABEL_OFFSET_X, y);
    }

    public static Coord2D centerOn(final Coord2D pos, final GameImage icon) {
        return pos.displace(-(icon.getWidth() / 2),
                -(icon.getHeight() / 2));
    }

    public static Coord2D centerWithin(
            final GameImage outer, final GameImage inner
    ) {
        return new Coord2D((outer.getWidth() - inner.getWidth()) / 2,
                (outer.getHeight() - inner.getHeight()) / 2);
    }

    public static Coord2D canvasAt(final double percX, final double percY) {
        return new Coord2D((int) (percX * CANVAS_W), (int) (percY * CANVAS_H));
    }

    public static int screenWidth(final double perc) {
        return (int) (perc * CANVAS_W);
    }

    public static Coord2D textButtonBelow(final MenuElement ref) {
        return ref.getPosition().displace(0, TEXT_BUTTON_INC_Y);
    }

    public static Coord2D tooltipRenderPos(
            final GameImage tooltip, final Coord2D mousePos
    ) {
        final boolean left = mousePos.x <= CANVAS_W / 2,
                top = mousePos.y <= CANVAS_H / 2;
        final int w = tooltip.getWidth(), h = tooltip.getHeight(),
                x = mousePos.x - (left ? TOOLTIP_OFFSET_LEFT
                        : w + TOOLTIP_OFFSET_RIGHT),
                y = mousePos.y - (top ? 0 : h);

        return new Coord2D(x, y);
    }

    public static Coord2D cursorRenderPos(
            final GameImage cursor, final Coord2D mousePos
    ) {
        final int w = cursor.getWidth(), h = cursor.getHeight();
        return mousePos.displace(new Coord2D(w / 2, h / 2).scale(-1));
    }
}
