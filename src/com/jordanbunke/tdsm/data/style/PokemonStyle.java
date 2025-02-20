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
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.IntStream;

import static com.jordanbunke.tdsm.util.Colors.*;

public final class PokemonStyle extends Style {
    private static final PokemonStyle INSTANCE;

    private static final String ID = "pkmn";
    private static final Bounds2D DIMS = new Bounds2D(32, 32);

    private final String COMBINED_OUTFIT = "Combined outfit";

    private static final Set<Color>
            SKIN, SKIN_OUTLINES, HAIR, IRIS, EYE_WHITE,
            CLOTH_1, CLOTH_2, CLOTH_3, CLOTH_4;
    private static final Color
            BASE_SKIN, BASE_HAIR, BASE_IRIS, BASE_EYE_WHITE,
            BASE_CLOTH_1, BASE_CLOTH_2, BASE_CLOTH_3, BASE_CLOTH_4;

    private static final Color[]
            SKIN_SWATCHES, HAIR_SWATCHES,
            IRIS_SWATCHES, CLOTHES_SWATCHES;

    private AssetChoiceLayer bodyLayer, hatLayer;
    private final MathLayer eyeHeightLayer;
    private final ChoiceLayer clothingTypeLayer;

    final ColorSelection skinTones, hairColors, eyebrowColors,
            irisColors, ewColors, hairAcc;
    final ColorSelection[] hatCS, topCS, botCS, shoeCS;

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

