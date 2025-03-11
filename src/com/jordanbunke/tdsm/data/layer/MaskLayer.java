package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;
import com.jordanbunke.tdsm.util.StringUtils;

public final class MaskLayer extends CustomizationLayer {
    private final String name;
    private final CustomizationLayer[] targets;
    private final SpriteConstituent<String> logic;

    public MaskLayer(
            final String id, final CustomizationLayer[] targets,
            final SpriteConstituent<String> logic
    ) {
        super(id);

        name = StringUtils.nameFromID(id);

        this.targets = targets;
        this.logic = logic;
    }

    public CustomizationLayer[] getTargets() {
        return targets;
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
