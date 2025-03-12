package com.jordanbunke.tdsm.data.style;

import com.jordanbunke.color_proc.ColorAlgo;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.ResourceLoader;
import com.jordanbunke.delta_time.menu.MenuBuilder;
import com.jordanbunke.delta_time.sprite.SpriteAssembler;
import com.jordanbunke.delta_time.sprite.SpriteSheet;
import com.jordanbunke.delta_time.sprite.SpriteStates;
import com.jordanbunke.delta_time.sprite.constituents.InterpretedSpriteSheet;
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
import com.jordanbunke.tdsm.util.Colors;
import com.jordanbunke.tdsm.util.Constants;
import com.jordanbunke.tdsm.util.ResourceCodes;
import com.jordanbunke.tdsm.util.hardware.GBAUtils;

import java.awt.*;
import java.nio.file.Path;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

import static com.jordanbunke.color_proc.ColorProc.*;
import static com.jordanbunke.tdsm.util.Colors.alphaMask;
import static com.jordanbunke.tdsm.util.Colors.black;

public final class PokemonGen4Style extends Style {
    private static final PokemonGen4Style INSTANCE;

    private static final String ID = "hokkaido";
    private static final Bounds2D DIMS = new Bounds2D(48, 48),
            HEAD_DIMS = new Bounds2D(32, 32),
            HEAD_SHEET_DIMS = new Bounds2D(160, 32);

    private static final String ANIM_ID_IDLE = "idle", ANIM_ID_WALK = "walk",
            ANIM_ID_RUN = "run", ANIM_ID_FISH = "fish",
            ANIM_ID_BIKE_IDLE = "bike_idle", ANIM_ID_CYCLE = "cycle",
            ANIM_ID_SURF = "surf", ANIM_ID_SWIM = "swim",
            ANIM_ID_CAPSULE = "use_capsule";

    private final String COMBINED_OUTFIT = "Combined outfit";

    private static final String[] BODY_IDs = new String[] {
            "average-body", "small-body"
    };

    private static final Set<Color>
            SKIN, SKIN_OUTLINES, HAIR, IRIS, EYE_WHITE,
            CLOTH_1, CLOTH_2, CLOTH_3, CLOTH_4;
    private static final Color
            BASE_SKIN, BASE_HAIR, BASE_IRIS, BASE_EYE_WHITE,
            BASE_CLOTH_1, BASE_CLOTH_2, BASE_CLOTH_3, BASE_CLOTH_4;

    private static final Color[]
            SKIN_SWATCHES, HAIR_SWATCHES,
            IRIS_SWATCHES, CLOTHES_SWATCHES;

    private final Map<String, SpriteSheet> headMasks;

    private final Function<Color, Color> quantizeToPalette;
    private final Map<Color, Color> replacementMap;
    private Color selectedToReplace;

    private AssetChoiceLayer bodyLayer, hatLayer;
    private final MathLayer eyeHeightLayer;
    private final ChoiceLayer clothingTypeLayer;

    final ColorSelection skinTones, hairColors, eyebrowColors,
            irisColors, ewColors, hairAcc;
    final ColorSelection[] hatCS, topCS, botCS, shoeCS;

    // SETTINGS
    private boolean quantize, warnROMColLimit;

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
                new Color(0x4b382c),
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

