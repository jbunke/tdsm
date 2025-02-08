package com.jordanbunke.tdsm.menu.sampler;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.menu_elements.button.MenuButton;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.menu.Button;
import com.jordanbunke.tdsm.util.Graphics;

import java.awt.*;

import static com.jordanbunke.tdsm.util.Layout.*;
import static com.jordanbunke.tdsm.util.Layout.CustomizationBox.*;

public final class SwatchButton extends MenuButton implements Button {
    private static final Bounds2D DIMS = new Bounds2D(
            SWATCH_BUTTON_DIM, SWATCH_BUTTON_DIM);

    private final Color color;

    private final GameImage base, highlight, selected;

    private SwatchButton(
            final Coord2D position, final Runnable behaviour,
            final Color color
    ) {
        super(position, DIMS, Anchor.LEFT_TOP, true, behaviour);

        this.color = color;

        base = Graphics.drawSwatchButton(color, Button.sim(false, false));
        highlight = Graphics.drawSwatchButton(color, Button.sim(false, true));
        selected = Graphics.drawSwatchButton(color, Button.sim(true, false));
    }

    public static SwatchButton make(
            final Color color, final int index, final SwatchManager manager
    ) {
        final int x = index / SWATCH_BUTTON_COLUMN,
                y = index % SWATCH_BUTTON_COLUMN;
        final Coord2D pos = SAMPLER.at(10, 10)
                .displace(x * SWATCH_BUTTON_INC, y * SWATCH_BUTTON_INC);

        final Runnable behaviour = () -> manager.setColor(color);

        return new SwatchButton(pos, behaviour, color);
    }

    @Override
    public void update(final double deltaTime) {
        if (color.equals(Sampler.get().getColor()))
            select();
        else
            deselect();
    }

    @Override
    public void render(final GameImage canvas) {
        draw(getAsset(), canvas);
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}

    @Override
    public GameImage getBaseAsset() {
        return base;
    }

    @Override
    public GameImage getHighlightedAsset() {
        return highlight;
    }

    @Override
    public GameImage getSelectedAsset() {
        return selected;
    }
}
