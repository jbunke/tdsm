package com.jordanbunke.tdsm.menu.pre_export;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.button.MenuButtonStub;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.menu.Button;
import com.jordanbunke.tdsm.util.Cursor;
import com.jordanbunke.tdsm.util.Tooltip;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.jordanbunke.tdsm.util.Graphics.drawColReplButton;
import static com.jordanbunke.tdsm.util.Layout.COL_REPL_OFF_DIM;
import static com.jordanbunke.tdsm.util.Layout.COL_SEL_BUTTON_DIM;

public final class ColorReplacementButton extends MenuButtonStub
        implements Button {
    private static final Bounds2D DIMS = new Bounds2D(
            COL_SEL_BUTTON_DIM + COL_REPL_OFF_DIM, COL_SEL_BUTTON_DIM);

    private final Color color;
    private final Function<Color, Color> replaceGetter;
    private final Supplier<Color> selectGetter;
    private final Consumer<Color> selector;
    private Color replacement;

    private final String tooltip;

    private GameImage base, highlight, selected;

    public ColorReplacementButton(
            final Coord2D position,
            final Color color, final String tooltip,
            final Function<Color, Color> replaceGetter,
            final Supplier<Color> selectGetter,
            final Consumer<Color> selector
    ) {
        super(position, DIMS, Anchor.LEFT_TOP, true);

        this.color = color;
        this.tooltip = tooltip;

        this.replaceGetter = replaceGetter;
        this.selectGetter = selectGetter;
        this.selector = selector;

        replacement = replaceGetter.apply(color);

        redraw();
    }

    private void redraw() {
        base = drawColReplButton(color, replacement,
                Button.sim(false, false));
        highlight = drawColReplButton(color, replacement,
                Button.sim(false, true));
        selected = drawColReplButton(color, replacement,
                Button.sim(true, false));
    }

    @Override
    public void execute() {
        selector.accept(isSelected() ? null : color);
    }

    @Override
    public void update(final double deltaTime) {
        final Color retrieved = replaceGetter.apply(color);

        // redraw assets if replacement color has changed
        if ((replacement == null && retrieved != null) ||
                (replacement != null && !replacement.equals(retrieved))) {
            replacement = retrieved;
            redraw();
        }

        // check whether selected
        if (color.equals(selectGetter.get()))
            select();
        else
            deselect();
    }

    @Override
    public void render(final GameImage canvas) {
        draw(getAsset(), canvas);
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        super.process(eventLogger);

        final Coord2D mousePos = eventLogger.getAdjustedMousePosition();
        final boolean inBounds = mouseIsWithinBounds(mousePos);

        setHighlighted(inBounds);

        if (inBounds) {
            Tooltip.get().ping(tooltip, mousePos);
            Cursor.ping(Cursor.POINTER);
        }
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
