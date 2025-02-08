package com.jordanbunke.tdsm.data.layer.support;

import com.jordanbunke.delta_time.utility.math.RNG;
import com.jordanbunke.tdsm.data.layer.CustomizationLayer;
import com.jordanbunke.tdsm.util.Colors;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public final class ColorSelection {
    public final String name;
    private final Color[] swatches;
    private final boolean anyColor;

    private Color color;

    private final Set<CustomizationLayer> dependents;

    public ColorSelection(
            final String name, final boolean anyColor,
            final Color... swatches
    ) {
        this.name = name;
        this.swatches = swatches == null || swatches.length == 0
                ? Colors.DEFAULT_SWATCHES : swatches;
        this.anyColor = anyColor;
        this.dependents = new HashSet<>();

        this.color = this.swatches[0];
    }

    public void addDependent(final CustomizationLayer dependent) {
        dependents.add(dependent);
    }

    public void randomize() {
        final int index = RNG.randomInRange(0, swatches.length);
        setColor(swatches[index]);
    }

    public void setColor(final Color color) {
        this.color = color;

        dependents.forEach(CustomizationLayer::update);
    }

    public Color getColor() {
        return color;
    }

    public Color[] getSwatches() {
        return swatches;
    }

    public boolean isAnyColor() {
        return anyColor;
    }
}
