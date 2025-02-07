package com.jordanbunke.tdsm.data.style;

public enum Styles {
    PKMN, VIGILANTE;

    public Style get() {
        return switch (this) {
            case PKMN -> PokemonStyle.get();
            case VIGILANTE -> VigilanteStyle.get();
        };
    }
}
