package com.jordanbunke.tdsm.menu.pre_export;

import com.jordanbunke.color_proc.ColorFormat;
import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.button.MenuButtonStub;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.stip_parser.ParserSerializer;
import com.jordanbunke.tdsm.menu.Button;
import com.jordanbunke.tdsm.util.Cursor;
import com.jordanbunke.tdsm.util.ParserUtils;
import com.jordanbunke.tdsm.util.ResourceCodes;
import com.jordanbunke.tdsm.util.Tooltip;

import java.awt.*;
import java.util.Map;
import java.util.function.Supplier;

import static com.jordanbunke.tdsm.util.Graphics.*;
import static com.jordanbunke.tdsm.util.Layout.COL_SEL_BUTTON_DIM;

public final class ReplacementOptionButton extends MenuButtonStub
        implements Button {
    private static final Bounds2D DIMS =
            new Bounds2D(COL_SEL_BUTTON_DIM, COL_SEL_BUTTON_DIM);

    private final Supplier<Boolean> selectChecker;
    private final Runnable behaviour;

    private final String tooltip;

    private final GameImage base, highlight, selected;

    private ReplacementOptionButton(
            final Coord2D position,
            final Color selectedColor, final Color color,
            final Map<Color, Color> map, final String tooltip,
            final GameImage base, final GameImage highlight,
            final GameImage selected
    ) {
        super(position, DIMS, Anchor.LEFT_TOP, true);

        if (color == null) {
            selectChecker = () -> !map.containsKey(selectedColor);
            behaviour = () -> map.remove(selectedColor);
        } else {
            selectChecker = () -> color.equals(map.get(selectedColor));
            behaviour = () -> map.put(selectedColor, color);
        }

        this.tooltip = tooltip;

        this.base = base;
        this.highlight = highlight;
        this.selected = selected;
    }

    public static ReplacementOptionButton make(
            final Coord2D position, final Color selectedColor,
            final Color color, final Map<Color, Color> map
    ) {
        final String tooltip = "#" + ParserSerializer
                .serializeColor(color, true) + "\n" +
                ColorFormat.percentageSimilarity(
                        selectedColor, color, false, 2) + " similar";


        return new ReplacementOptionButton(
                position, selectedColor, color, map, tooltip,
                drawColSelButton(color, Button.sim(false, false)),
                drawColSelButton(color, Button.sim(false, true)),
                drawColSelButton(color, Button.sim(true, false)));
    }

    public static ReplacementOptionButton none(
            final Coord2D position, final Color selectedColor,
            final Map<Color, Color> map
    ) {
        final GameImage icon = readIcon(ResourceCodes.NONE);
        final int w = DIMS.width(), h = DIMS.height();
        final String tooltip = ParserUtils.readTooltip(ResourceCodes.NO_REPL);

        return new ReplacementOptionButton(
                position, selectedColor, null, map, tooltip,
                drawAssetChoiceButton(icon, Button.sim(false, false), w, h),
                drawAssetChoiceButton(icon, Button.sim(false, true), w, h),
                drawAssetChoiceButton(icon, Button.sim(true, false), w, h));
    }

    @Override
    public void execute() {
        behaviour.run();
    }

    @Override
    public void update(final double deltaTime) {
        if (selectChecker.get())
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
            com.jordanbunke.tdsm.util.Cursor.ping(Cursor.POINTER);
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
