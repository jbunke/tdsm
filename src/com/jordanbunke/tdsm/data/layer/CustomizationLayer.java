package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;
import com.jordanbunke.tdsm.data.layer.support.ColorSelection;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomizationLayer {
    public final String id;
    public final boolean rendered;
    private boolean expanded;

    private final List<ColorSelection> influencingSelections;

    CustomizationLayer(
            final String id, final boolean rendered
    ) {
        this.id = id;
        this.rendered = rendered;

        influencingSelections = new ArrayList<>();

        collapse();
    }

    public void addInfluencingSelection(
            final ColorSelection influencingSelection
    ) {
        influencingSelections.add(influencingSelection);
        influencingSelection.addDependent(this);
    }

    public void expand() {
        expanded = true;
    }

    public void collapse() {
        expanded = false;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public List<ColorSelection> getInfluencingSelections() {
        return influencingSelections;
    }

    public abstract SpriteConstituent<String> getComposer();

    public abstract String name();

    public abstract void update();

    public abstract void randomize();

    @Override
    public String toString() {
        return name();
    }
}
