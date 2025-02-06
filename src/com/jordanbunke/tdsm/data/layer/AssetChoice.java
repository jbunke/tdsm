package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.ResourceLoader;
import com.jordanbunke.tdsm.data.style.Style;
import com.jordanbunke.tdsm.util.Constants;

import java.awt.*;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.function.Function;

public final class AssetChoice {
    public final String id;
    private final Style style;
    private final AssetChoiceLayer layer;

    private final ColorSelection[] colorSelections;
    private final Function<Color, Integer> colorReplacementFunc;

    private final GameImage asset;
    private GameImage render;

    AssetChoice(
            final String id, final Style style,
            final AssetChoiceLayer layer,
            final ColorSelection[] colorSelections,
            final Function<Color, Integer> colorReplacementFunc
    ) {
        this.id = id;
        this.style = style;
        this.layer = layer;
        this.colorSelections = colorSelections;
        this.colorReplacementFunc = colorReplacementFunc;

        this.asset = fetchAsset();
        redraw();
    }

    public void randomize() {
        for (ColorSelection colorSelection : colorSelections)
            colorSelection.randomize();
    }

    public GameImage retrieve() {
        return render;
    }

    public void redraw() {
        final List<Color> selections = new ArrayList<>();

        for (ColorSelection colorSelection : colorSelections)
            selections.add(colorSelection.getSelection());

        selections.addAll(
                layer.getInfluencingSelections().stream()
                        .map(ColorSelection::getSelection).toList());

        if (selections.size() == 0) {
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
                    final int index = colorReplacementFunc.apply(c);

                    if (index < 0 || index >= selections.size())
                        render.setRGB(x, y, c.getRGB());
                    else {
                        final Color set = selections.get(index);
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

    @Override
    public String toString() {
        return id;
    }
}
