package com.jordanbunke.tdsm.data.style;

import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.data.Animation;
import com.jordanbunke.tdsm.data.Directions;
import com.jordanbunke.tdsm.data.Directions.*;
import com.jordanbunke.tdsm.data.layer.CustomizationLayer;

public final class PokemonStyle extends Style {
    private static final PokemonStyle INSTANCE;

    private static final String ID = "pkmn";
    private static final Bounds2D DIMS = new Bounds2D(32, 32);

    static {
        INSTANCE = new PokemonStyle();
    }

    private PokemonStyle() {
        super(ID, DIMS, setUpDirections(), setUpAnimations(), setUpLayers());
    }

    public static PokemonStyle get() {
        return INSTANCE;
    }

    private static Directions setUpDirections() {
        return new Directions(NumDirs.FOUR, true,
                Dir.DOWN, Dir.LEFT, Dir.UP, Dir.RIGHT);
    }

    private static Animation[] setUpAnimations() {
        final boolean vertical = false;

        return new Animation[] {
                Animation.make("idle", 3, new Coord2D(0, 0),
                        vertical, Animation.PlaybackMode.PONG),
                // TODO
        };
    }

    private static CustomizationLayer[] setUpLayers() {
        // TODO
        return null;
    }

    @Override
    public String name() {
        return "PKMN Gen IV";
    }
}
