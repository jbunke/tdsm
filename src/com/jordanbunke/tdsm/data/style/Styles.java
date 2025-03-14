package com.jordanbunke.tdsm.data.style;

import com.jordanbunke.tdsm.data.style.pkmn.HokkaidoStyle;
import com.jordanbunke.tdsm.data.style.pkmn.KyushuStyle;

public enum Styles {
    HOKKAIDO, KYUSHU, VIGILANTE;

    public Style get() {
        return switch (this) {
            case HOKKAIDO -> HokkaidoStyle.get();
            case KYUSHU -> KyushuStyle.get();
            case VIGILANTE -> VigilanteStyle.get();
        };
    }
}
