package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.tdsm.data.style.Style;

import java.awt.*;
import java.util.function.Function;

public record AssetChoiceTemplate(
        String id, ColorSelection[] colorSelections,
        Function<Color, Integer> colorReplacementFunc
) {
    AssetChoice realize(final Style style, final AssetChoiceLayer layer) {
        return new AssetChoice(id, style, layer,
                colorSelections, colorReplacementFunc);
    }
}
