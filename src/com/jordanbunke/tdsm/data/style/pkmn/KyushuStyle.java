package com.jordanbunke.tdsm.data.style.pkmn;

import com.jordanbunke.delta_time.image.GameImage;
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
import com.jordanbunke.tdsm.data.func.ColorReplacementFunc;
import com.jordanbunke.tdsm.data.layer.*;
import com.jordanbunke.tdsm.data.layer.builders.ACLBuilder;
import com.jordanbunke.tdsm.data.layer.builders.MLBuilder;
import com.jordanbunke.tdsm.data.layer.support.AssetChoice;
import com.jordanbunke.tdsm.data.layer.support.AssetChoiceTemplate;
import com.jordanbunke.tdsm.data.layer.support.ColorSelection;
import com.jordanbunke.tdsm.data.layer.support.NoAssetChoice;
import com.jordanbunke.tdsm.util.ParserUtils;

import java.awt.*;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.IntStream;

import static com.jordanbunke.tdsm.util.Colors.alphaMask;

public final class KyushuStyle extends PokemonStyle {
    private static final KyushuStyle INSTANCE;

    private static final String ID = "kyushu";
    private static final Bounds2D DIMS = new Bounds2D(40, 40),
            HEAD_DIMS = new Bounds2D(20, 20),
            HEAD_SHEET_DIMS = new Bounds2D(5 * HEAD_DIMS.width(),
                    HEAD_DIMS.height());

    private static final Color[] KYUSHU_EYES;

    private AssetChoiceLayer bodyLayer;

    private final ColorSelection skinTones, hairColors,
            eyebrowColors, eyeColors;

    private final ColorSelection[] hairAccCS, hatCS, topCS, botCS, shoeCS, capsuleCS;

    static {
        KYUSHU_EYES = new Color[] {
                new Color(0x7b4141),
                new Color(0x8b8b94),
                new Color(0x522041),
                new Color(0x442525)
        };

        INSTANCE = new KyushuStyle();
    }

    private enum BodyType {
        DEFAULT_MALE, DEFAULT_FEMALE;

        String prefix() {
            return switch (this) {
                case DEFAULT_MALE -> "dm";
                case DEFAULT_FEMALE -> "df";
            };
        }
    }

    private KyushuStyle() {
        super(ID, DIMS, setUpAnimations());

        skinTones = new ColorSelection("Skin", true, SKIN_SWATCHES);
        hairColors = new ColorSelection("Hair", true, HAIR_SWATCHES);
        eyebrowColors = new ColorSelection("Brows", true, HAIR_SWATCHES);
        eyeColors = new ColorSelection("Eye", true, KYUSHU_EYES);


        hairAccCS = IntStream.range(0, 2).mapToObj(i ->
                new ColorSelection(i == 0 ? "Accessory" : "Acc. 2",
                        true, CLOTHES_SWATCHES))
                .toArray(ColorSelection[]::new);
        hatCS = IntStream.range(0, 4).mapToObj(PokemonStyle::clothesSwatch)
                .toArray(ColorSelection[]::new);
        topCS = IntStream.range(0, 4).mapToObj(PokemonStyle::clothesSwatch)
                .toArray(ColorSelection[]::new);
        botCS = IntStream.range(0, 4).mapToObj(PokemonStyle::clothesSwatch)
                .toArray(ColorSelection[]::new);
        shoeCS = IntStream.range(0, 2).mapToObj(PokemonStyle::clothesSwatch)
                .toArray(ColorSelection[]::new);
        capsuleCS = IntStream.range(0, 3).mapToObj(PokemonStyle::clothesSwatch)
                .toArray(ColorSelection[]::new);

        bodyLayer = null;

        setUpLayers();
        update();
    }

    public static KyushuStyle get() {
        return INSTANCE;
    }

    private static Animation[] setUpAnimations() {
        final boolean horizontal = true;

        return new Animation[] {
                Animation.init(ANIM_ID_WALK, 3)
                        .setPlaybackMode(PlaybackMode.PONG)
                        .setCoordFunc(new Coord2D(), horizontal)
                        .setFrameTiming(10).build(),
                Animation.init(ANIM_ID_IDLE, 1)
                        .setCoordFunc(new Coord2D(1, 0), horizontal).build(),
                Animation.init(ANIM_ID_RUN, 3)
                        .setPlaybackMode(PlaybackMode.PONG)
                        .setCoordFunc(new Coord2D(3, 0), horizontal)
                        .setFrameTiming(6).build(),
                Animation.init(ANIM_ID_CYCLE, 3)
                        .setPlaybackMode(PlaybackMode.PONG)
                        .setCoordFunc(new Coord2D(6, 0), horizontal)
                        .setFrameTiming(6).build(),
                Animation.init(ANIM_ID_FISH, 4)
                        .setCoordFunc(new Coord2D(9, 0), horizontal).build(),
                Animation.init(ANIM_ID_SURF, 2)
                        .setCoordFunc(new Coord2D(13, 0), horizontal)
                        .setFrameTiming(15).build(),
                Animation.init(ANIM_ID_CAPSULE, 4)
                        .setCoordFunc(new Coord2D(15, 0), horizontal).build(),
        };
    }

