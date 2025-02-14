package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;
import com.jordanbunke.tdsm.data.layer.support.ColorSelection;
import com.jordanbunke.tdsm.menu.layer.LayerElement;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomizationLayer {
    public final String id;

    private final List<ColorSelection> influencingSelections;
    LayerElement element;

    CustomizationLayer(final String id) {
        this.id = id;

        influencingSelections = new ArrayList<>();
        element = null;
    }

    public void addInfluencingSelection(
            final ColorSelection influencingSelection
    ) {
        influencingSelections.add(influencingSelection);
        influencingSelection.addDependent(this);
    }

    public List<ColorSelection> getInfluencingSelections() {
        return influencingSelections;
    }

    public abstract SpriteConstituent<String> getComposer();

    public abstract String name();

    public abstract boolean isRendered();

    public abstract void update();

    public abstract void randomize();

    @Override
    public String toString() {
        return name();
    }

    public void setElement(final LayerElement element) {
        this.element = element;
    }

    void refreshElement() {
        if (element != null)
            element.refresh();
    }
}
