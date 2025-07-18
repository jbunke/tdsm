package com.jordanbunke.tdsm.data.style;

import com.jordanbunke.delta_time.scripting.ast.collection.ScriptArray;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.tdsm.data.Animation;
import com.jordanbunke.tdsm.data.Directions;
import com.jordanbunke.tdsm.data.layer.CustomizationLayer;
import com.jordanbunke.tdsm.data.layer.Layers;
import com.jordanbunke.tdsm.data.style.settings.StyleSettings;
import com.jordanbunke.tdsm.util.Layout;

public final class FromFileStyle extends Style {
    public static final String NAME = "name", INFO_TOOLTIP = "info",
            SETTINGS = "settings", PREVIEW_SCALE = "preview_scale";

    public static final String DEF_NAME = "Uploaded", DEF_TOOLTIP = "";
    public static final ScriptArray DEF_SETTINGS = ScriptArray.of();

    private String name = DEF_NAME, infoTooltip = DEF_TOOLTIP;
    private int previewScale = Layout.SPRITE_PREVIEW_SCALE_UP;

    /**
     * For use by scripting function <code>$Init.style(
     *      string id,
     *      int[] bounds,
     *      string[] directions,
     *      bool anim_orientation,
     *      anim[] animations,
     *      {bool : layer<>} layers
     * )</code>
     * */
    @SuppressWarnings("unused")
    public FromFileStyle(
            final String id, final Bounds2D dims, final Directions directions,
            final Animation[] animations, final CustomizationLayer[] custom,
            final CustomizationLayer[] assembly
    ) {
        super(id, dims, directions, animations, new Layers());

        populateLayers(custom, assembly);
        update();
    }

    private void populateLayers(
            final CustomizationLayer[] custom,
            final CustomizationLayer[] assembly
    ) {
        layers.addToCustomization(custom);
        layers.addToAssembly(assembly);
    }

    public String infoToolTip() {
        return infoTooltip;
    }

    public void setInfoTooltip(final String infoTooltip) {
        this.infoTooltip = infoTooltip;
    }

    @Override
    public String name() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public int getPreviewScaleUp() {
        return previewScale;
    }

    public void setPreviewScale(final int previewScale) {
        this.previewScale = previewScale;
    }

    public void setSettings(final ScriptArray scriptArray) {
        final String[] codes = scriptArray.stream()
                .map(String::valueOf).filter(StyleSettings::validCode)
                .distinct().sorted().toArray(String[]::new);

        for (String code : codes)
            settings.add(this, code);
    }

    @Override
    public boolean shipping() {
        return true;
    }
}
