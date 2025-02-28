package com.jordanbunke.tdsm.data.style;

import com.jordanbunke.color_proc.ColorAlgo;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.MenuBuilder;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement.Anchor;
import com.jordanbunke.delta_time.sprite.SpriteAssembler;
import com.jordanbunke.delta_time.sprite.SpriteSheet;
import com.jordanbunke.delta_time.sprite.SpriteStates;
import com.jordanbunke.delta_time.sprite.constituents.InterpretedSpriteSheetWithOffset;
import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.delta_time.utility.math.MathPlus;
import com.jordanbunke.delta_time.utility.math.Pair;
import com.jordanbunke.stip_parser.ParserSerializer;
import com.jordanbunke.stip_parser.rep.IRPalette;
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
import com.jordanbunke.tdsm.menu.Checkbox;
import com.jordanbunke.tdsm.menu.IconButton;
import com.jordanbunke.tdsm.menu.Indicator;
import com.jordanbunke.tdsm.menu.StaticLabel;
import com.jordanbunke.tdsm.util.Constants;
import com.jordanbunke.tdsm.util.MenuAssembly;
import com.jordanbunke.tdsm.util.ParserUtils;
import com.jordanbunke.tdsm.util.ResourceCodes;

import java.awt.*;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import static com.jordanbunke.color_proc.ColorProc.*;
import static com.jordanbunke.tdsm.util.Colors.black;
import static com.jordanbunke.tdsm.util.Layout.atX;
import static com.jordanbunke.tdsm.util.Layout.atY;

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

    private final Function<Color, Color> quantizeToPalette;

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

        // Initialize settings
        quantize = false;
        warnROMColLimit = false;

        quantizeToPalette = buildPaletteQuantizer();

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
                .setComposer(this::composeHead).setName("Hairstyle")
                .setNoAssetChoice(NoAssetChoice.equal())
                .build();
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
                .setName("Headwear").setComposer(this::composeHead)
                .setNoAssetChoice(NoAssetChoice.prob(0.75)).build();

        final MaskLayer hatMaskLayer = MLBuilder.init("hat-mask", hairLayer)
                .trySetNaiveLogic(this, hatLayer).build();

        // TODO - still assembling
        layers.add(
                skinLayer, hairBack, bodyLayer, headLayer,
                eyeLayer, eyeColorLayer, eyeHeightLayer,
                clothingTypeLayer, clothingLogic,
                hairLayer, hairColorLayer,
                hatLayer, hatMaskLayer, hairFront
        );
    }

    @Override
    public String name() {
        return "Pokémon Trainer [Gen 4]";
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
    public void buildSettingsMenu(final MenuBuilder mb) {
        final double REL_W = 0.6;
        final int LEFT = atX((1.0 - REL_W) / 2.0), INC_Y = atY(1 / 9.0);

        int y = atY(0.25);

        final Checkbox quantizeCheckbox = new Checkbox(new Coord2D(LEFT, y),
                Anchor.LEFT_TOP, () -> quantize, b -> quantize = b);
        final StaticLabel quantizeLabel = StaticLabel.init(
                quantizeCheckbox.followMiniLabel(),
                        "Quantize to Pokemon Gen IV sprite palette")
                .setMini().build();
        final Indicator quantizeInfo = Indicator.make(
                ResourceCodes.QUANTIZE_PKMN_G4,
                quantizeLabel.follow(), Anchor.LEFT_TOP);

        y += (int) (INC_Y * 0.5);
        final Checkbox romColLimitCheckbox = new Checkbox(new Coord2D(LEFT, y),
                Anchor.LEFT_TOP, () -> warnROMColLimit, b -> warnROMColLimit = b);
        final StaticLabel romColLimitLabel = StaticLabel.init(
                        romColLimitCheckbox.followMiniLabel(),
                        "Warn if sprite contains more than 15 colors")
                .setMini().build();
        final Indicator romColLimitInfo = Indicator.make(
                ResourceCodes.WARN_ROM_15_COLS,
                romColLimitLabel.follow(), Anchor.LEFT_TOP);

        mb.addAll(quantizeCheckbox, quantizeLabel, quantizeInfo,
                romColLimitCheckbox, romColLimitLabel, romColLimitInfo);
    }

    @Override
    public boolean hasPreExportStep() {
        if (!warnROMColLimit)
            return false;

        return spriteSheetColors().size() > Constants.GBA_SPRITE_COL_LIMIT;
    }

    private Map<Color, Integer> spriteSheetColors() {
        final GameImage spriteSheet = renderSpriteSheet();
        final int w = spriteSheet.getWidth(), h = spriteSheet.getHeight();
        final Map<Color, Integer> cs = new HashMap<>();

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                final Color c = spriteSheet.getColorAt(x, y);

                if (c.getAlpha() == 0) continue;

                if (cs.containsKey(c))
                    cs.put(c, cs.get(c) + 1);
                else
                    cs.put(c, 1);
            }
        }

        return cs;
    }

    @Override
    public void buildPreExportMenu(final MenuBuilder mb) {
        final Map<Color, Integer> cs = spriteSheetColors();

        MenuAssembly.preExportExplanation(mb, """
                The sprite sheet has $cols non-transparent colors,
                which is more that the $max-color maximum permitted
                for Game Boy Advance sprites."""
                .replace("$cols", String.valueOf(cs.size()))
                .replace("$max", String.valueOf(Constants.GBA_SPRITE_COL_LIMIT)),
                0.05, 0.15);

        final double REL_W = 0.6;
        final int LEFT = atX((1.0 - REL_W) / 2.0), INC_Y = atY(1 / 9.0);

        int y = atY(0.15);

        final StaticLabel replacementLabel = StaticLabel.init(
                new Coord2D(LEFT, y), "Replacement").build();
        final Indicator replacementInfo = Indicator.make(
                ResourceCodes.REPLACEMENT, replacementLabel.followIcon17(),
                Anchor.LEFT_TOP);
        final IconButton replacementReset = IconButton.init(
                ResourceCodes.RESET, replacementInfo.following(),
                () -> {} /* TODO */).build();

        mb.addAll(replacementLabel, replacementInfo, replacementReset);

        // TODO
    }

    @Override
    public GameImage preExportTransform(final GameImage input) {
        // TODO - use a Map<Color, Color> replacement field

        return input;
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

    private Function<Color, Color> buildPaletteQuantizer() {
        final String content = ParserUtils.read(Constants.ASSET_ROOT_FOLDER
                .resolve(Path.of(id, "palettes", "palette.stippal")));
        final IRPalette rep = ParserSerializer.loadPalette(content);
        return ColorAlgo.quantizeToPalette(rep.colors());
    }

    private AssetChoiceLayer buildEyes() {
        final String[] ids = new String[] {
                "determined", "hooded", "soft", "vacant", "narrow",
                "menacing", "feminine", "tired", "lashes", "cranky"
        };

        return ACLBuilder.of("eyes", this, Arrays.stream(ids)
                .map(id -> new AssetChoiceTemplate(id, this::replace))
                .toArray(AssetChoiceTemplate[]::new))
                .setComposer(this::composeEyes).build();
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
