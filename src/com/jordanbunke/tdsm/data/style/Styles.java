package com.jordanbunke.tdsm.data.style;

import com.jordanbunke.tdsm.data.style.pkmn.HokkaidoStyle;
import com.jordanbunke.tdsm.data.style.pkmn.KyushuStyle;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Styles {
    private static final Map<String, Style> styles = new HashMap<>();

    static {
        final Style[] DEFAULTS = new Style[] {
                HokkaidoStyle.get(), KyushuStyle.get(), VigilanteStyle.get()
        };

        for (Style def : DEFAULTS)
            styles.put(def.id, def);
    }

    public static Style get(final String id) {
        return styles.getOrDefault(id, null);
    }

    public static Stream<Style> all() {
        return styles.keySet().stream().map(styles::get);
    }
}
