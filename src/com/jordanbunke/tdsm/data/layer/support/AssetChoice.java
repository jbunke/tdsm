package com.jordanbunke.tdsm.data.layer.support;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.ResourceLoader;
import com.jordanbunke.tdsm.data.func.ColorReplacementFunc;
import com.jordanbunke.tdsm.data.layer.CustomizationLayer;
import com.jordanbunke.tdsm.util.Colors;
import com.jordanbunke.tdsm.util.Constants;

import java.awt.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

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

    // TODO - remove when default styles have been converted to files
    @Deprecated
    public static Function<String, GameImage> tempDefGetter(
            final String styleID, final String layerID
    ) {
        return id -> {
            final Path filepath = Constants.ASSET_ROOT_FOLDER
                    .resolve(Path.of(styleID, layerID, id + ".png"));

            return ResourceLoader.loadImageResource(filepath);
        };
    }

    public ColorSelection[] getColorSelections() {
        return colorSelections;
    }

    @Override
    public String toString() {
        return id;
    }
}
