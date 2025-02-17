package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;

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
        return decision.compose();
    }

    @Override
    public String name() {
        return decision.name();
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
