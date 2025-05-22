package com.jordanbunke.tdsm.data;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.Pair;
import com.jordanbunke.tdsm.data.style.Style;
import com.jordanbunke.tdsm.data.style.pkmn.HokkaidoStyle;
import com.jordanbunke.tdsm.flow.screens.Configuration;
import com.jordanbunke.tdsm.flow.screens.Customization;
import com.jordanbunke.tdsm.menu.sampler.Sampler;
import com.jordanbunke.tdsm.visual_misc.Playback;

import java.util.List;

public final class Sprite {
    private static Sprite INSTANCE;

    private final Style style;

    // sprite info
    private Directions.Dir previewDir;

    static {
        INSTANCE = new Sprite(HokkaidoStyle.get());
        rebuildMenus();
    }

    private Sprite(final Style style) {
        this.style = style;
        setup();
    }

    public static Sprite get() {
        return INSTANCE;
    }

    public static void tap() {}

    private static void rebuildMenus() {
        Customization.get().rebuildMenu();
        Configuration.get().rebuildMenu();
        Sampler.get().close();
    }

    private void setup() {
        previewDir = this.style.directions.order()[0];
        Playback.get().setAnimation(this.style.animations[0]);
    }

    public GameImage renderSpriteSheet() {
        final GameImage spriteSheet = style.renderSpriteSheet();

        return style.settings.hasPreExportStep()
                ? style.settings.preExportTransform(spriteSheet)
                : spriteSheet;
    }

    public List<Pair<String, GameImage>> renderStipExport() {
        final List<Pair<String, GameImage>> stipRep = style.renderStipExport();

        if (style.settings.hasPreExportStep()) {
            for (int i = 0; i < stipRep.size(); i++) {
                final Pair<String, GameImage> layer = stipRep.get(i);

                stipRep.set(i, new Pair<>(layer.a(),
                        style.settings.preExportTransform(layer.b())));
            }
        }

        return stipRep;
    }

    public GameImage renderSprite() {
        return style.renderSprite(previewDir,
                Playback.get().getAnimation(), Playback.get().getFrame());
    }

    public void setStyle(final Style style) {
        if (!style.equals(this.style)) {
            INSTANCE = new Sprite(style);
            rebuildMenus();
        }
    }

    public Style getStyle() {
        return style;
    }

    public void turn(final boolean clockwise) {
        previewDir = clockwise
                ? previewDir.cw(style.directions.numDirs())
                : previewDir.ccw(style.directions.numDirs());
    }
}
