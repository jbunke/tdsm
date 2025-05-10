package com.jordanbunke.tdsm.data.style.pkmn;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.ResourceLoader;
import com.jordanbunke.delta_time.sprite.SpriteSheet;
import com.jordanbunke.delta_time.sprite.SpriteStates;
import com.jordanbunke.delta_time.sprite.constituents.InterpretedSpriteSheet;
import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.data.Animation;
import com.jordanbunke.tdsm.data.Animation.PlaybackMode;
import com.jordanbunke.tdsm.data.Directions;
import com.jordanbunke.tdsm.data.Directions.Dir;
import com.jordanbunke.tdsm.data.func.CoordFunc;
import com.jordanbunke.tdsm.data.layer.*;
import com.jordanbunke.tdsm.data.layer.builders.ACLBuilder;
import com.jordanbunke.tdsm.data.layer.builders.MLBuilder;
import com.jordanbunke.tdsm.data.layer.support.AssetChoice;
import com.jordanbunke.tdsm.data.layer.support.AssetChoiceTemplate;
import com.jordanbunke.tdsm.data.layer.support.ColorSelection;
import com.jordanbunke.tdsm.data.layer.support.NoAssetChoice;
import com.jordanbunke.tdsm.util.Constants;
import com.jordanbunke.tdsm.util.ParserUtils;

import java.awt.*;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

import static com.jordanbunke.tdsm.util.Colors.alphaMask;

public final class HokkaidoStyle extends PokemonStyle {
    private static final HokkaidoStyle INSTANCE;

    private static final String ID = "hokkaido";
    private static final Bounds2D DIMS = new Bounds2D(48, 48),
            HEAD_DIMS = new Bounds2D(32, 32),
            HEAD_SHEET_DIMS = new Bounds2D(160, 32);

    private static final String[] BODY_IDs = new String[] {
            "average-body", "small-body"
    };

    private final Map<String, SpriteSheet> headMasks;

    private AssetChoiceLayer bodyLayer, hatLayer;
    private final MathLayer eyeHeightLayer;
    private final ChoiceLayer clothingTypeLayer;

    private final ColorSelection skinTones, hairColors, eyebrowColors,
            irisColors, ewColors, hairAcc;
    private final ColorSelection[] hatCS, topCS, botCS, shoeCS;

    static {
        INSTANCE = new HokkaidoStyle();
    }

    private enum BodyType {
        AVERAGE, SMALL;

        final String prefix;

        BodyType() {
            prefix = name().substring(0, 2).toLowerCase();
        }
    }

    private HokkaidoStyle() {
        super(ID, DIMS, setUpAnimations());

        skinTones = new ColorSelection("Skin", true, SKIN_SWATCHES);
        hairColors = new ColorSelection("Hair", true, HAIR_SWATCHES);
        eyebrowColors = new ColorSelection("Brows", true, HAIR_SWATCHES);
        irisColors = new ColorSelection("Iris", true, IRIS_SWATCHES);
        ewColors = new ColorSelection("Outer", true, new Color(0xe8e8f8));
        hairAcc = new ColorSelection("Accessory", true, CLOTHES_SWATCHES);

        hatCS = IntStream.range(0, 4).mapToObj(PokemonStyle::clothesSwatch)
                .toArray(ColorSelection[]::new);
        topCS = IntStream.range(0, 4).mapToObj(PokemonStyle::clothesSwatch)
                .toArray(ColorSelection[]::new);
        botCS = IntStream.range(0, 4).mapToObj(PokemonStyle::clothesSwatch)
                .toArray(ColorSelection[]::new);
        shoeCS = IntStream.range(0, 2).mapToObj(PokemonStyle::clothesSwatch)
                .toArray(ColorSelection[]::new);

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

    public static HokkaidoStyle get() {
        return INSTANCE;
    }

    private static Animation[] setUpAnimations() {
        // TODO - uncomment animations as implemented
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
                        .setCoordFunc(CoordFunc.simple(new Coord2D(3, 0), HORIZONTAL_ANIMS))
                        .setFrameTiming(6).build(),
//                Animation.init(ANIM_ID_FISH, 4)
//                        .setCoordFunc(new Coord2D(6, 0), orientation)
//                        .build(),
//                Animation.init(ANIM_ID_BIKE_IDLE, 1)
//                        .setCoordFunc(new Coord2D(10, 0), orientation).build(),
//                Animation.init(ANIM_ID_CYCLE, 4)
//                        .setCoordFunc(new Coord2D(11, 0), orientation)
//                        .setFrameTiming(8).build(),
                Animation.init(ANIM_ID_SURF, 1)
                        .setCoordFunc(CoordFunc.simple(new Coord2D(15, 0), HORIZONTAL_ANIMS)).build(),
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
//                Animation.init(ANIM_ID_CAPSULE, 4)
//                        .setCoordFunc(new Coord2D(19, 0), orientation).build(),
        };
    }

