package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;
import com.jordanbunke.tdsm.data.layer.support.ColorSelection;
import com.jordanbunke.tdsm.util.StringUtils;

import static com.jordanbunke.tdsm.util.Layout.*;

public final class ColorSelectionLayer extends CustomizationLayer {
    private final String name;
    private final ColorSelection[] selections;

    public ColorSelectionLayer(
            final String id, final String name,
            final ColorSelection... selections
    ) {
        super(id);

        this.name = name;
        this.selections = selections;
    }

    public ColorSelectionLayer(
            final String id, final ColorSelection... selections
    ) {
        this(id, StringUtils.nameFromID(id), selections);
    }

    @Override
    public SpriteConstituent<String> getComposer() {
        return s -> GameImage.dummy();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean isRendered() {
        return false;
    }

    @Override
    public void update() {}

    @Override
    public void randomize() {
        for (ColorSelection selection : selections)
            selection.randomize();
    }

    @Override
    public int calculateExpandedHeight() {
        return COL_SEL_LAYER_BASE_H + (isSingle() ? 0 : COL_SEL_DROPOFF);
    }

    public boolean isSingle() {
        return selections.length == 1;
    }

    public ColorSelection[] getSelections() {
        return selections;
    }
}
