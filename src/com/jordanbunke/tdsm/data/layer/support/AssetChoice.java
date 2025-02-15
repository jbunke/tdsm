package com.jordanbunke.tdsm.data.layer.support;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.ResourceLoader;
import com.jordanbunke.delta_time.utility.math.Pair;
import com.jordanbunke.tdsm.data.func.ColorReplacementFunc;
import com.jordanbunke.tdsm.data.layer.AssetChoiceLayer;
import com.jordanbunke.tdsm.data.style.Style;
import com.jordanbunke.tdsm.util.Constants;

import java.awt.*;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.function.Function;

public final class AssetChoice {
    public final String id, name;
    private final Style style;
    private final AssetChoiceLayer layer;

    private final ColorSelection[] colorSelections;
    private final ColorReplacementFunc colorReplacementFunc;

    private final GameImage asset;
    private GameImage render;

    AssetChoice(
            final String id, final String name,
            final Style style,
            final AssetChoiceLayer layer,
            final ColorSelection[] colorSelections,
            final ColorReplacementFunc colorReplacementFunc
    ) {
        this.id = id;
        this.name = name;
        this.style = style;
        this.layer = layer;
        this.colorSelections = colorSelections;
        this.colorReplacementFunc = colorReplacementFunc;

        this.asset = fetchAsset();
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

        final int w = asset.getWidth(), h = asset.getHeight();
        final Map<Color, Color> replacements = new HashMap<>();
        render = new GameImage(w, h);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                final Color c = asset.getColorAt(x, y);

                if (replacements.containsKey(c))
                    render.setRGB(x, y, replacements.get(c).getRGB());
                else {
                    final Pair<Integer, Function<Color, Color>> out =
                            colorReplacementFunc.apply(c);
                    final int index = out.a();

                    if (index < 0 || index >= colors.length)
                        render.setRGB(x, y, c.getRGB());
                    else {
                        final Color set = out.b().apply(colors[index]);
                        replacements.put(c, set);
                        render.setRGB(x, y, set.getRGB());
                    }
                }
            }
        }

        render.free();
    }

    private GameImage fetchAsset() {
        final Path filepath = Constants.ASSET_ROOT_FOLDER
                .resolve(Path.of(style.id, layer.id, id + ".png"));

        return ResourceLoader.loadImageResource(filepath);
    }

    public ColorSelection[] getColorSelections() {
        return colorSelections;
    }

    @Override
    public String toString() {
        return id;
    }
}
