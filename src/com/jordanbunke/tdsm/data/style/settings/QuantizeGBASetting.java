package com.jordanbunke.tdsm.data.style.settings;

import com.jordanbunke.delta_time.menu.MenuBuilder;
import com.jordanbunke.delta_time.sprite.SpriteAssembler;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.data.style.Style;
import com.jordanbunke.tdsm.util.ResourceCodes;
import com.jordanbunke.tdsm.util.hardware.GBAUtils;

import java.awt.*;
import java.util.List;
import java.util.function.Function;

public final class QuantizeGBASetting extends StyleSetting {
    private final Function<Color, Color> quantizeFunc;

    public QuantizeGBASetting(final Style style) {
        super(style, "Quantize to Game Boy Advance colors",
                ResourceCodes.QUANTIZE_GBA);

        quantizeFunc = GBAUtils::quantize;
    }

    @Override
    void considerations(SpriteAssembler<String, String> assembler) {
        if (get()) {
            final String quantizeID = "quantize-to-palette";

            final List<String> layerIDs = assembler.getEnabledLayerIDs();

            for (String layerID : layerIDs)
                assembler.addFilter(quantizeID, quantizeFunc, layerID);
        }
    }

    @Override
    boolean hasPreExportStep() {
        return false;
    }

    @Override
    void resetPreExport() {}

    @Override
    void buildPreExportMenu(final MenuBuilder mb, final Coord2D warningPos) {}
}
