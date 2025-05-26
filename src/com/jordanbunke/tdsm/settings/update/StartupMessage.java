package com.jordanbunke.tdsm.settings.update;

import com.jordanbunke.delta_time.utility.Version;

public enum StartupMessage {
    POKEMON_SPRITES_NOW_SEPARATE_DOWNLOADS(new Version(1, 2, 0)),
    MAKE_EDIT_STYLES(new Version(1, 2, 0));

    public final Version since;

    StartupMessage(final Version since) {
        this.since = since;
    }

    public String id() {
        return name().toLowerCase();
    }
}
