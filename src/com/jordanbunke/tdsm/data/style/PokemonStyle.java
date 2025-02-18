package com.jordanbunke.tdsm.data.style;

import com.jordanbunke.delta_time.sprite.SpriteSheet;
import com.jordanbunke.delta_time.sprite.SpriteStates;
import com.jordanbunke.delta_time.sprite.constituents.InterpretedSpriteSheetWithOffset;
import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.delta_time.utility.math.MathPlus;
import com.jordanbunke.delta_time.utility.math.Pair;
import com.jordanbunke.tdsm.data.Animation;
import com.jordanbunke.tdsm.data.Animation.PlaybackMode;
import com.jordanbunke.tdsm.data.Directions;
import com.jordanbunke.tdsm.data.Directions.Dir;
import com.jordanbunke.tdsm.data.Directions.NumDirs;
import com.jordanbunke.tdsm.data.layer.*;
import com.jordanbunke.tdsm.data.layer.builders.ACLBuilder;
import com.jordanbunke.tdsm.data.layer.builders.MLBuilder;
import com.jordanbunke.tdsm.data.layer.support.AssetChoiceTemplate;
import com.jordanbunke.tdsm.data.layer.support.ColorSelection;
import com.jordanbunke.tdsm.data.layer.support.NoAssetChoice;

import java.awt.*;
import java.util.Set;
import java.util.function.Function;

import static com.jordanbunke.tdsm.util.Colors.*;

public final class PokemonStyle extends Style {
    private static final PokemonStyle INSTANCE;

    private static final String ID = "pkmn";
    private static final Bounds2D DIMS = new Bounds2D(32, 32);

    private static final Set<Color>
            SKIN, SKIN_OUTLINES, HAIR, IRIS, EYE_WHITE,
            CLOTH_1, CLOTH_2, CLOTH_3, CLOTH_4;
    private static final Color
            BASE_SKIN, BASE_HAIR, BASE_IRIS, BASE_EYE_WHITE,
            BASE_CLOTH_1, BASE_CLOTH_2, BASE_CLOTH_3, BASE_CLOTH_4;

    private AssetChoiceLayer bodyLayer, hatLayer;
    private final MathLayer eyeLevelLayer;
    private final ChoiceLayer clothingTypeLayer;

    static {
        BASE_SKIN = new Color(0xb8f8b8);
        SKIN = Set.of(BASE_SKIN,
                new Color(0x98e898),
                new Color(0x70d870));
        SKIN_OUTLINES = Set.of(
                new Color(0x557840),
                new Color(0x364030));

        BASE_HAIR = new Color(0xb0b0f8);
        HAIR = Set.of(BASE_HAIR,
                new Color(0x8080f0),
                new Color(0x4848c8),
                new Color(0x303070));

        BASE_IRIS = new Color(0xff0000);
        IRIS = Set.of(BASE_IRIS,
                new Color(0x884848));

        BASE_EYE_WHITE = new Color(0x00ffff);
        EYE_WHITE = Set.of(BASE_EYE_WHITE,
                new Color(0xa8d8d8));

        BASE_CLOTH_1 = new Color(0xf08080);
        BASE_CLOTH_2 = new Color(0xf0b880);
        BASE_CLOTH_3 = new Color(0xf0f080);
        BASE_CLOTH_4 = new Color(0xb8f080);

        CLOTH_1 = Set.of(BASE_CLOTH_1,
                new Color(0xf8b0b0),
                new Color(0xc84848),
                new Color(0x703030));
        CLOTH_2 = Set.of(BASE_CLOTH_2,
                new Color(0xf8d4b0),
                new Color(0xc88848),
                new Color(0x705030));
        CLOTH_3 = Set.of(BASE_CLOTH_3,
                new Color(0xf8f8b0),
                new Color(0xc8c848),
                new Color(0x707030));
        CLOTH_4 = Set.of(BASE_CLOTH_4,
                new Color(0xd4f8b0),
                new Color(0x88c848),
                new Color(0x507030));

        INSTANCE = new PokemonStyle();
    }

