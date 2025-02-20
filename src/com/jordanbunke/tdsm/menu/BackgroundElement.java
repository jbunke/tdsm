package com.jordanbunke.tdsm.menu;

import com.jordanbunke.delta_time.menu.menu_elements.visual.StaticMenuElement;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.util.Graphics;

import static com.jordanbunke.tdsm.util.Layout.*;

public class BackgroundElement extends StaticMenuElement {
    private static final int DIV = 5;
    private int tick;

    public BackgroundElement() {
        super(new Coord2D(), Anchor.LEFT_TOP,
                Graphics.drawCheckerboard(CANVAS_W + CHECKERBOARD_SQUARE,
                CANVAS_H + CHECKERBOARD_SQUARE));

        tick = 0;
    }

    @Override
    public void update(final double deltaTime) {
        super.update(deltaTime);

        tick++;

        if (tick >= CHECKERBOARD_SQUARE * DIV)
            tick = 0;

        setPosition(new Coord2D(tick / DIV, tick / DIV).scale(-1));
    }

    @Override
    public int getRenderOrder() {
        return -1;
    }
}