    private void setUpLayers() {
        final ColorSelectionLayer skinLayer = new ColorSelectionLayer(
                "skin", "Skin Color", skinTones);

        bodyLayer = buildBody();
        bodyLayer.addInfluencingSelection(skinTones);

        final AssetChoiceLayer headLayer = ACLBuilder.of(
                        "head", this,
                        new AssetChoiceTemplate("oval-head", PokemonStyle::replace),
                        new AssetChoiceTemplate("round-head", PokemonStyle::replace),
                        new AssetChoiceTemplate("square-jaw", PokemonStyle::replace))
                .trivialComposer().setDims(HEAD_DIMS)
                .setName("Head Shape").build();
        headLayer.addInfluencingSelection(skinTones);

        final AssetChoiceLayer eyeLayer = buildEyes();
        eyeLayer.addInfluencingSelections(skinTones,
                eyebrowColors, irisColors, ewColors);

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

        final AssetChoiceLayer hairLayer = buildHair();
        hairLayer.addInfluencingSelections(skinTones, hairColors);

        final DependentComponentLayer
                hairBack = buildDCL("hair-back", hairLayer, -1),
                hairFront = buildDCL("hair-front", hairLayer, 1);

        hatLayer = buildClothes(this, "hat", hatCS)
                .setName("Headwear").setComposer(this::composeOnHead)
                .setNoAssetChoice(NoAssetChoice.prob(0.75))
                .setDims(HEAD_DIMS).build();

        final DependentComponentLayer
                hatBack = buildDCL("hat-back", hatLayer, -1);

        final MaskLayer hatMaskLayer = MLBuilder.init("hat-mask", hairLayer)
                .setLogic(MaskLayer.naiveLogic(hatLayer)).build(),
                hatBackMaskLayer = MLBuilder.init("hat-back-mask", hairBack)
                        .setLogic(MaskLayer.naiveLogic(hatBack)).build();

        // TODO - accessories, capsule
        layers.addToCustomization(
                bodyLayer, skinLayer, headLayer, eyeLayer,
                eyeColorLayer, eyeHeightLayer, hairLayer, hairColorLayer,
                clothingTypeLayer, clothingLogic, hatLayer
        );

        final PureComposeLayer combinedHeadBackLayer =
                new PureComposeLayer("combined-head-back", spriteID -> {
                    final GameImage preassembled = new GameImage(HEAD_SHEET_DIMS);

                    preassembled.draw(hatBack.compose().getSprite(spriteID));

                    final GameImage hairB = hairBack.compose().getSprite(spriteID),
                            hatBMask = hatBackMaskLayer.compose().getSprite(spriteID);
                    alphaMask(hairB, hatBMask);

                    preassembled.draw(hairB);

                    final SpriteSheet combined =
                            new SpriteSheet(preassembled.submit(),
                                    HEAD_DIMS.width(), HEAD_DIMS.height());

                    return composeHead(combined).getSprite(spriteID);
                });

        final PureComposeLayer combinedHeadLayer =
                new PureComposeLayer("combined-head",
                        spriteID -> {
                    final GameImage preassembled = new GameImage(HEAD_SHEET_DIMS);

                    preassembled.draw(headLayer.compose().getSprite(spriteID));
                    preassembled.draw(eyeLayer.compose().getSprite(spriteID));

                    final GameImage hair = hairLayer.compose().getSprite(spriteID),
                            hatMask = hatMaskLayer.compose().getSprite(spriteID);
                    alphaMask(hair, hatMask);

                    preassembled.draw(hair);
                    preassembled.draw(hatLayer.compose().getSprite(spriteID));
                    preassembled.draw(hairFront.compose().getSprite(spriteID));

                    final SpriteSheet combined =
                            new SpriteSheet(preassembled.submit(),
                                    HEAD_DIMS.width(), HEAD_DIMS.height());

                    return composeHead(combined).getSprite(spriteID);
                });

        final MaskLayer headMask = MLBuilder.init("head-mask",
                        combinedHeadBackLayer, combinedHeadLayer)
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
                combinedHeadBackLayer, bodyLayer,
                clothingLogic, combinedHeadLayer, headMask);
    }

    private DependentComponentLayer buildDCL(
            final String layerID, final AssetChoiceLayer ref, final int relativeIndex
    ) {
        return new DependentComponentLayer(layerID,
                AssetChoice.tempDefGetter(id, layerID), ref, relativeIndex);
    }

    private AssetChoiceLayer buildBody() {
        return ACLBuilder.of("body", this, Arrays.stream(BODY_IDs)
                .map(id -> new AssetChoiceTemplate(id, PokemonStyle::replace))
                .toArray(AssetChoiceTemplate[]::new))
                .setName("Body Type").build();
    }

    private AssetChoiceLayer buildEyes() {
        final String layerID = "eyes";
        final String[] ids = ParserUtils.readAssetCSV(id, layerID);

        return ACLBuilder.of(layerID, this, Arrays.stream(ids)
                .map(id -> new AssetChoiceTemplate(id, PokemonStyle::replace))
                .toArray(AssetChoiceTemplate[]::new))
                .setDims(HEAD_DIMS).setComposer(this::composeEyes).build();
    }

    private AssetChoiceLayer buildHair() {
        final String layerID = "hair";
        final String[] csv = ParserUtils.readAssetCSV(id, layerID);

        final AssetChoiceTemplate[] templates = Arrays.stream(csv)
                .map(s -> s.split(":")).map(s -> switch (s.length) {
                    case 1 -> new AssetChoiceTemplate(s[0],
                            PokemonStyle::replace);
                    case 2 -> {
                        final String code = s[0];
                        final int selections = Integer.parseInt(s[1]);

                        yield new AssetChoiceTemplate(code,
                                c -> replaceWithNSelections(c, selections),
                                hairAcc);
                    }
                    default -> null;
                }).toArray(AssetChoiceTemplate[]::new);

        return ACLBuilder.of(layerID, this, templates)
                .setComposer(this::composeOnHead).setName("Hairstyle")
                .setNoAssetChoice(NoAssetChoice.equal())
                .setDims(HEAD_DIMS).build();
    }

    private AssetChoiceLayer buildOutfit(
            final BodyType bt
    ) {
        return buildClothes(this, bt.prefix + "-outfit", topCS)
                .setName("Outfit").build();
    }

    private AssetChoiceLayer buildTop(
            final BodyType bt
    ) {
        return buildClothes(this, bt.prefix + "-top", topCS)
                .setName("Torso").build();
    }

    private AssetChoiceLayer buildBottom(
            final BodyType bt
    ) {
        return buildClothes(this, bt.prefix + "-bottom", botCS)
                .setName("Legs").build();
    }

    private AssetChoiceLayer buildShoes(
            final BodyType bt
    ) {
        return buildClothes(this, bt.prefix + "-shoes", shoeCS)
                .setName("Shoes").build();
    }

    private SpriteConstituent<String> composeEyes(
            final SpriteSheet sheet
    ) {
        return composeOnHead(sheet, -eyeHeightLayer.getValue(), true);
    }

    private SpriteConstituent<String> composeOnHead(final SpriteSheet sheet) {
        return composeOnHead(sheet, 0, false);
    }

    private SpriteConstituent<String> composeOnHead(
            final SpriteSheet sheet, final int augY, final boolean copyTiltedDown
    ) {
        final int max = copyTiltedDown ? 4 : 5;

        return id -> {
            final GameImage dest = new GameImage(HEAD_SHEET_DIMS);

            for (int x = 0; x < max; x++) {
                final GameImage source = sheet.getSprite(new Coord2D(x, 0));

                dest.draw(source, x * HEAD_DIMS.width(), augY);

                if (x == 0 && copyTiltedDown)
                    dest.draw(source, max * HEAD_DIMS.width(), augY);
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