        INSTANCE = new PokemonGen4Style();
    }

    private enum BodyType {
        AVERAGE, SMALL;

        final String prefix;

        BodyType() {
            prefix = name().substring(0, 2).toLowerCase();
        }
    }

    private PokemonGen4Style() {
        super(ID, DIMS, setUpDirections(), setUpAnimations(), new Layers());

        // Initialize settings
        quantize = false;
        warnROMColLimit = false;

        quantizeToPalette = GBAUtils::quantize;
        replacementMap = new HashMap<>();
        selectedToReplace = null;

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

        headMasks = new HashMap<>();

        for (String bodyID : BODY_IDs) {
            final Path filepath = Constants.ASSET_ROOT_FOLDER
                    .resolve(Path.of(id, "head-mask", bodyID + ".png"));
            final GameImage source = ResourceLoader
                    .loadImageResource(filepath);
            headMasks.put(bodyID, new SpriteSheet(source,
                    dims.width(), dims.height()));
        }

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

    public static PokemonGen4Style get() {
        return INSTANCE;
    }

    private static Directions setUpDirections() {
        return new Directions(NumDirs.FOUR, false,
                Dir.DOWN, Dir.LEFT, Dir.RIGHT, Dir.UP);
    }

    private static Animation[] setUpAnimations() {
        final boolean horizontal = true;

        return new Animation[] {
                Animation.init(ANIM_ID_WALK, 3)
                        .setPlaybackMode(PlaybackMode.PONG)
                        .setCoordFunc(f -> {
                            final int x = switch (f) {
                                case 0 -> 1;
                                case 1 -> 0;
                                default -> 2;
                            };
                            final int y = 0;
                            return new Coord2D(x, y);
                        }).setFrameTiming(10).build(),
                Animation.init(ANIM_ID_IDLE, 1).build(),
                Animation.init(ANIM_ID_RUN, 3)
                        .setPlaybackMode(PlaybackMode.PONG)
                        .setCoordFunc(new Coord2D(3, 0), horizontal)
                        .setFrameTiming(6).build(),
                Animation.init(ANIM_ID_FISH, 4)
                        .setCoordFunc(new Coord2D(6, 0), horizontal)
                        .build(),
                Animation.init(ANIM_ID_BIKE_IDLE, 1)
                        .setCoordFunc(new Coord2D(10, 0), horizontal).build(),
                Animation.init(ANIM_ID_CYCLE, 4)
                        .setCoordFunc(new Coord2D(11, 0), horizontal)
                        .setFrameTiming(8).build(),
                Animation.init(ANIM_ID_SURF, 1)
                        .setCoordFunc(new Coord2D(15, 0), horizontal).build(),
                Animation.init(ANIM_ID_SWIM, 3)
                        .setPlaybackMode(PlaybackMode.PONG)
                        .setCoordFunc(f -> {
                            final int x = switch (f) {
                                case 0 -> 1;
                                case 1 -> 0;
                                default -> 2;
                            };
                            final int y = 0;
                            return new Coord2D(16 + x, y);
                        }).build(),
                Animation.init(ANIM_ID_CAPSULE, 4)
                        .setCoordFunc(new Coord2D(19, 0), horizontal).build(),
        };
    }

    private void setUpLayers() {
        final ColorSelectionLayer skinLayer = new ColorSelectionLayer(
                "skin", "Skin Color", skinTones);

        bodyLayer = buildBody();
        bodyLayer.addInfluencingSelection(skinTones);

        final AssetChoiceLayer headLayer = ACLBuilder.of(
                        "head", this,
                        new AssetChoiceTemplate("oval-head", this::replace),
                        new AssetChoiceTemplate("round-head", this::replace),
                        new AssetChoiceTemplate("square-jaw", this::replace))
                .setComposer(sheet -> spriteID -> sheet.getSheet())
                .setDims(HEAD_DIMS).setName("Head Shape").build();
        headLayer.addInfluencingSelection(skinTones);

        final AssetChoiceLayer eyeLayer = buildEyes();
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
                "outfit", "Outfit", smBottomsLayer,
                smShoesLayer, smTopsLayer);

        final AssetChoiceLayer avOutfitLayer = buildOutfit(BodyType.AVERAGE),
                avTopsLayer = buildTop(BodyType.AVERAGE),
                avBottomsLayer = buildBottom(BodyType.AVERAGE),
                avShoesLayer = buildShoes(BodyType.AVERAGE);
        final GroupLayer avArticlesLayer = new GroupLayer(
                "outfit", "Outfit", avBottomsLayer,
                avShoesLayer, avTopsLayer);

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
                        new AssetChoiceTemplate("waves", this::replace),
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
                .setComposer(this::composeOnHead).setName("Hairstyle")
                .setNoAssetChoice(NoAssetChoice.equal())
                .setDims(HEAD_DIMS).build();
        hairLayer.addInfluencingSelections(skinTones, hairColors);

        final DependentComponentLayer hairBack = new DependentComponentLayer(
                "hair-back", this, hairLayer, -1),
                hairFront = new DependentComponentLayer(
                        "hair-front", this, hairLayer, 1);

        // TODO - add more
        hatLayer = ACLBuilder.of("hat", this,
                        new AssetChoiceTemplate("fitted-front",
                                this::clothesReplace, hatCS[0], hatCS[1]),
                        new AssetChoiceTemplate("fitted-back",
                                this::clothesReplace, hatCS[0], hatCS[1]),
                        new AssetChoiceTemplate("fedora",
                                this::clothesReplace, hatCS[0], hatCS[1]),
                        new AssetChoiceTemplate("durag",
                                this::clothesReplace, hatCS[0]))
                .setName("Headwear").setComposer(this::composeOnHead)
                .setNoAssetChoice(NoAssetChoice.prob(0.75))
                .setDims(HEAD_DIMS).build();

        final MaskLayer hatMaskLayer = MLBuilder.init("hat-mask", hairLayer)
                .trySetNaiveLogic(this, hatLayer).build();

        // TODO - still assembling
        layers.addToCustomization(
                bodyLayer, skinLayer, headLayer, eyeLayer,
                eyeColorLayer, eyeHeightLayer, hairLayer, hairColorLayer,
                clothingTypeLayer, clothingLogic, hatLayer
        );

        // TODO - consider separating hairBack from combined head

        final PureComposeLayer combinedHeadLayer =
                new PureComposeLayer("combined-head",
                        spriteID -> {
                    final GameImage preassembled = new GameImage(HEAD_SHEET_DIMS);

                    preassembled.draw(hairBack.compose().getSprite(spriteID));
                    preassembled.draw(headLayer.compose().getSprite(spriteID));
                    preassembled.draw(eyeLayer.compose().getSprite(spriteID));

                    final GameImage hair = hairLayer.compose().getSprite(spriteID),
                            hatMask = hatMaskLayer.compose().getSprite(spriteID);
                    alphaMask(hair, hatMask);

                    preassembled.draw(hair);
                    preassembled.draw(hatLayer.compose().getSprite(spriteID));
                    preassembled.draw(hairFront.compose().getSprite(spriteID));

                    final SpriteSheet combinedHead =
                            new SpriteSheet(preassembled.submit(),
                                    HEAD_DIMS.width(), HEAD_DIMS.height());

                    return composeHead(combinedHead).getSprite(spriteID);
                });

        final MaskLayer headMask = MLBuilder.init("head-mask", combinedHeadLayer)
                .setLogic(s -> {
                    final String animID =
                            SpriteStates.extractContributor(ANIM, s);

                    return switch (animID) {
                        case ANIM_ID_CAPSULE, ANIM_ID_SWIM ->
                                bodyLayer.composer.build(headMasks.get(
                                        bodyLayer.getChoice().id)).getSprite(s);
                        default -> new GameImage(dims.width(), dims.height());
                    };
                }).build();

        layers.addToAssembly(
                skinLayer, /* hairBack, */ bodyLayer,
                clothingTypeLayer, clothingLogic,
                combinedHeadLayer, headMask);
    }

    @Override
    public String name() {
        return "Hokkaido";
    }

    @Override
    public boolean shipping() {
        return true;
    }

    @Override
    public boolean hasSettings() {
        return true;
    }

    @Override
    public StyleOption[] getOptionSettings() {
        return new StyleOption[] {
                new StyleOption("Quantize to Game Boy Advance colors",
                        () -> quantize, b -> quantize = b,
                        ResourceCodes.QUANTIZE_GBA),
                new StyleOption("Warn if sprite contains more than 15 colors",
                        () -> warnROMColLimit, b -> warnROMColLimit = b,
                        ResourceCodes.WARN_ROM_15_COLS)
        };
    }

    @Override
    public boolean hasPreExportStep() {
        if (!warnROMColLimit)
            return false;

        return Colors.colorOccurrences(this).size() >
                Constants.GBA_SPRITE_COL_LIMIT;
    }

    @Override
    public void buildPreExportMenu(final MenuBuilder mb) {
        GBAUtils.buildReplacementMenu(mb, this, replacementMap,
                () -> selectedToReplace, c -> selectedToReplace = c);
    }

    @Override
    public GameImage preExportTransform(final GameImage input) {
        if (replacementMap.isEmpty())
            return input;

        return ColorAlgo.run(c -> replacementMap.getOrDefault(c, c), input);
    }

    @Override
    public void resetPreExport() {
        replacementMap.clear();
        selectedToReplace = null;
    }

    @Override
    void considerations(final SpriteAssembler<String, String> assembler) {
        if (quantize) {
            final String quantizeID = "quantize-to-palette";

            final List<String> layerIDs = assembler.getEnabledLayerIDs();

            for (String layerID : layerIDs)
                assembler.addFilter(quantizeID, quantizeToPalette, layerID);
        }
    }

    private AssetChoiceLayer buildBody() {
        return ACLBuilder.of("body", this, Arrays.stream(BODY_IDs)
                .map(id -> new AssetChoiceTemplate(id, this::replace))
                .toArray(AssetChoiceTemplate[]::new))
                .setName("Body Type").build();
    }

    private AssetChoiceLayer buildEyes() {
        final String[] ids = new String[] {
                "determined", "hooded", "soft", "vacant", "narrow",
                "menacing", "feminine", "tired", "lashes", "cranky"
        };

        return ACLBuilder.of("eyes", this, Arrays.stream(ids)
                .map(id -> new AssetChoiceTemplate(id, this::replace))
                .toArray(AssetChoiceTemplate[]::new))
                .setDims(HEAD_DIMS).setComposer(this::composeEyes).build();
    }

    private AssetChoiceLayer buildOutfit(
            final BodyType bt
    ) {
        return buildClothes(bt.prefix + "-outfit",
                new AssetChoiceTemplate("gi",
                        this::clothesReplace, topCS[0], topCS[1]),
                new AssetChoiceTemplate("farmer-1",
                        this::clothesReplace, topCS[0], topCS[1], topCS[2]))
                .setName("Outfit").build();
    }

    private AssetChoiceLayer buildTop(
            final BodyType bt
    ) {
        return buildClothes(bt.prefix + "-top",
                new AssetChoiceTemplate("vest",
                        this::clothesReplace, topCS[0], topCS[1]),
                new AssetChoiceTemplate("blouse",
                        this::clothesReplace, topCS[0], topCS[1]))
                .setName("Torso").build();
    }

    private AssetChoiceLayer buildBottom(
            final BodyType bt
    ) {
        return buildClothes(bt.prefix + "-bottom",
                new AssetChoiceTemplate("slacks",
                        this::clothesReplace, botCS[0]),
                new AssetChoiceTemplate("shorts",
                        this::clothesReplace, botCS[0]))
                .setName("Legs").build();
    }

    private AssetChoiceLayer buildShoes(
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
        final Color rgbInput = rgbOnly(input), base;
        final int index;

        if (CLOTH_1.contains(rgbInput)) {
            index = 0;
            base = BASE_CLOTH_1;
        } else if (CLOTH_2.contains(rgbInput)) {
            index = 1;
            base = BASE_CLOTH_2;
        } else if (CLOTH_3.contains(rgbInput)) {
            index = 2;
            base = BASE_CLOTH_3;
        } else if (CLOTH_4.contains(rgbInput)) {
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

            return fromHSV(ch, s, v, input.getAlpha());
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
        final Color rgbInput = rgbOnly(input);

        int index = -1;
        Color b = black();

        final List<Set<Color>> REPLS = List.of(
                CLOTH_1, CLOTH_2, CLOTH_3, CLOTH_4);
        final Color[] BASES = new Color[] {
                BASE_CLOTH_1, BASE_CLOTH_2, BASE_CLOTH_3, BASE_CLOTH_4
        };

        final boolean isSkin = SKIN.contains(rgbInput),
                isOutline = SKIN_OUTLINES.contains(rgbInput),
                isHair = HAIR.contains(rgbInput),
                isIris = IRIS.contains(rgbInput),
                isEW = EYE_WHITE.contains(rgbInput);

        for (int i = 0; i < n; i++)
            if (REPLS.get(i).contains(rgbInput)) {
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

            return fromHSV(ch, s, v, input.getAlpha());
        });
    }

    private SpriteConstituent<String> composeEyes(
            final SpriteSheet sheet
    ) {
        return composeOnHead(sheet, -eyeHeightLayer.getValue());
    }

    private SpriteConstituent<String> composeOnHead(final SpriteSheet sheet) {
        return composeOnHead(sheet, 0);
    }

    private SpriteConstituent<String> composeOnHead(
            final SpriteSheet sheet, final int augY
    ) {
        return id -> {
            final GameImage dest = new GameImage(HEAD_SHEET_DIMS);

            for (int x = 0; x < 4; x++) {
                final GameImage source = sheet.getSprite(new Coord2D(x, 0));

                dest.draw(source, x * HEAD_DIMS.width(), augY);

                if (x == 0)
                    dest.draw(source, 4 * HEAD_DIMS.width(), augY);
            }

            return dest.submit();
        };
    }

    private SpriteConstituent<String> composeHead(final SpriteSheet sheet) {
        final SpriteConstituent<String> assetFetcher =
                new InterpretedSpriteSheet<>(sheet, this::headDirX);

        return id -> {
            final GameImage sprite = new GameImage(DIMS.width(), DIMS.height());

            final Directions.Dir dir = Directions.get(
                    SpriteStates.extractContributor(DIRECTION, id));
            final String animID =
                    SpriteStates.extractContributor(ANIM, id);

            if (animID.equals(ANIM_ID_CAPSULE) && !dir.equals(Dir.DOWN))
                return sprite;

            final Coord2D offset = headOffset(id);
            final int BASE_X = 8, BASE_Y = 8,
                    x = BASE_X + offset.x, y = BASE_Y + offset.y;

            sprite.draw(assetFetcher.getSprite(id), x, y);
            return sprite.submit();
        };
    }

    private Coord2D headOffset(final String spriteID) {
        final Directions.Dir dir = Directions.get(
                SpriteStates.extractContributor(DIRECTION, spriteID));
        final String animID =
                SpriteStates.extractContributor(ANIM, spriteID);
        final int frame = Integer.parseInt(
                SpriteStates.extractContributor(FRAME, spriteID));

        final int bodyComp, animComp, frameComp, x;

        bodyComp = switch (animID) {
            case ANIM_ID_SWIM, ANIM_ID_RUN, ANIM_ID_CYCLE -> 0;
            default -> getBodyLayerChoice();
        };

        final Function<Integer, Integer>
                cycleFunc = f -> f % 2 == 0 ? 0 : (f - 2),
                runFunc = f -> f % 2 == 0 ? (f - 1) : 0,
                fishSideFunc = f -> (f < 2 ? -1 : 1) * (f % 3 == 0 ? 1 : 2);

        x = switch (animID) {
            case ANIM_ID_RUN -> switch (dir) {
                case LEFT -> -3 + runFunc.apply(frame);
                case RIGHT -> 3 + runFunc.apply(frame);
                default -> runFunc.apply(frame);
            };
            case ANIM_ID_FISH -> switch (dir) {
                case LEFT -> -1 * fishSideFunc.apply(frame);
                case RIGHT -> fishSideFunc.apply(frame);
                case DOWN -> frame < 2 ? 1 : 0;
                case UP -> frame < 3 ? -1 : 0;
                default -> 0;
            };
            case ANIM_ID_BIKE_IDLE -> switch (dir) {
                case LEFT -> -1;
                case RIGHT -> 1;
                case UP -> -2;
                case DOWN -> 2;
                default -> 0;
            };
            case ANIM_ID_CYCLE -> switch (dir) {
                case LEFT -> -2 + cycleFunc.apply(frame);
                case RIGHT -> 2 - cycleFunc.apply(frame);
                default -> cycleFunc.apply(frame);
            };
            case ANIM_ID_CAPSULE -> switch (frame) {
                case 0, 2 -> 0;
                default -> -1;
            };
            default -> 0;
        };

        // animation Y offset component
        animComp = switch (animID) {
            case ANIM_ID_IDLE -> 1;
            case ANIM_ID_RUN -> dir.equals(Dir.DOWN) ? 6 : 2;
            case ANIM_ID_FISH, ANIM_ID_BIKE_IDLE -> switch (dir) {
                case LEFT, RIGHT -> 1;
                default -> 0;
            };
            case ANIM_ID_CYCLE -> dir.equals(Dir.DOWN) ? 1 : 0;
            case ANIM_ID_SURF -> switch (dir) {
                case LEFT, RIGHT -> 3;
                case UP -> 2;
                case DOWN -> 4;
                default -> 0;
            };
            case ANIM_ID_SWIM -> 7;
            default -> 0;
        };

        // animation frame Y offset component
        frameComp = switch (animID) {
            case ANIM_ID_WALK -> frame == 1 ? 1 : 0;
            case ANIM_ID_RUN -> frame == 1
                    ? (dir.equals(Dir.DOWN) ? -1 : 1) : 0;
            case ANIM_ID_FISH -> switch (dir) {
                case LEFT, RIGHT -> frame == 3 ? 1 : 0;
                case DOWN -> switch (frame) {
                    case 0 -> 2;
                    case 2 -> 5;
                    case 3 -> 3;
                    default -> 1;
                };
                case UP -> switch (frame) {
                    case 1 -> 4;
                    case 2 -> 2;
                    default -> 3;
                };
                default -> 0;
            };
            case ANIM_ID_SWIM -> switch (dir) {
                case LEFT, RIGHT -> frame != 1 ? 1 : 0;
                default -> 0;
            };
            case ANIM_ID_CAPSULE -> switch (frame) {
                case 0 -> 4;
                case 1 -> 5;
                default -> 1;
            };
            default -> 0;
        };

        return new Coord2D(x, bodyComp + animComp + frameComp);
    }

    private Coord2D headDirX(final String spriteID) {
        final Directions.Dir dir = Directions.get(
                SpriteStates.extractContributor(DIRECTION, spriteID));
        final String animID =
                SpriteStates.extractContributor(ANIM, spriteID);
        final int frame = Integer.parseInt(
                SpriteStates.extractContributor(FRAME, spriteID));

        final int x = indexOfDir(dir);

        final boolean tiltedDown = dir.equals(Dir.DOWN) &&
                switch (animID) {
            case ANIM_ID_RUN, ANIM_ID_CYCLE, ANIM_ID_SURF -> true;
            case ANIM_ID_FISH -> frame == 2;
            case ANIM_ID_CAPSULE -> frame < 2;
            default -> false;
        };

        return new Coord2D(x + (tiltedDown ? 4 : 0), 0);
    }

    private int getBodyLayerChoice() {
        if (bodyLayer == null)
            return 0;

        return bodyLayer.getChoiceIndex();
    }
}
