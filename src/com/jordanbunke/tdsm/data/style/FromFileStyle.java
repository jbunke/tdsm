package com.jordanbunke.tdsm.data.style;

import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.tdsm.data.Animation;
import com.jordanbunke.tdsm.data.Directions;
import com.jordanbunke.tdsm.data.layer.CustomizationLayer;
import com.jordanbunke.tdsm.data.layer.Layers;

public final class FromFileStyle extends Style {
    private FromFileStyle(
            final String id, final Bounds2D dims, final Directions directions,
            final Animation[] animations, final CustomizationLayer[] custom,
            final CustomizationLayer[] assembly
    ) {
        super(id, dims, directions, animations, new Layers());

        populateLayers(custom, assembly);
    }

    private void populateLayers(
            final CustomizationLayer[] custom,
            final CustomizationLayer[] assembly
    ) {
        layers.addToCustomization(custom);
        layers.addToAssembly(assembly);
    }

    @Override
    public String name() {
        // TODO
        return "From script example";
    }

    @Override
    public boolean shipping() {
        return true;
    }

    @Override
    public boolean hasSettings() {
        // TODO
        return false;
    }
}
