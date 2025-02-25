package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;
import com.jordanbunke.tdsm.data.layer.support.ColorSelection;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomizationLayer {
    public final String id;

    private final List<ColorSelection> influencingSelections;
    private final List<CustomizationLayer> dependents;
    private boolean locked;

    CustomizationLayer(final String id) {
        this.id = id;

        influencingSelections = new ArrayList<>();
        dependents = new ArrayList<>();
        locked = false;
    }

    public void addDependent(final CustomizationLayer dependent) {
        dependents.add(dependent);
    }

    public void updateDependents() {
        for (CustomizationLayer dependent : dependents)
            dependent.update();
    }

    public void addInfluencingSelections(
            final ColorSelection... selections
    ) {
        for (ColorSelection influencingSelection : selections)
            addInfluencingSelection(influencingSelection, false);

        update();
    }

    public void addInfluencingSelection(
            final ColorSelection influencingSelection
    ) {
        addInfluencingSelection(influencingSelection, true);
    }

    private void addInfluencingSelection(
            final ColorSelection influencingSelection, final boolean update
    ) {
        influencingSelections.add(influencingSelection);
        influencingSelection.addDependent(this);

        if (update) update();
    }

    public List<ColorSelection> getInfluencingSelections() {
        return influencingSelections;
    }

    public final boolean isLocked() {
        return locked;
    }

    public final void lock() {
        locked = true;
    }

    public final void unlock() {
        locked = false;
    }

    public abstract SpriteConstituent<String> compose();

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
