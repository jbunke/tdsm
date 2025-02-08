package com.jordanbunke.tdsm.menu.layer;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.menu_elements.button.MenuButtonStub;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.data.layer.support.ColorSelection;
import com.jordanbunke.tdsm.menu.Button;
import com.jordanbunke.tdsm.menu.sampler.Sampler;
import com.jordanbunke.tdsm.util.Graphics;

import java.awt.*;

import static com.jordanbunke.tdsm.util.Layout.*;

public final class ColorSelectionButton extends MenuButtonStub
        implements Button {
    private static final Bounds2D DIMS = new Bounds2D(
            COL_SEL_BUTTON_DIM, COL_SEL_BUTTON_DIM);
    private final ColorSelection selection;
    private Color atLastUpdate;

    private GameImage base, highlight, selected;

    public ColorSelectionButton(
            final Coord2D position, final Anchor anchor,
            final ColorSelection selection
    ) {
        super(position, DIMS, anchor, true);

        this.selection = selection;
        atLastUpdate = selection.getColor();

        redraw(atLastUpdate);
    }

    @Override
    public void execute() {
        if (isSelected())
            Sampler.get().close();
        else
            Sampler.get().setSelection(selection);
    }

    private void redraw(final Color color) {
        base = Graphics.drawColSelButton(color, Button.sim(false, false));
        highlight = Graphics.drawColSelButton(color, Button.sim(false, true));
        selected = Graphics.drawColSelButton(color, Button.sim(true, false));
    }

    @Override
    public void update(final double deltaTime) {
        final Color color = selection.getColor();

        // update assets if color selection value has changed
        if (!color.equals(atLastUpdate)) {
            redraw(color);
            atLastUpdate = color;
        }

        // check whether selected
        if (selection.equals(Sampler.get().getSelection()))
            select();
        else
            deselect();
    }

    @Override
    public void render(final GameImage canvas) {
        draw(getAsset(false), canvas);
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
