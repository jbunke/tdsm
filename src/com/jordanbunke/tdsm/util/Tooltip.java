package com.jordanbunke.tdsm.util;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.Coord2D;

public final class Tooltip {
    private static final Tooltip INSTANCE;
    private static final GameImage BLANK = GameImage.dummy();

    public static final String NONE = "";

    private String tooltip, lastCheck;
    private Coord2D mousePos;
    private int ticks;
    private GameImage image;

    static {
        INSTANCE = new Tooltip();
    }

    private Tooltip() {
        tooltip = NONE;
        lastCheck = tooltip;
        ticks = 0;
        mousePos = new Coord2D();
        image = BLANK;
    }

    public static Tooltip get() {
        return INSTANCE;
    }

    public void ping(
            final String tooltip, final Coord2D mousePos
    ) {
        this.mousePos = mousePos;
        this.tooltip = tooltip;
    }

    public void check() {
        if (tooltip.equals(NONE) || !tooltip.equals(lastCheck)) {
            ticks = 0;
            image = BLANK;
        } else {
            ticks++;

            if (ticks == Constants.TOOLTIP_TICKS)
                image = Graphics.drawTooltip(tooltip);
        }

        lastCheck = tooltip;
    }

    public void render(final GameImage canvas) {
        if (ticks >= Constants.TOOLTIP_TICKS) {
            final Coord2D renderPos =
                    Layout.tooltipRenderPos(image, mousePos);
            canvas.draw(image, renderPos.x, renderPos.y);
        }
    }
}