    private PokemonStyle() {
        super(ID, DIMS, setUpDirections(), setUpAnimations(), new Layers());

        bodyLayer = null;
        hatLayer = null;
        eyeLevelLayer = new MathLayer("eye-level", -1, 1, 0,
                i -> switch (i) {
                    case -1 -> "Low";
                    case 1 -> "High";
                    default -> "Average";
                });
        clothingTypeLayer = new ChoiceLayer("clothing-type",
                "Top and bottom", "Outfit");

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
        return new Animation[]{
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
        final Color[] skinSwatches = new Color[] {
                new Color(0xf8d0b8),
                new Color(0xa88050),
                new Color(0xc89060),
                new Color(0xf8e0b8),
                new Color(0x986860),
                new Color(0x986840),
                new Color(0x58402e),
        };
        final Color[] hairSwatches = new Color[] {
                new Color(0x404040),
                new Color(0x342820),
                new Color(0x684828),
                new Color(0x82662d),
                new Color(0xb2864b),
                new Color(0xc4b880),
                new Color(0xdedbb8),
                new Color(0x888480),
                new Color(0xc4beb8)
        };
        final Color[] irisSwatches = new Color[] {
                black(),
                new Color(0x402016),
                new Color(0x7c6424),
                new Color(0x506c32),
                new Color(0x70207c),
                new Color(0x70a0c0)
        };
        final Color[] clothesSwatches = new Color[] {
                new Color(0xcbcbce),
                new Color(0x383838),
                new Color(0xe06040),
                new Color(0x4060e0),
                new Color(0xa040e0),
                new Color(0x309ea4),
                new Color(0xc0709c),
                new Color(0x70c070),
                new Color(0xf8b020),
                new Color(0x784040),
                new Color(0x609038),
                new Color(0x989090)
        };

        final ColorSelection skinTones = new ColorSelection(
                "Skin", true, skinSwatches),
                hairColors = new ColorSelection(
                        "Hair", true, hairSwatches),
                eyebrowColors = new ColorSelection(
                        "Brows", true, hairSwatches),
                irisColors = new ColorSelection(
                        "Iris", true, irisSwatches),
                ewColors = new ColorSelection(
                        "Outer", true, new Color(0xe8e8f8)),
                hat1 = new ColorSelection("Main", true, clothesSwatches),
                hat2 = new ColorSelection("Accent", true, clothesSwatches),
                hat3 = new ColorSelection("3rd", true, clothesSwatches),
                hat4 = new ColorSelection("4th", true, clothesSwatches);

        final ColorSelectionLayer skinLayer = new ColorSelectionLayer(
                "skin", "Skin Color", skinTones);

        bodyLayer = ACLBuilder.of("body", this,
                new AssetChoiceTemplate("average-body",
                        this::replace),
                new AssetChoiceTemplate("small-body",
                        this::replace)).build();
        bodyLayer.addInfluencingSelection(skinTones);

        final AssetChoiceLayer headLayer = ACLBuilder.of(
                        "head", this,
                        new AssetChoiceTemplate("oval-head", this::replace),
                        new AssetChoiceTemplate("round-head", this::replace))
                .setComposer(this::composeHead)
                .build();
        headLayer.addInfluencingSelection(skinTones);

        final AssetChoiceLayer eyeLayer = ACLBuilder.of(
                        "eyes", this,
                        new AssetChoiceTemplate("determined", this::replace),
                        new AssetChoiceTemplate("hooded", this::replace),
                        new AssetChoiceTemplate("soft", this::replace),
                        new AssetChoiceTemplate("vacant", this::replace),
                        new AssetChoiceTemplate("narrow", this::replace),
                        new AssetChoiceTemplate("menacing", this::replace),
                        new AssetChoiceTemplate("feminine", this::replace),
                        new AssetChoiceTemplate("cranky", this::replace))
                .setComposer(this::composeEyes)
                .build();
        eyeLayer.addInfluencingSelection(skinTones);
        eyeLayer.addInfluencingSelection(eyebrowColors);
        eyeLayer.addInfluencingSelection(irisColors);
        eyeLayer.addInfluencingSelection(ewColors);

        final ColorSelectionLayer eyeColorLayer = new ColorSelectionLayer(
                "eye-color", irisColors, ewColors),
                hairColorLayer = new ColorSelectionLayer(
                        "hair-color", hairColors, eyebrowColors);

        final AssetChoiceLayer hairLayer = ACLBuilder.of(
                        "hair", this,
                        new AssetChoiceTemplate("dragon-master", this::replace),
                        new AssetChoiceTemplate("nest", this::replace),
                        new AssetChoiceTemplate("professional", this::replace),
                        new AssetChoiceTemplate("crew-cut", this::replace),
                        new AssetChoiceTemplate("dainty", this::replace /* TODO */),
                        new AssetChoiceTemplate("flared-curtains", this::replace),
                        new AssetChoiceTemplate("geezer", this::replace),
                        new AssetChoiceTemplate("mop", this::replace),
                        new AssetChoiceTemplate("outta-my-face", this::replace /* TODO */),
                        new AssetChoiceTemplate("pigtails", this::replace /* TODO */),
                        new AssetChoiceTemplate("rocker", this::replace),
                        new AssetChoiceTemplate("serene", this::replace /* TODO */),
                        new AssetChoiceTemplate("porcupine", this::replace),
                        new AssetChoiceTemplate("mane", this::replace),
                        new AssetChoiceTemplate("receding", this::replace),
                        new AssetChoiceTemplate("heli-pad", this::replace),
                        new AssetChoiceTemplate("cowlick", this::replace),
                        new AssetChoiceTemplate("magnate", this::replace),
                        new AssetChoiceTemplate("silver-fox", this::replace),
                        new AssetChoiceTemplate("high-ponytail", this::replace),
                        new AssetChoiceTemplate("chic", this::replace))
                .setComposer(this::composeHead).setName("Hairstyle")
                .setNoAssetChoice(NoAssetChoice.equal())
                .build();
        hairLayer.addInfluencingSelection(skinTones);
        hairLayer.addInfluencingSelection(hairColors);

        // TODO - add more
        hatLayer = ACLBuilder.of("hat", this,
                        new AssetChoiceTemplate("fitted-front",
                                this::clothesReplace, hat1, hat2),
                        new AssetChoiceTemplate("fitted-back",
                                this::clothesReplace, hat1, hat2),
                        new AssetChoiceTemplate("fedora",
                                this::clothesReplace, hat1, hat2))
                .setName("Headwear").setComposer(this::composeHead)
                .setNoAssetChoice(NoAssetChoice.prob(0.75)).build();

        final MaskLayer hatMaskLayer = MLBuilder.init("hat-mask", hairLayer)
                .trySetNaiveLogic(this, hatLayer).build();

        // TODO - still assembling
        layers.add(
                skinLayer, bodyLayer, headLayer,
                eyeLayer, eyeLevelLayer, eyeColorLayer,
                hairLayer, hairColorLayer,
                clothingTypeLayer, hatLayer, hatMaskLayer
        );
    }

    @Override
    public String name() {
        return "Pokémon Trainer [Gen 4]";
    }

    private Pair<Integer, Function<Color, Color>> clothesReplace(
            final Color input
    ) {
        final Color base;
        final int index;

        if (CLOTH_1.contains(input)) {
            index = 0;
            base = BASE_CLOTH_1;
        } else if (CLOTH_2.contains(input)) {
            index = 1;
            base = BASE_CLOTH_1;
        } else if (CLOTH_3.contains(input)) {
            index = 2;
            base = BASE_CLOTH_1;
        } else if (CLOTH_4.contains(input)) {
            index = 3;
            base = BASE_CLOTH_1;
        } else {
            index = -1;
            base = black();
        }

        return new Pair<>(index, c -> {
            final double is = rgbToSat(input), iv = rgbToValue(input),
                    ch = rgbToHue(c), cs = rgbToSat(c), cv = rgbToValue(c),
                    bs = rgbToSat(base), bv = rgbToValue(base),
                    sRatio = (cs * is) / bs, vRatio = (cv * iv) / bv,
                    s = MathPlus.bounded(0.0, sRatio, 1.0),
                    v = MathPlus.bounded(0.0, vRatio, 1.0);

            return fromHSV(ch, s, v);
        });
    }

    private Pair<Integer, Function<Color, Color>> replace(
            final Color input
    ) {
        final boolean isSkin = SKIN.contains(input),
                isOutline = SKIN_OUTLINES.contains(input),
                isHair = HAIR.contains(input),
                isIris = IRIS.contains(input),
                isEW = EYE_WHITE.contains(input);
        final int index;

        if (isSkin || isOutline)
            index = 0;
        else if (isHair)
            index = 1;
        else if (isIris)
            index = 2;
        else if (isEW)
            index = 3;
        else
            index = -1;

        return new Pair<>(index, c -> {
            final double ih = rgbToHue(input),
                    is = rgbToSat(input), iv = rgbToValue(input),
                    ch = rgbToHue(c), cs = rgbToSat(c),
                    cv = rgbToValue(c);

            if (isSkin || isOutline) {
                final double bs = rgbToSat(BASE_SKIN),
                        bv = rgbToValue(BASE_SKIN);

                if (isSkin) {
                    // Skin
                    final double sRatio = (cs * is) / bs,
                            vRatio = (cv * iv) / bv,
                            s = MathPlus.bounded(0.0, sRatio, 1.0),
                            v = MathPlus.bounded(0.0, vRatio, 1.0);

                    return fromHSV(ch, s, v);
                } else {
                    // Skin outline
                    final double hueDiff = rgbToHue(BASE_SKIN) - ih,
                            hue = normalizeHue(ch - hueDiff);

                    final double sRatio = (cs * is) / bs,
                            vRatio = (cv * iv) / bv,
                            s = MathPlus.bounded(0.0, sRatio, 1.0),
                            v = MathPlus.bounded(0.0, vRatio, 1.0);

                    return fromHSV(hue, s, v);
                }
            } else if (isHair) {
                // Hair
                final double bs = rgbToSat(BASE_HAIR),
                        bv = rgbToValue(BASE_HAIR);

                final double sRatio = (cs * is) / bs,
                        vRatio = (cv * iv) / bv,
                        s = MathPlus.bounded(0.0, sRatio, 1.0),
                        v = MathPlus.bounded(0.0, vRatio, 1.0);

                return fromHSV(ch, s, v);
            } else if (isIris) {
                // Iris
                final double bs = rgbToSat(BASE_IRIS),
                        bv = rgbToValue(BASE_IRIS);

                final double sRatio = (cs * is) / bs,
                        vRatio = (cv * iv) / bv,
                        s = MathPlus.bounded(0.0, sRatio, 1.0),
                        v = MathPlus.bounded(0.0, vRatio, 1.0);

                return fromHSV(ch, s, v);
            } else {
                // Eye white
                final double bs = rgbToSat(BASE_EYE_WHITE),
                        bv = rgbToValue(BASE_EYE_WHITE);

                final double sRatio = (cs * is) / bs,
                        vRatio = (cv * iv) / bv,
                        s = MathPlus.bounded(0.0, sRatio, 1.0),
                        v = MathPlus.bounded(0.0, vRatio, 1.0);

                return fromHSV(ch, s, v);
            }
        });
    }

    private SpriteConstituent<String> composeEyes(
            final SpriteSheet sheet
    ) {
        return composeHead(sheet, -eyeLevelLayer.getValue());
    }

    private SpriteConstituent<String> composeHead(
            final SpriteSheet sheet
    ) {
        return composeHead(sheet, 0);
    }

    private SpriteConstituent<String> composeHead(
            final SpriteSheet sheet, final int augY
    ) {
        return new InterpretedSpriteSheetWithOffset<>(
                sheet, this::forDir, id -> headOffset(id, augY));
    }

    private Coord2D headOffset(final String spriteID, final int augY) {
        final String animID =
                SpriteStates.extractContributor(ANIM, spriteID);
        final int frame = Integer.parseInt(
                SpriteStates.extractContributor(FRAME, spriteID));

        final int bodyComp = getBodyLayerChoice(), frameComp;

        frameComp = switch (animID) {
            case "walk" -> frame == 1 ? 1 : 0;
            case "idle" -> 1;
            default -> 0;
        };

        return new Coord2D(0, bodyComp + frameComp + augY);
    }

    private Coord2D forDir(final String spriteID) {
        final Directions.Dir dir = Directions.get(
                SpriteStates.extractContributor(DIRECTION, spriteID));
        return new Coord2D(indexOfDir(dir), 0);
    }

    private int getBodyLayerChoice() {
        if (bodyLayer == null)
            return 0;

        return bodyLayer.getChoiceIndex();
    }
}