        SKIN_SWATCHES = new Color[] {
                new Color(0xf8d0b8),
                new Color(0xa88050),
                new Color(0xc89060),
                new Color(0xf8e0b8),
                new Color(0x986860),
                new Color(0x986840),
                new Color(0x58402e),
        };
        HAIR_SWATCHES = new Color[] {
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
        IRIS_SWATCHES = new Color[] {
                black(),
                new Color(0x402016),
                new Color(0x7c6424),
                new Color(0x506c32),
                new Color(0x70207c),
                new Color(0x70a0c0)
        };
        CLOTHES_SWATCHES = new Color[] {
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

        INSTANCE = new PokemonStyle();
    }

    private enum BodyType {
        AVERAGE, SMALL;

        final String prefix;

        BodyType() {
            prefix = name().substring(0, 2).toLowerCase();
        }
    }

    private PokemonStyle() {
        super(ID, DIMS, setUpDirections(), setUpAnimations(), new Layers());

        skinTones = new ColorSelection("Skin", true, SKIN_SWATCHES);
        hairColors = new ColorSelection("Hair", true, HAIR_SWATCHES);
        eyebrowColors = new ColorSelection("Brows", true, HAIR_SWATCHES);
        irisColors = new ColorSelection("Iris", true, IRIS_SWATCHES);
        ewColors = new ColorSelection("Outer", true, new Color(0xe8e8f8));
        hairAcc = new ColorSelection("Accessory", true, CLOTHES_SWATCHES);

        hatCS = IntStream.range(0, 4).mapToObj(this::clothesSwatch).toArray(ColorSelection[]::new);
        topCS = IntStream.range(0, 4).mapToObj(this::clothesSwatch).toArray(ColorSelection[]::new);
        botCS = IntStream.range(0, 4).mapToObj(this::clothesSwatch).toArray(ColorSelection[]::new);
        shoeCS = IntStream.range(0, 2).mapToObj(this::clothesSwatch).toArray(ColorSelection[]::new);

        bodyLayer = null;
        hatLayer = null;
        eyeHeightLayer = new MathLayer("eye-height", -1, 1, 0,
                i -> switch (i) {
                    case -1 -> "Low";
                    case 1 -> "High";
                    default -> "Average";
                });
        clothingTypeLayer = new ChoiceLayer("outfit-type",
                "Separate articles", COMBINED_OUTFIT);

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
        final ColorSelectionLayer skinLayer = new ColorSelectionLayer(
                "skin", "Skin Color", skinTones);

        bodyLayer = ACLBuilder.of("body", this,
                new AssetChoiceTemplate("average-body",
                        this::replace),
                new AssetChoiceTemplate("small-body",
                        this::replace)).setName("Body Type").build();
        bodyLayer.addInfluencingSelection(skinTones);

        final AssetChoiceLayer headLayer = ACLBuilder.of(
                        "head", this,
                        new AssetChoiceTemplate("oval-head", this::replace),
                        new AssetChoiceTemplate("round-head", this::replace))
                .setComposer(this::composeHead).setName("Head Shape")
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
                        new AssetChoiceTemplate("tired", this::replace),
                        new AssetChoiceTemplate("lashes", this::replace),
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

        final AssetChoiceLayer smOutfitLayer = buildOutfit(BodyType.SMALL),
                smTopsLayer = buildTop(BodyType.SMALL),
                smBottomsLayer = buildBottom(BodyType.SMALL),
                smShoesLayer = buildShoes(BodyType.SMALL);
        final GroupLayer smArticlesLayer = new GroupLayer(
                "outfit", "Outfit", smShoesLayer,
                smBottomsLayer, smTopsLayer);

        final AssetChoiceLayer avOutfitLayer = buildOutfit(BodyType.AVERAGE),
                avTopsLayer = buildTop(BodyType.AVERAGE),
                avBottomsLayer = buildBottom(BodyType.AVERAGE),
                avShoesLayer = buildShoes(BodyType.AVERAGE);
        final GroupLayer avArticlesLayer = new GroupLayer(
                "outfit", "Outfit", avShoesLayer,
                avBottomsLayer, avTopsLayer);

        AssetChoiceLayer.parallelMatchers(smOutfitLayer, avOutfitLayer);
        AssetChoiceLayer.parallelMatchers(smBottomsLayer, avBottomsLayer);
        AssetChoiceLayer.parallelMatchers(smTopsLayer, avTopsLayer);
        AssetChoiceLayer.parallelMatchers(smShoesLayer, avShoesLayer);

        @SuppressWarnings("all")
        final DecisionLayer clothingLogic = new DecisionLayer(
                "outfit", () -> {
                    final boolean combined = clothingTypeLayer
                            .getChoice().equals(COMBINED_OUTFIT);

                    // TODO - if more body shapes are added
                    return switch (getBodyLayerChoice()) {
                        case 0 -> combined ? avOutfitLayer : avArticlesLayer;
                        default -> combined ? smOutfitLayer : smArticlesLayer;
                    };
        });
        clothingTypeLayer.addDependent(clothingLogic);
        bodyLayer.addDependent(clothingLogic);

        final AssetChoiceLayer hairLayer = ACLBuilder.of(
                        "hair", this,
                        new AssetChoiceTemplate("dragon-master", this::replace),
                        new AssetChoiceTemplate("nest", this::replace),
                        new AssetChoiceTemplate("afro", this::replace),
                        new AssetChoiceTemplate("bangs", this::replace),
                        new AssetChoiceTemplate("closer", this::replace),
                        new AssetChoiceTemplate("disheveled", this::replace),
                        new AssetChoiceTemplate("framed", this::replace),
                        new AssetChoiceTemplate("pageant-queen", this::replace),
                        new AssetChoiceTemplate("pippi",
                                c -> replaceWithNSelections(c, 1), hairAcc),
                        new AssetChoiceTemplate("pixie", this::replace),
                        new AssetChoiceTemplate("prodigy", this::replace),
                        new AssetChoiceTemplate("raven", this::replace),
                        new AssetChoiceTemplate("professional", this::replace),
                        new AssetChoiceTemplate("crew-cut", this::replace),
                        new AssetChoiceTemplate("dainty",
                                c -> replaceWithNSelections(c, 1), hairAcc),
                        new AssetChoiceTemplate("flared-curtains", this::replace),
                        new AssetChoiceTemplate("geezer", this::replace),
                        new AssetChoiceTemplate("mop", this::replace),
                        new AssetChoiceTemplate("outta-my-face",
                                c -> replaceWithNSelections(c, 1), hairAcc),
                        new AssetChoiceTemplate("pigtails",
                                c -> replaceWithNSelections(c, 1), hairAcc),
                        new AssetChoiceTemplate("rocker", this::replace),
                        new AssetChoiceTemplate("serene",
                                c -> replaceWithNSelections(c, 1), hairAcc),
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
                                this::clothesReplace, hatCS[0], hatCS[1]),
                        new AssetChoiceTemplate("fitted-back",
                                this::clothesReplace, hatCS[0], hatCS[1]),
                        new AssetChoiceTemplate("fedora",
                                this::clothesReplace, hatCS[0], hatCS[1]))
                .setName("Headwear").setComposer(this::composeHead)
                .setNoAssetChoice(NoAssetChoice.prob(0.75)).build();

        final MaskLayer hatMaskLayer = MLBuilder.init("hat-mask", hairLayer)
                .trySetNaiveLogic(this, hatLayer).build();

        // TODO - still assembling
        layers.add(
                skinLayer, bodyLayer, headLayer,
                eyeLayer, eyeHeightLayer, eyeColorLayer,
                clothingTypeLayer, clothingLogic,
                hairLayer, hairColorLayer,
                hatLayer, hatMaskLayer
        );
    }

