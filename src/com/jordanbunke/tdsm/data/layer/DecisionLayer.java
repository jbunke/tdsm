package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;

import java.util.function.Supplier;

// TODO
public final class DecisionLayer extends CustomizationLayer {
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
    public SpriteConstituent<String> getComposer() {
        return decision.getComposer();
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
    public void update() {
        think();
        decision.update();
        refreshElement();
    }

    private void think() {
        decision = logic.get();
    }

    @Override
    public void randomize() {
        think();
        decision.randomize();
    }

    @Override
    public int calculateExpandedHeight() {
        return decision.calculateExpandedHeight();
    }

    public CustomizationLayer getDecision() {
        return decision;
    }
}
