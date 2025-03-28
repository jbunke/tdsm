package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;
import com.jordanbunke.tdsm.util.StringUtils;

public final class PureComposeLayer extends CustomizationLayer {
    private final SpriteConstituent<String> logic;

    public PureComposeLayer(
            final String id, final SpriteConstituent<String> logic
    ) {
        super(id);

        this.logic = logic;
    }

    @Override
    public SpriteConstituent<String> compose() {
        return logic;
    }

    @Override
    public String name() {
        return StringUtils.nameFromID(id);
    }

    @Override
    public boolean isRendered() {
        return true;
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
