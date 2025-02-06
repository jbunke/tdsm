package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.tdsm.data.style.Style;

public final class AssetChoiceLayer extends CustomizationLayer {
    private final Bounds2D dims;
    private final String name;
    private final Style style;

    private final AssetChoice[] choices;

    AssetChoiceLayer(
            final String id, final String name,
            final Bounds2D dims, final Style style,
            final AssetChoice[] choices
    ) {
        super(id, true);

        this.style = style;
        this.name = name;
        this.dims = dims;
        this.choices = choices;
    }

    @Override
    public SpriteConstituent<String> getComposer() {
        return null;
    }

    @Override
    public String name() {
        return name;
    }
}
