package com.jordanbunke.tdsm.data.layer.support;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.tdsm.data.func.ColorReplacementFunc;
import com.jordanbunke.tdsm.data.layer.CustomizationLayer;
import com.jordanbunke.tdsm.util.StringUtils;

import java.util.function.Function;

public final class AssetChoiceTemplate {
    public final String id, name;
    public final ColorSelection[] colorSelections;
    public final ColorReplacementFunc colorReplacementFunc;

    public AssetChoiceTemplate(final String id) {
        this(id, ColorReplacementFunc.trivial());
    }

    public AssetChoiceTemplate(
            final String id,
            final ColorReplacementFunc colorReplacementFunc,
            final ColorSelection... colorSelections
    ) {
        this(id, StringUtils.nameFromID(id),
                colorReplacementFunc, colorSelections);
    }

    public AssetChoiceTemplate(
            final String id, final String name,
            final ColorReplacementFunc colorReplacementFunc,
            final ColorSelection... colorSelections
    ) {
        this.id = id;
        this.name = name;
        this.colorSelections = colorSelections;
        this.colorReplacementFunc = colorReplacementFunc;
    }

    public AssetChoice realize(
            final Function<String, GameImage> getter,
            final CustomizationLayer layer
    ) {
        for (ColorSelection selection : colorSelections)
            selection.addDependent(layer);

        final GameImage asset = getter.apply(id);

        return new AssetChoice(id, name, asset, layer,
                colorSelections, colorReplacementFunc);
    }
}
