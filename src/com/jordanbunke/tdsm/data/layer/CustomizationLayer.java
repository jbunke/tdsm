package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;

public abstract class CustomizationLayer {
    public final String id;
    public final boolean rendered;
    private boolean expanded;

    CustomizationLayer(
            final String id, final boolean rendered
    ) {
        this.id = id;
        this.rendered = rendered;

        collapse();
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

    public abstract SpriteConstituent<String> getComposer();

    public abstract String name();

    @Override
    public String toString() {
        return name();
    }
}
