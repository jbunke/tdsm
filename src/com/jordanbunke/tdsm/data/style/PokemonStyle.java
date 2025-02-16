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
import java.util.Set;
import java.util.function.Function;

import static com.jordanbunke.tdsm.util.Colors.*;

public final class PokemonStyle extends Style {
    private static final PokemonStyle INSTANCE;

    private static final String ID = "pkmn";
    private static final Bounds2D DIMS = new Bounds2D(32, 32);

    private AssetChoiceLayer bodyLayer;

    static {
        INSTANCE = new PokemonStyle();
    }

    private PokemonStyle() {
        super(ID, DIMS, setUpDirections(), setUpAnimations(), new Layers());

        bodyLayer = null;

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
                Animation.make("idle", 1, new Coord2D(),
                        false, PlaybackMode.LOOP),
        };
    }

    private void setUpLayers() {
        final ColorSelection skinTones = new ColorSelection(
                "Skin", true, new Color(0xf8, 0xd0, 0xb8));
        final ColorSelectionLayer skinLayer = new ColorSelectionLayer(
                "skin", "Skin Color", skinTones);

        bodyLayer = ACLBuilder.of("body", this,
                        new AssetChoiceTemplate("average-body",
                                this::skinReplacement),
                        new AssetChoiceTemplate("small-body",
                                this::skinReplacement)).build();
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
        final Color baseSkin = new Color(0xb8f8b8);
        final double highestVal = rgbToValue(baseSkin);

        final Set<Color> SKIN = Set.of(
                baseSkin,
                new Color(0x98e898),
                new Color(0x70d870)),
                SKIN_OUTLINES = Set.of(
                        new Color(0x557840),
                        new Color(0x364030));

        final boolean isSkin = SKIN.contains(input),
                isOutline = SKIN_OUTLINES.contains(input);
        final int index = isSkin || isOutline ? 0 : -1;

        return new Pair<>(index, c -> {
            final double ih = rgbToHue(input),
                    is = rgbToSat(input), iv = rgbToValue(input),
                    ch = rgbToHue(c), cs = rgbToSat(c),
                    cv = rgbToValue(c);

            if (isSkin) {
                // TODO


                return fromHSV(ch, is, iv);
            }

            // TODO - is outline
            final double hueDiff = rgbToHue(baseSkin) - ih,
                    hue = normalizeHue(ch - hueDiff);

            return fromHSV(hue, is, iv);
        });
    }

    private SpriteConstituent<String> composeHead(final SpriteSheet sheet) {
        return new InterpretedSpriteSheetWithOffset<>(sheet, id -> {
            final Directions.Dir dir = Directions.get(
                    SpriteStates.extractContributor(DIRECTION, id));
            return new Coord2D(indexOfDir(dir), 0);
        }, id -> {
            final String animID =
                    SpriteStates.extractContributor(ANIM, id);
            final int frame = Integer.parseInt(
                    SpriteStates.extractContributor(FRAME, id));

            final int bodyComp = getBodyLayerChoice(), frameComp;

            frameComp = switch (animID) {
                case "walk" -> frame == 1 ? 1 : 0;
                case "idle" -> 1;
                default -> 0;
            };

            return new Coord2D(0, bodyComp + frameComp);
        });
    }

    private int getBodyLayerChoice() {
        if (bodyLayer == null)
            return 0;

        return bodyLayer.getChoiceIndex();
    }
}