    private void setUpLayers() {
        final ColorSelectionLayer skinLayer = new ColorSelectionLayer(
                "skin", "Skin Color", skinTones);

        bodyLayer = prepReplaceACLB("body")
                .setPreviewCoord(new Coord2D(DIMS.width(), 0))
                .setName("Body Type").build();
        bodyLayer.addInfluencingSelection(skinTones);

        final AssetChoiceLayer headLayer = prepReplaceACLB("head")
                .trivialComposer().setDims(HEAD_DIMS)
                .setName("Head Shape").build();
        headLayer.addInfluencingSelection(skinTones);

        final MathLayer eyeHeight = new MathLayer("eye-height", -1, 1, 0,
                i -> switch (i) {
            case -1 -> "Low";
            case 1 -> "High";
            default -> "Average";
        });
        final AssetChoiceLayer eyeLayer = buildEyes(eyeHeight);
        eyeLayer.addInfluencingSelections(skinTones, eyebrowColors, eyeColors);

        final ColorSelectionLayer eyeColorLayer =
                new ColorSelectionLayer("eye-color", eyeColors, eyebrowColors);

        final AssetChoiceLayer hairLayer = buildHair();
        hairLayer.addInfluencingSelections(skinTones, hairColors);

        final DependentComponentLayer
                hairBack = buildDCL("hair-back", hairLayer, -1),
                hairFront = buildDCL("hair-front", hairLayer, 1);

        final AssetChoiceLayer hatLayer = buildClothes(this, "hat", hatCS)
                .setName("Headwear").setComposer(this::composeOnHead)
                .setNoAssetChoice(NoAssetChoice.prob(0.75))
                .setDims(HEAD_DIMS).build();

        final DependentComponentLayer
                hatBack = buildDCL("hat-back", hatLayer, -1);

        final MaskLayer hatMaskLayer = MLBuilder.init("hat-mask", hairLayer)
                .trySetNaiveLogic(this, hatLayer).build(),
                hatBackMaskLayer = MLBuilder.init("hat-back-mask", hairBack)
                        .trySetNaiveLogic(this, hatBack).build();

        final ChoiceLayer clothingTypeLayer = new ChoiceLayer("outfit-type",
                "Separate articles", COMBINED_OUTFIT);

        final AssetChoiceLayer
                dmOutfitLayer = buildOutfit(BodyType.DEFAULT_MALE),
                dmTopsLayer = buildTop(BodyType.DEFAULT_MALE),
                dmBottomsLayer = buildBottom(BodyType.DEFAULT_MALE),
                dmShoesLayer = buildShoes(BodyType.DEFAULT_MALE);
        final GroupLayer dmArticlesLayer = new GroupLayer("outfit",
                "Outfit", dmBottomsLayer, dmShoesLayer, dmTopsLayer);

        final AssetChoiceLayer
                dfOutfitLayer = buildOutfit(BodyType.DEFAULT_FEMALE),
                dfTopsLayer = buildTop(BodyType.DEFAULT_FEMALE),
                dfBottomsLayer = buildBottom(BodyType.DEFAULT_FEMALE),
                dfShoesLayer = buildShoes(BodyType.DEFAULT_FEMALE);
        final GroupLayer dfArticlesLayer = new GroupLayer("outfit",
                "Outfit", dfBottomsLayer, dfShoesLayer, dfTopsLayer);

        AssetChoiceLayer.parallelMatchers(dmOutfitLayer, dfOutfitLayer);
        AssetChoiceLayer.parallelMatchers(dmBottomsLayer, dfBottomsLayer);
        AssetChoiceLayer.parallelMatchers(dmTopsLayer, dfTopsLayer);
        AssetChoiceLayer.parallelMatchers(dmShoesLayer, dfShoesLayer);

        @SuppressWarnings("all")
        final DecisionLayer clothingLogic = new DecisionLayer(
                "outfit", () -> {
            final boolean combined = clothingTypeLayer
                    .getChoice().equals(COMBINED_OUTFIT);

            // TODO - if more body shapes are added
            return switch (getBodyLayerChoice()) {
                case 0 -> combined ? dmOutfitLayer : dmArticlesLayer;
                default -> combined ? dfOutfitLayer : dfArticlesLayer;
            };
        });
        clothingTypeLayer.addDependent(clothingLogic);
        bodyLayer.addDependent(clothingLogic);

        // TODO
        final AssetChoiceLayer capsuleLayer = buildCapsule();

        layers.addToCustomization(skinLayer, bodyLayer, headLayer,
                eyeLayer, eyeColorLayer, eyeHeight, hairLayer,
                clothingTypeLayer, clothingLogic, hatLayer, capsuleLayer);

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
                new PureComposeLayer("combined-head", spriteID -> {
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

        // TODO - head mask

        layers.addToAssembly(
                combinedHeadBackLayer, bodyLayer, clothingLogic,
                combinedHeadLayer, capsuleLayer);
    }

    private DependentComponentLayer buildDCL(
            final String layerID, final AssetChoiceLayer ref, final int relativeIndex
    ) {
        return new DependentComponentLayer(layerID,
                AssetChoice.tempDefGetter(id, layerID), ref, relativeIndex);
    }

    private AssetChoiceLayer buildHair() {
        final String layerID = "hair";
        final String[] csv = ParserUtils.readAssetCSV(id, layerID);

        final AssetChoiceTemplate[] templates = Arrays.stream(csv)
                .map(s -> s.split(":")).map(s -> {
                    final String code = s[0];
                    final int numSels = Integer.parseInt(s[1]) + 1;

                    final ColorSelection[] sels = switch (numSels) {
                        case 3 -> new ColorSelection[] { hairAccCS[0],
                                hairAccCS[1], hairColors };
                        case 2 -> new ColorSelection[] { hairAccCS[0], hairColors };
                        default -> new ColorSelection[] { hairColors };
                    };

                    return new AssetChoiceTemplate(code,
                            c -> replaceWithNSelections(c, numSels), sels);
                }).toArray(AssetChoiceTemplate[]::new);

        return ACLBuilder.of(layerID, this, templates)
                .setComposer(this::composeOnHead).setName("Hairstyle")
                .setNoAssetChoice(NoAssetChoice.equal()).setDims(HEAD_DIMS)
                .build();
    }

    private AssetChoiceLayer buildEyes(final MathLayer eyeHeight) {
        return prepReplaceACLB("eyes").setDims(HEAD_DIMS)
                .setComposer(sheet -> composeEyes(sheet, eyeHeight)).build();
    }

    private ACLBuilder prepReplaceACLB(final String layerID) {
        return ACLBuilder.of(layerID, this,
                buildTemplates(ParserUtils.readAssetCSV(id, layerID)));
    }

    private AssetChoiceTemplate[] buildTemplates(final String... ids) {
        return buildTemplates(PokemonStyle::replace, ids);
    }

    private AssetChoiceTemplate[] buildTemplates(
            final ColorReplacementFunc f, final String... ids
    ) {
        return Arrays.stream(ids)
                .map(id -> new AssetChoiceTemplate(id, f))
                .toArray(AssetChoiceTemplate[]::new);
    }

    private AssetChoiceLayer buildCapsule() {
        return buildClothes("capsule", capsuleCS)
                .setDims(new Bounds2D(5, 5))
                .setPreviewCoord(new Coord2D())
                .setNoAssetChoice(NoAssetChoice.invalid())
                .setComposer(this::composeCapsule).build();
    }

    private AssetChoiceLayer buildOutfit(final BodyType bt) {
        return buildClothes(bt.prefix() + "-outfit", topCS)
                .setName("Outfit").build();
    }

    private AssetChoiceLayer buildTop(
            final BodyType bt
    ) {
        return buildClothes(bt.prefix() + "-top", topCS)
                .setName("Torso").build();
    }

    private AssetChoiceLayer buildBottom(
            final BodyType bt
    ) {
        return buildClothes(bt.prefix() + "-bottom", botCS)
                .setName("Legs").build();
    }

    private AssetChoiceLayer buildShoes(
            final BodyType bt
    ) {
        return buildClothes(bt.prefix() + "-shoes", shoeCS)
                .setName("Shoes").build();
    }

    private ACLBuilder buildClothes(
            final String layerID, final ColorSelection[] selections
    ) {
        return buildClothes(this, layerID, selections)
                .setPreviewCoord(new Coord2D(DIMS.width(), 0));
    }

    private SpriteConstituent<String> composeCapsule(final SpriteSheet sheet) {
        return id -> {
            final GameImage sprite = new GameImage(DIMS.width(), DIMS.height());

            final Directions.Dir dir = Directions.get(
                    SpriteStates.extractContributor(DIRECTION, id));
            final String animID =
                    SpriteStates.extractContributor(ANIM, id);
            final int frame = Integer.parseInt(
                    SpriteStates.extractContributor(FRAME, id));

            if (!animID.equals(ANIM_ID_CAPSULE) ||
                    !Dir.DOWN.equals(dir) || frame == 0)
                return sprite;

            final Coord2D offset = switch (frame) {
                case 1 -> new Coord2D(15, 25);
                case 2 -> new Coord2D(22, 24);
                case 3 -> new Coord2D(23, 21);
                default -> new Coord2D();
            };

            sprite.draw(sheet.getSprite(new Coord2D()), offset.x, offset.y);
            return sprite.submit();
        };
    }

    private SpriteConstituent<String> composeEyes(
            final SpriteSheet sheet, final MathLayer eyeHeight
    ) {
        return composeOnHead(sheet, -eyeHeight.getValue(), true);
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

            if (animID.equals(ANIM_ID_CAPSULE) && !Dir.DOWN.equals(dir))
                return sprite;

            final Coord2D offset = headOffset(id);
            final int BASE_X = 10, BASE_Y = 10,
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

        final int animComp, frameComp, x;

        // frame functions
        final Function<Integer, Integer> rock3 = f -> f % 2 == 0 ? f - 1 : 0,
                fishSide = f -> switch (f) {
            case 0 -> 3;
            case 2 -> 1;
            default -> 2;
        };

        x = switch (animID) {
            case ANIM_ID_CYCLE -> rock3.apply(frame);
            case ANIM_ID_FISH -> switch (dir) {
                case DOWN -> frame == 3 ? 0 : 1;
                case UP -> frame > 1 ? 0 : -1;
                case RIGHT -> fishSide.apply(frame) * -1;
                default -> fishSide.apply(frame);
            };
            case ANIM_ID_CAPSULE -> switch (frame) {
                case 1, 4 -> -1;
                default -> 0;
            };
            case ANIM_ID_SURF -> switch (dir) {
                case LEFT -> 1;
                case RIGHT -> -1;
                default -> 0;
            };
            default -> 0;
        };

        // animation Y offset component
        animComp = switch (animID) {
            case ANIM_ID_IDLE, ANIM_ID_WALK -> switch (dir) {
                case DOWN -> 2;
                case UP -> 4;
                default -> 3;
            };
            case ANIM_ID_RUN -> 2 + (dir.equals(Dir.DOWN) ? 0 : 1);
            case ANIM_ID_CYCLE -> 1 + (dir.equals(Dir.UP) ? 1 : 0);
            case ANIM_ID_SURF -> -4 - switch (dir) {
                case DOWN -> 2;
                case UP -> 0;
                default -> 1;
            };
            case ANIM_ID_FISH -> dir == Dir.DOWN ? -4 : 3;
            case ANIM_ID_CAPSULE -> 2;
            default -> 0;
        };

        // animation frame Y offset component
        frameComp = switch (animID) {
            case ANIM_ID_WALK, ANIM_ID_RUN -> frame != 1 ? 1 : 0;
            case ANIM_ID_SURF -> frame;
            case ANIM_ID_FISH -> switch (dir) {
                case UP -> frame == 3 ? -1 : 0;
                case DOWN -> frame == 2 ? 1 : 0;
                default -> frame % 2 == 0 ? 1 : 0;
            };
            case ANIM_ID_CAPSULE -> frame == 3 ? -1 : 0;
            default -> 0;
        };

        return new Coord2D(x, animComp + frameComp);
    }

    private Coord2D headDirX(final String spriteID) {
        final Directions.Dir dir = Directions.get(
                SpriteStates.extractContributor(DIRECTION, spriteID));
        final String animID =
                SpriteStates.extractContributor(ANIM, spriteID);

        final int x = indexOfDir(dir);

        final boolean tiltedDown = dir.equals(Directions.Dir.DOWN) &&
                animID.equals(ANIM_ID_RUN);

        return new Coord2D(x + (tiltedDown ? 4 : 0), 0);
    }

    private int getBodyLayerChoice() {
        if (bodyLayer == null)
            return 0;

        return bodyLayer.getChoiceIndex();
    }
}
