package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.tdsm.util.Colors;

import java.awt.*;

public final class ColorSelection {
    private final Color[] swatches;
    private final boolean anyColor;

    private Color selection;

    public ColorSelection(final boolean anyColor, final Color... swatches) {
        this.swatches = swatches == null || swatches.length == 0
                ? Colors.DEFAULT_SWATCHES : swatches;
        this.anyColor = anyColor;

        this.selection = this.swatches[0];
    }

    public void setSelection(final Color selection) {
        this.selection = selection;

        // TODO - update dependents on this selection
    }

    public Color getSelection() {
        return selection;
    }

    public Color[] getSwatches() {
        return swatches;
    }

    public boolean isAnyColor() {
        return anyColor;
    }
}
