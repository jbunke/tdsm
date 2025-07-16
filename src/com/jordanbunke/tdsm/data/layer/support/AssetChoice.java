package com.jordanbunke.tdsm.data.layer.support;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.tdsm.data.func.ColorReplacementFunc;
import com.jordanbunke.tdsm.data.layer.CustomizationLayer;
import com.jordanbunke.tdsm.util.Colors;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class AssetChoice {
    public final String id, name;
    private final CustomizationLayer layer;

    private final ColorSelection[] colorSelections;
    public final ColorReplacementFunc colorReplacementFunc;

    private final GameImage asset;
    private GameImage render;

    AssetChoice(
            final String id, final String name,
            final GameImage asset,
            final CustomizationLayer layer,
            final ColorSelection[] colorSelections,
            final ColorReplacementFunc colorReplacementFunc
    ) {
        this.id = id;
        this.name = name;
        this.layer = layer;
        this.colorSelections = colorSelections;
        this.colorReplacementFunc = colorReplacementFunc;

        this.asset = asset;
        redraw();
    }

    public void randomize() {
        for (ColorSelection colorSelection : colorSelections)
            colorSelection.randomize(false);
    }

    public GameImage retrieve() {
        return render;
    }

    public ColorSelection[] allInfluencingColorSelections() {
        final List<ColorSelection> selections =
                new ArrayList<>(Arrays.stream(colorSelections).toList());

        selections.addAll(layer.getInfluencingSelections());

        return selections.toArray(ColorSelection[]::new);
    }

    public void redraw() {
        final Color[] colors = Arrays.stream(allInfluencingColorSelections())
                .map(ColorSelection::getColor).toArray(Color[]::new);

        if (colors.length == 0) {
            render = new GameImage(asset);
            return;
        }

        render = Colors.runColorReplacement(asset, colors, colorReplacementFunc);
    }

    public ColorSelection[] getColorSelections() {
        return colorSelections;
    }

    @Override
    public String toString() {
        return id;
    }
}
