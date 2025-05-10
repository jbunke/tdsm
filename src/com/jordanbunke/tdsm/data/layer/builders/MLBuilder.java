package com.jordanbunke.tdsm.data.layer.builders;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;
import com.jordanbunke.tdsm.data.layer.CustomizationLayer;
import com.jordanbunke.tdsm.data.layer.MaskLayer;

public final class MLBuilder {
    private final String id;
    private final CustomizationLayer[] targets;

    private SpriteConstituent<String> logic;

    public static MLBuilder init(
            final String id, final CustomizationLayer... targets
    ) {
        return new MLBuilder(id, targets);
    }

    private MLBuilder(
            final String id, final CustomizationLayer[] targets
    ) {
        this.id = id;
        this.targets = targets;

        logic = s -> GameImage.dummy();
    }

    public MaskLayer build() {
        return new MaskLayer(id, targets, logic);
    }

    public MLBuilder setLogic(final SpriteConstituent<String> logic) {
        this.logic = logic;
        return this;
    }
}
