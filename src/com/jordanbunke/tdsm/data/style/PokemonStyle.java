package com.jordanbunke.tdsm.data.style;

import com.jordanbunke.delta_time.sprite.SpriteSheet;
import com.jordanbunke.delta_time.sprite.SpriteStates;
import com.jordanbunke.delta_time.sprite.constituents.InterpretedSpriteSheet;
import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.data.Animation;
import com.jordanbunke.tdsm.data.Directions;
import com.jordanbunke.tdsm.data.Directions.Dir;
import com.jordanbunke.tdsm.data.Directions.NumDirs;
import com.jordanbunke.tdsm.data.layer.*;

import java.util.function.Function;

public final class PokemonStyle extends Style {
    private static final PokemonStyle INSTANCE;

    private static final int DIRECTION = 0, ANIM = 1, FRAME = 2;

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
                        vertical, Animation.PlaybackMode.PONG),
                Animation.make("walk", 3, new Coord2D(0, 1),
                        vertical, Animation.PlaybackMode.PONG),
                // TODO
        };
    }

    private void setUpLayers() {
        final Function<SpriteSheet, SpriteConstituent<String>>
                DEFAULT_COMPOSER_BUILDER = sheet ->
                new InterpretedSpriteSheet<>(sheet, id -> {
                    final Dir dir = Dir.valueOf(
                            SpriteStates.extractContributor(DIRECTION, id)
                                    .toUpperCase());
                    final String anim =
                            SpriteStates.extractContributor(ANIM, id);
                    final int frame = Integer.parseInt(
                            SpriteStates.extractContributor(FRAME, id));

                    final int x = indexOfDir(dir),
                            y = startingIndexForAnim(anim) + frame;

                    return new Coord2D(x, y);
                });

        // TODO - temp
        layers.get().add(new AssetChoiceLayer(
                "body", this, new AssetChoiceTemplate[] {
                        new AssetChoiceTemplate("body-temp",
                                new ColorSelection[0],
                                ColorReplacementFunc.trivial())
                }, DEFAULT_COMPOSER_BUILDER));
    }

    @Override
    public String name() {
        return "PKMN Gen IV";
    }
}
