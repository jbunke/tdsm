package com.jordanbunke.tdsm.data.style;

import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.data.Animation;
import com.jordanbunke.tdsm.data.Animation.PlaybackMode;
import com.jordanbunke.tdsm.data.Directions;
import com.jordanbunke.tdsm.data.Directions.Dir;
import com.jordanbunke.tdsm.data.Directions.NumDirs;
import com.jordanbunke.tdsm.data.layer.*;
import com.jordanbunke.tdsm.data.layer.builders.ACLBuilder;
import com.jordanbunke.tdsm.data.layer.support.AssetChoiceTemplate;

public final class PokemonStyle extends Style {
    private static final PokemonStyle INSTANCE;

    private static final String ID = "pkmn";
    private static final Bounds2D DIMS = new Bounds2D(32, 32);

    static {
        INSTANCE = new PokemonStyle();
    }

    private PokemonStyle() {
        super(ID, DIMS, setUpDirections(), setUpAnimations(), new Layers());

        setUpLayers();
        update();
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
                Animation.make("idle", 1, new Coord2D(0, 0),
                        vertical, PlaybackMode.LOOP),
                Animation.make("walk", 3,
                        i -> {
                    final int x = 0;
                    final int y = switch (i) {
                        case 0 -> 1;
                        case 1 -> 0;
                        default -> 2;
                    };
                    return new Coord2D(x, y);
                    }, PlaybackMode.PONG),
                // TODO
        };
    }

    private void setUpLayers() {
        // TODO - temp
        layers.add(
                ACLBuilder.of("body", this,
                                new AssetChoiceTemplate("body-temp"))
                        .build()
        );
    }

    @Override
    public String name() {
        return "PKMN Gen IV";
    }
}
