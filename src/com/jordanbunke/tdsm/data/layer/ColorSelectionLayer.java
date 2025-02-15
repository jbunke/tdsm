package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;
import com.jordanbunke.tdsm.data.Sprite;
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
    public boolean isNonTrivial() {
        return true;
    }

    @Override
    public void update() {}

    @Override
    public void randomize(final boolean updateSprite) {
        for (ColorSelection selection : selections)
            selection.randomize(false);

        if (updateSprite)
            Sprite.get().getStyle().update();
    }

    @Override
    public int calculateExpandedHeight() {
        return COL_SEL_LAYER_BASE_H + (isSingle() ? 0
                : COL_SEL_DROPOFF + (selections.length > 5
                ? HORZ_SCROLL_BAR_H + 4 : 0));
    }

    public boolean isSingle() {
        return selections.length == 1;
    }

    public ColorSelection[] getSelections() {
        return selections;
    }
}
