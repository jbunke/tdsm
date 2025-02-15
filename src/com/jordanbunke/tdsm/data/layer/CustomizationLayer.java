package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;
import com.jordanbunke.tdsm.data.layer.support.ColorSelection;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomizationLayer {
    public final String id;

    private final List<ColorSelection> influencingSelections;

    CustomizationLayer(final String id) {
        this.id = id;

        influencingSelections = new ArrayList<>();
    }

    public void addInfluencingSelection(
            final ColorSelection influencingSelection
    ) {
        influencingSelections.add(influencingSelection);
        influencingSelection.addDependent(this);
        update();
    }

    public List<ColorSelection> getInfluencingSelections() {
        return influencingSelections;
    }

    public abstract SpriteConstituent<String> getComposer();

    public abstract String name();

    public abstract boolean isRendered();

    public abstract boolean isNonTrivial();

    public abstract void update();

    public abstract void randomize(final boolean updateSprite);

    public abstract int calculateExpandedHeight();

    @Override
    public String toString() {
        return name();
    }
}
