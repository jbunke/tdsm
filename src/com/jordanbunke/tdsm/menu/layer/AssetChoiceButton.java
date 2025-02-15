package com.jordanbunke.tdsm.menu.layer;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.button.MenuButton;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.data.layer.AssetChoiceLayer;
import com.jordanbunke.tdsm.menu.Button;
import com.jordanbunke.tdsm.util.*;

public final class AssetChoiceButton extends MenuButton implements Button {
    private final AssetChoiceLayer layer;
    private final int index;

    private GameImage preview;
    private final String tooltip;

    private GameImage base, highlight, selected;

    private AssetChoiceButton(
            final Coord2D position, final Bounds2D dimensions,
            final AssetChoiceLayer layer, final int index,
            final GameImage preview, final String tooltip
    ) {
        super(position, dimensions, Anchor.LEFT_TOP,
                true, () -> layer.choose(index));

        this.layer = layer;
        this.index = index;

        this.preview = preview;
        this.tooltip = tooltip;

        redraw();
    }

    public static AssetChoiceButton none(
            final Coord2D position, final Bounds2D dims,
            final AssetChoiceLayer layer
    ) {
        final String code = ResourceCodes.NONE,
                tooltip = ParserUtils.readTooltip(code);
        final GameImage preview = Graphics.readIcon(code);

        return new AssetChoiceButton(position, dims, layer,
                AssetChoiceLayer.NONE, preview, tooltip);
    }

    public static AssetChoiceButton ofChoice(
            final Coord2D position, final Bounds2D dims,
            final AssetChoiceLayer layer, final int index
    ) {
        final String tooltip = layer.getChoiceAt(index).name;
        final GameImage preview = layer.getPreview(index);

        return new AssetChoiceButton(position, dims,
                layer, index, preview, tooltip);
    }

    private void redraw() {
        final int w = getWidth(), h = getHeight();

        base = Graphics.drawAssetChoiceButton(preview,
                Button.sim(false, false), w, h);
        highlight = Graphics.drawAssetChoiceButton(preview,
                Button.sim(false, true), w, h);
        selected = Graphics.drawAssetChoiceButton(preview,
                Button.sim(true, false), w, h);
    }

    @Override
    public void update(final double deltaTime) {
        if (isNone())
            return;

        final GameImage preview = layer.getPreview(index);

        // update assets if preview has changed
        if (!this.preview.equals(preview)) {
            this.preview = preview;
            redraw();
        }

        // check whether selected
        if (index == layer.getChoiceIndex())
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
    public void process(final InputEventLogger eventLogger) {
        super.process(eventLogger);

        final Coord2D mousePos = eventLogger.getAdjustedMousePosition();

        if (mouseIsWithinBounds(mousePos)) {
            Tooltip.get().ping(tooltip, mousePos);
            Cursor.ping(Cursor.POINTER);
        }
    }

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

    private boolean isNone() {
        return index == AssetChoiceLayer.NONE;
    }
}
