package com.jordanbunke.tdsm.data.style;

import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.tdsm.data.Animation;
import com.jordanbunke.tdsm.data.Directions;
import com.jordanbunke.tdsm.data.layer.CustomizationLayer;
import com.jordanbunke.tdsm.data.layer.Layers;

public final class FromFileStyle extends Style {
    public static final String NAME = "name", INFO_TOOLTIP = "info";

    public static final String DEF_NAME = "Uploaded", DEF_TOOLTIP = "";

    private String name = DEF_NAME, infoTooltip = DEF_TOOLTIP;

    /**
     * For use by scripting function <code>$Init.style(
     *      string id,
     *      int[] bounds,
     *      string[] directions,
     *      bool anim_orientation,
     *      anim[] animations,
     *      {bool : layer<>} layers
     * )</code>
     * */
    @SuppressWarnings("unused")
    public FromFileStyle(
            final String id, final Bounds2D dims, final Directions directions,
            final Animation[] animations, final CustomizationLayer[] custom,
            final CustomizationLayer[] assembly
    ) {
        super(id, dims, directions, animations, new Layers());

        populateLayers(custom, assembly);
        update();
    }

    private void populateLayers(
            final CustomizationLayer[] custom,
            final CustomizationLayer[] assembly
    ) {
        layers.addToCustomization(custom);
        layers.addToAssembly(assembly);
    }

    public String infoToolTip() {
        return infoTooltip;
    }

    public void setInfoTooltip(final String infoTooltip) {
        this.infoTooltip = infoTooltip;
    }

    @Override
    public String name() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
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
