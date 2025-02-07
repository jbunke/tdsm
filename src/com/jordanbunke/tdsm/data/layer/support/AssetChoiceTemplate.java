package com.jordanbunke.tdsm.data.layer.support;

import com.jordanbunke.tdsm.data.func.ColorReplacementFunc;
import com.jordanbunke.tdsm.data.layer.AssetChoiceLayer;
import com.jordanbunke.tdsm.data.style.Style;
import com.jordanbunke.tdsm.util.StringUtils;

public final class AssetChoiceTemplate {
    public final String id, name;
    public final ColorSelection[] colorSelections;
    public final ColorReplacementFunc colorReplacementFunc;

    public AssetChoiceTemplate(final String id) {
        this(id, new ColorSelection[0], ColorReplacementFunc.trivial());
    }

    public AssetChoiceTemplate(
            final String id, final ColorSelection[] colorSelections,
            final ColorReplacementFunc colorReplacementFunc
    ) {
        this(id, StringUtils.nameFromID(id),
                colorSelections, colorReplacementFunc);
    }

    public AssetChoiceTemplate(
            final String id, final String name,
            final ColorSelection[] colorSelections,
            final ColorReplacementFunc colorReplacementFunc
    ) {
        this.id = id;
        this.name = name;
        this.colorSelections = colorSelections;
        this.colorReplacementFunc = colorReplacementFunc;
    }

    public AssetChoice realize(final Style style, final AssetChoiceLayer layer) {
        return new AssetChoice(id, name, style, layer,
                colorSelections, colorReplacementFunc);
    }
}