    @Override
    public String name() {
        return "Pokémon Trainer [Gen 4]";
    }

    public AssetChoiceLayer buildOutfit(
            final BodyType bt
    ) {
        return buildClothes(bt.prefix + "-outfit",
                new AssetChoiceTemplate("gi",
                        this::clothesReplace, topCS[0], topCS[1]),
                new AssetChoiceTemplate("farmer-1",
                        this::clothesReplace, topCS[0], topCS[1], topCS[2]))
                .setName("Outfit").build();
    }

    public AssetChoiceLayer buildTop(
            final BodyType bt
    ) {
        return buildClothes(bt.prefix + "-top",
                new AssetChoiceTemplate("vest",
                        this::clothesReplace, topCS[0], topCS[1]),
                new AssetChoiceTemplate("blouse",
                        this::clothesReplace, topCS[0], topCS[1]))
                .setName("Torso").build();
    }

    public AssetChoiceLayer buildBottom(
            final BodyType bt
    ) {
        return buildClothes(bt.prefix + "-bottom",
                new AssetChoiceTemplate("slacks",
                        this::clothesReplace, botCS[0]),
                new AssetChoiceTemplate("shorts",
                        this::clothesReplace, botCS[0]))
                .setName("Legs").build();
    }

    public AssetChoiceLayer buildShoes(
            final BodyType bt
    ) {
        return buildClothes(bt.prefix + "-shoes",
                new AssetChoiceTemplate("simple",
                        this::clothesReplace, shoeCS[0]))
                .setName("Shoes").build();
    }

    private ACLBuilder buildClothes(
            final String id, final AssetChoiceTemplate... choices
    ) {
        return ACLBuilder.of(id, this, choices)
                .setNoAssetChoice(NoAssetChoice.prob(0.0));
    }

    private ColorSelection clothesSwatch(
            final int index
    ) {
        final String name = switch (index) {
            case 0 -> "Main";
            case 1 -> "Accent";
            case 2 -> "3rd";
            default -> (index + 1) + "th";
        };

        return new ColorSelection(name, true, CLOTHES_SWATCHES);
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
            base = BASE_CLOTH_2;
        } else if (CLOTH_3.contains(input)) {
            index = 2;
            base = BASE_CLOTH_3;
        } else if (CLOTH_4.contains(input)) {
            index = 3;
            base = BASE_CLOTH_4;
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
        return replaceWithNSelections(input, 0);
    }

    private Pair<Integer, Function<Color, Color>> replaceWithNSelections(
            final Color input, final int n
    ) {

        int index = -1;
        Color b = black();

        final List<Set<Color>> REPLS = List.of(
                CLOTH_1, CLOTH_2, CLOTH_3, CLOTH_4);
        final Color[] BASES = new Color[] {
                BASE_CLOTH_1, BASE_CLOTH_2, BASE_CLOTH_3, BASE_CLOTH_4
        };

        final boolean isSkin = SKIN.contains(input),
                isOutline = SKIN_OUTLINES.contains(input),
                isHair = HAIR.contains(input),
                isIris = IRIS.contains(input),
                isEW = EYE_WHITE.contains(input);

        for (int i = 0; i < n; i++)
            if (REPLS.get(i).contains(input)) {
                index = i;
                b = BASES[i];
                break;
            }

        if (isSkin || isOutline) {
            index = n;
            b = BASE_SKIN;
        } else if (isHair) {
            index = n + 1;
            b = BASE_HAIR;
        } else if (isIris) {
            index = n + 2;
            b = BASE_IRIS;
        } else if (isEW) {
            index = n + 3;
            b = BASE_EYE_WHITE;
        }

        final Color base = b;

        return new Pair<>(index, c -> {
            final double ih = rgbToHue(input),
                    is = rgbToSat(input), iv = rgbToValue(input),
                    ch = rgbToHue(c), cs = rgbToSat(c),
                    cv = rgbToValue(c),
                    bs = rgbToSat(base), bv = rgbToValue(base),
                    sRatio = (cs * is) / bs, vRatio = (cv * iv) / bv,
                    s = MathPlus.bounded(0.0, sRatio, 1.0),
                    v = MathPlus.bounded(0.0, vRatio, 1.0);

            if (isOutline) {
                // Skin outline
                final double hueDiff = rgbToHue(base) - ih,
                        hue = normalizeHue(ch - hueDiff);

                return fromHSV(hue, s, v);
            }

            return fromHSV(ch, s, v);
        });
    }

    private SpriteConstituent<String> composeEyes(
            final SpriteSheet sheet
    ) {
        return composeHead(sheet, -eyeHeightLayer.getValue());
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
