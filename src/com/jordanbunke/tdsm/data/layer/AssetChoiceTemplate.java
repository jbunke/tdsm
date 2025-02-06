package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.tdsm.data.style.Style;
import com.jordanbunke.tdsm.util.StringUtils;

import java.awt.*;
import java.util.function.Function;

public final class AssetChoiceTemplate {
    public final String id, name;
    public final ColorSelection[] colorSelections;
    public final Function<Color, Integer> colorReplacementFunc;

    public AssetChoiceTemplate(
            final String id, final ColorSelection[] colorSelections,
            final Function<Color, Integer> colorReplacementFunc
    ) {
        this(id, StringUtils.nameFromID(id),
                colorSelections, colorReplacementFunc);
    }

    public AssetChoiceTemplate(
            final String id, final String name,
            final ColorSelection[] colorSelections,
            final Function<Color, Integer> colorReplacementFunc
    ) {
        this.id = id;
        this.name = name;
        this.colorSelections = colorSelections;
        this.colorReplacementFunc = colorReplacementFunc;
    }

    AssetChoice realize(final Style style, final AssetChoiceLayer layer) {
        return new AssetChoice(id, name, style, layer,
                colorSelections, colorReplacementFunc);
    }
}
