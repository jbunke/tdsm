package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;
import com.jordanbunke.tdsm.util.StringUtils;

import java.util.function.Supplier;

public final class DecisionLayer extends ManualRefreshLayer {
    private final Supplier<CustomizationLayer> logic;
    private CustomizationLayer decision;

    public DecisionLayer(
            final String id, final Supplier<CustomizationLayer> logic
    ) {
        super(id);

        this.logic = logic;

        update();
    }

    @Override
    public SpriteConstituent<String> compose() {
        return s -> GameImage.dummy();
    }

    @Override
    public String name() {
        return StringUtils.nameFromID(id);
    }

    @Override
    public boolean isRendered() {
        return decision.isRendered();
    }

    @Override
    public boolean isNonTrivial() {
        return decision.isNonTrivial();
    }

    @Override
    public void update() {
        think();
        decision.update();
        refreshElement();
    }

    private void think() {
        decision = logic.get();
    }

    @Override
    public void randomize(final boolean updateSprite) {
        if (isLocked())
            return;

        think();
        decision.randomize(updateSprite);
    }

    @Override
    public int calculateExpandedHeight() {
        return decision.calculateExpandedHeight();
    }

    public CustomizationLayer getDecision() {
        return decision;
    }
}
