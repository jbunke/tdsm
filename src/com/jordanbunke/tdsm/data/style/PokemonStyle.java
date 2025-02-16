package com.jordanbunke.tdsm.data.style;

import com.jordanbunke.delta_time.sprite.SpriteSheet;
import com.jordanbunke.delta_time.sprite.SpriteStates;
import com.jordanbunke.delta_time.sprite.constituents.InterpretedSpriteSheetWithOffset;
import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.delta_time.utility.math.Pair;
import com.jordanbunke.tdsm.data.Animation;
import com.jordanbunke.tdsm.data.Animation.PlaybackMode;
import com.jordanbunke.tdsm.data.Directions;
import com.jordanbunke.tdsm.data.Directions.Dir;
import com.jordanbunke.tdsm.data.Directions.NumDirs;
import com.jordanbunke.tdsm.data.layer.AssetChoiceLayer;
import com.jordanbunke.tdsm.data.layer.ColorSelectionLayer;
import com.jordanbunke.tdsm.data.layer.Layers;
import com.jordanbunke.tdsm.data.layer.builders.ACLBuilder;
import com.jordanbunke.tdsm.data.layer.support.AssetChoiceTemplate;
import com.jordanbunke.tdsm.data.layer.support.ColorSelection;

import java.awt.*;
import java.util.function.Function;

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
                Dir.DOWN, Dir.LEFT, Dir.RIGHT, Dir.UP);
    }

    private static Animation[] setUpAnimations() {
        return new Animation[] {
                Animation.make("walk", 3, i -> {
                    final int x = 0;
                    final int y = switch (i) {
                        case 0 -> 1;
                        case 1 -> 0;
                        default -> 2;
                    };
                    return new Coord2D(x, y);
                }, PlaybackMode.PONG),
        };
    }

    private void setUpLayers() {
        final ColorSelection skinTones = new ColorSelection(
                "Skin", true, new Color(0xf8, 0xd0, 0xb8));
        final ColorSelectionLayer skinLayer = new ColorSelectionLayer(
                "skin", "Skin Color", skinTones);

        final AssetChoiceLayer bodyLayer = ACLBuilder.of(
                "body", this,
                new AssetChoiceTemplate("small-body",
                        this::skinReplacement),
                new AssetChoiceTemplate("average-body",
                        this::skinReplacement))
                .build();
        bodyLayer.addInfluencingSelection(skinTones);

        final AssetChoiceLayer headLayer = ACLBuilder.of(
                "head", this,
                new AssetChoiceTemplate("oval-head",
                        this::skinReplacement),
                new AssetChoiceTemplate("round-head",
                        this::skinReplacement))
                .setComposer(this::composeHead)
                .build();
        headLayer.addInfluencingSelection(skinTones);

        // TODO - temp
        layers.add(
                skinLayer, bodyLayer, headLayer
        );
    }

    @Override
    public String name() {
        return "Pokemon Gen IV"; // TODO - with é
    }

    private Pair<Integer, Function<Color, Color>> skinReplacement(
            final Color input
    ) {
        // TODO

        return new Pair<>(-1, c -> c);
    }

    private SpriteConstituent<String> composeHead(final SpriteSheet sheet) {
        return new InterpretedSpriteSheetWithOffset<>(sheet, id -> {
            final Directions.Dir dir = Directions.get(
                    SpriteStates.extractContributor(DIRECTION, id));
            return new Coord2D(indexOfDir(dir), 0);
        }, id -> {
            // TODO
            return new Coord2D();
        });
    }
}
