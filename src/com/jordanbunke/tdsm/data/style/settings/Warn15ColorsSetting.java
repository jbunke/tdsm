package com.jordanbunke.tdsm.data.style.settings;

import com.jordanbunke.color_proc.ColorAlgo;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.MenuBuilder;
import com.jordanbunke.delta_time.sprite.SpriteAssembler;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.data.style.Style;
import com.jordanbunke.tdsm.util.Colors;
import com.jordanbunke.tdsm.util.Constants;
import com.jordanbunke.tdsm.util.ResourceCodes;
import com.jordanbunke.tdsm.util.hardware.GBAUtils;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public final class Warn15ColorsSetting extends StyleSetting {
    private final Map<Color, Color> replacementMap;
    private Color selectedToReplace;

    public Warn15ColorsSetting(final Style style) {
        super(style, "Warn if sprite contains more than 15 colors",
                ResourceCodes.WARN_ROM_15_COLS);

        replacementMap = new HashMap<>();
        selectedToReplace = null;
    }

    @Override
    void considerations(final SpriteAssembler<String, String> assembler) {}

    @Override
    boolean hasPreExportStep() {
        if (!get())
            return false;

        return Colors.colorOccurrences(style).size() >
                Constants.GBA_SPRITE_COL_LIMIT;
    }

    @Override
    void resetPreExport() {
        replacementMap.clear();
        selectedToReplace = null;
    }

    @Override
    void buildPreExportMenu(final MenuBuilder mb, final Coord2D warningPos) {
        GBAUtils.buildReplacementMenu(mb, style, replacementMap,
                () -> selectedToReplace, c -> selectedToReplace = c,
                warningPos);
    }

    @Override
    GameImage preExportTransform(final GameImage input) {
        if (replacementMap.isEmpty())
            return input;

        return ColorAlgo.run(c -> replacementMap.getOrDefault(c, c), input);
    }
}
