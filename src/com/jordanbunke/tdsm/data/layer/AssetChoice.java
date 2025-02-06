package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.ResourceLoader;
import com.jordanbunke.tdsm.data.style.Style;
import com.jordanbunke.tdsm.util.Constants;

import java.awt.*;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class AssetChoice {
    public final String id;
    private final Style style;
    private final String layerID;

    private final ColorSelection[] colorSelections;
    private final Function<Color, Integer> colorReplacementFunc;

    private final GameImage asset;
    private GameImage render;

    AssetChoice(
            final String id, final Style style, final String layerID,
            final ColorSelection[] colorSelections,
            final Function<Color, Integer> colorReplacementFunc
    ) {
        this.id = id;
        this.style = style;
        this.layerID = layerID;
        this.colorSelections = colorSelections;
        this.colorReplacementFunc = colorReplacementFunc;

        this.asset = fetchAsset();
        redraw();
    }

    public GameImage retrieve() {
        return render;
    }

    public void redraw() {
        if (colorSelections.length == 0) {
            render = new GameImage(asset);
            return;
        }

        final Color[] selections = Arrays.stream(colorSelections)
                .map(ColorSelection::getSelection).toArray(Color[]::new);

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

                    if (index < 0 || index >= selections.length)
                        render.setRGB(x, y, c.getRGB());
                    else {
                        final Color set = selections[index];
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
                .resolve(Path.of(style.id, layerID, id + ".png"));

        return ResourceLoader.loadImageResource(filepath);
    }

    @Override
    public String toString() {
        return id;
    }
}
