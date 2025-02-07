package com.jordanbunke.tdsm.data;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.tdsm.data.style.Style;
import com.jordanbunke.tdsm.data.style.Styles;
import com.jordanbunke.tdsm.flow.screens.Customization;
import com.jordanbunke.tdsm.visual_misc.Playback;

public final class Sprite {
    private static Sprite INSTANCE;

    private final Style style;

    // sprite info
    private Directions.Dir previewDir;

    static {
        INSTANCE = new Sprite(Styles.PKMN.get());
        rebuildMenus();
    }

    private Sprite(final Style style) {
        this.style = style;
        setup();
    }

    public static Sprite get() {
        return INSTANCE;
    }

    private static void rebuildMenus() {
        Customization.rebuildMenu();
        // TODO
    }

    private void setup() {
        previewDir = this.style.directions.order()[0];
        Playback.get().setAnimation(this.style.animations[0]);
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
