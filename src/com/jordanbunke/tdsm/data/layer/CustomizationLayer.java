package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;
import com.jordanbunke.tdsm.data.layer.support.ColorSelection;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomizationLayer {
    public final String id;

    private final List<ColorSelection> influencingSelections;
    private boolean locked;

    CustomizationLayer(final String id) {
        this.id = id;

        influencingSelections = new ArrayList<>();
        locked = false;
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
