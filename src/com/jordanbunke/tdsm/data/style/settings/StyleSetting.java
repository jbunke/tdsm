package com.jordanbunke.tdsm.data.style.settings;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.MenuBuilder;
import com.jordanbunke.delta_time.sprite.SpriteAssembler;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.data.style.Style;

public abstract class StyleSetting {
    public final String description, infoCode;
    final Style style;
    private boolean flag;

    public StyleSetting(
            final Style style, final String description, final String infoCode
    ) {
        this.style = style;
        this.description = description;
        this.infoCode = infoCode;

        flag = false;
    }

    public void set(final boolean value) {
        flag = value;
    }

    public boolean get() {
        return flag;
    }

    abstract void considerations(final SpriteAssembler<String, String> assembler);

    abstract boolean hasPreExportStep();

    abstract void resetPreExport();

    abstract void buildPreExportMenu(
            final MenuBuilder mb, final Coord2D warningPos
    );

    GameImage preExportTransform(final GameImage input) {
        return input;
    }
}
