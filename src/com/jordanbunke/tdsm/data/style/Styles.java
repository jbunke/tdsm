package com.jordanbunke.tdsm.data.style;

public enum Styles {
    PKMN, TEMP;

    public Style get() {
        return switch (this) {
            case PKMN -> PokemonStyle.get();
            case TEMP -> null; // TODO
        };
    }
}
