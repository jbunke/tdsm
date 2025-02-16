package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;
import com.jordanbunke.tdsm.menu.layer.LayerElement;

import java.util.function.Supplier;

public final class DecisionLayer extends CustomizationLayer {
    private final Supplier<CustomizationLayer> logic;
    private CustomizationLayer decision;
    LayerElement element;

    public DecisionLayer(
            final String id, final Supplier<CustomizationLayer> logic
    ) {
        super(id);

        this.logic = logic;
        element = null;

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

    public void setElement(final LayerElement element) {
        this.element = element;
    }

    private void refreshElement() {
        if (element != null)
            element.refresh();
    }
}
