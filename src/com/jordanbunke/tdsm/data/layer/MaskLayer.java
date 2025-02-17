package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;
import com.jordanbunke.tdsm.util.StringUtils;

public final class MaskLayer extends CustomizationLayer {
    private final String name;
    private final CustomizationLayer target;
    private final SpriteConstituent<String> logic;

    public MaskLayer(
            final String id, final CustomizationLayer target,
            final SpriteConstituent<String> logic
    ) {
        super(id);

        name = StringUtils.nameFromID(id);

        this.target = target;
        this.logic = logic;
    }

    public CustomizationLayer getTarget() {
        return target;
    }

    @Override
    public SpriteConstituent<String> compose() {
        return logic;
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
        return false;
    }

    @Override
    public void update() {}

    @Override
    public void randomize(final boolean updateSprite) {}

    @Override
    public int calculateExpandedHeight() {
        return 0;
    }
}
