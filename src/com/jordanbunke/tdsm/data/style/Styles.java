package com.jordanbunke.tdsm.data.style;

public enum Styles {
    PKMN;

    public Style get() {
        return switch (this) {
            case PKMN -> PokemonStyle.get();
        };
    }
}
