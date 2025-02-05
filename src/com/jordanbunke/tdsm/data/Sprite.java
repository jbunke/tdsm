package com.jordanbunke.tdsm.data;

import com.jordanbunke.tdsm.data.style.Style;
import com.jordanbunke.tdsm.data.style.Styles;

public final class Sprite {
    private static Sprite INSTANCE;

    private final Style style;

    // sprite info

    private String playbackAnim;
    // TODO - playback

    static {
        INSTANCE = new Sprite(Styles.PKMN.get());
    }

    private Sprite(final Style style) {
        this.style = style;
    }

    public static Sprite get() {
        return INSTANCE;
    }

    public void setStyle(final Style style) {
        if (!style.equals(this.style))
            INSTANCE = new Sprite(style);
    }
}
