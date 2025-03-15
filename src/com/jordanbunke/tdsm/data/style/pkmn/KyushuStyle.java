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
import com.jordanbunke.tdsm.data.layer.AssetChoiceLayer;
import com.jordanbunke.tdsm.data.layer.ColorSelectionLayer;
import com.jordanbunke.tdsm.data.layer.PureComposeLayer;
import com.jordanbunke.tdsm.data.layer.builders.ACLBuilder;
import com.jordanbunke.tdsm.data.layer.support.AssetChoiceTemplate;
import com.jordanbunke.tdsm.data.layer.support.ColorSelection;

import java.util.Arrays;
import java.util.function.Function;

public final class KyushuStyle extends PokemonStyle {
    private static final KyushuStyle INSTANCE;

    private static final String ID = "kyushu";
    private static final Bounds2D DIMS = new Bounds2D(40, 40),
            HEAD_DIMS = new Bounds2D(20, 20),
            HEAD_SHEET_DIMS = new Bounds2D(5 * HEAD_DIMS.width(),
                    HEAD_DIMS.height());

    private AssetChoiceLayer bodyLayer;

    private final ColorSelection skinTones;

    static {
        INSTANCE = new KyushuStyle();
    }

    private KyushuStyle() {
        super(ID, DIMS, setUpAnimations());

        skinTones = new ColorSelection("Skin", true, SKIN_SWATCHES);
        // TODO - color selections

        bodyLayer = null;

        setUpLayers();
        update();
    }

    public static KyushuStyle get() {
        return INSTANCE;
    }

    private static Animation[] setUpAnimations() {
        final boolean horizontal = true;

        // TODO - uncomment animations as implemented
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
//                Animation.init(ANIM_ID_FISH, 4)
//                        .setCoordFunc(new Coord2D(9, 0), horizontal).build(),
                Animation.init(ANIM_ID_SURF, 2)
                        .setCoordFunc(new Coord2D(13, 0), horizontal)
                        .setFrameTiming(15).build(),
//                Animation.init(ANIM_ID_CAPSULE, 5)
//                        .setCoordFunc(new Coord2D(15, 0), horizontal).build(),
        };
    }

    private void setUpLayers() {
        final ColorSelectionLayer skinLayer = new ColorSelectionLayer(
                "skin", "Skin Color", skinTones);

        bodyLayer = ACLBuilder.of("body", this,
                buildTemplates("player-example"))
                .setPreviewCoord(new Coord2D(DIMS.width(), 0))
                .setName("Body Type").build();
        bodyLayer.addInfluencingSelection(skinTones);

        final AssetChoiceLayer headLayer = ACLBuilder.of("head", this,
                buildTemplates("standard-head")).trivialComposer()
                .setDims(HEAD_DIMS).setName("Head Shape").build();
        headLayer.addInfluencingSelection(skinTones);

        // TODO

        layers.addToCustomization(skinLayer, bodyLayer, headLayer);

        final PureComposeLayer combinedHeadLayer =
                new PureComposeLayer("combined-head", spriteID -> {
                    final GameImage preassembled = new GameImage(HEAD_SHEET_DIMS);

                    preassembled.draw(headLayer.compose().getSprite(spriteID));
                    // TODO

                    final SpriteSheet combined =
                            new SpriteSheet(preassembled.submit(),
                                    HEAD_DIMS.width(), HEAD_DIMS.height());

                    return composeHead(combined).getSprite(spriteID);
                });

        layers.addToAssembly(bodyLayer, combinedHeadLayer);
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

    private SpriteConstituent<String> composeHead(final SpriteSheet sheet) {
        final SpriteConstituent<String> assetFetcher =
                new InterpretedSpriteSheet<>(sheet, this::headDirX);

        return id -> {
            final GameImage sprite = new GameImage(DIMS.width(), DIMS.height());

            final Directions.Dir dir = Directions.get(
                    SpriteStates.extractContributor(DIRECTION, id));
            final String animID =
                    SpriteStates.extractContributor(ANIM, id);

            if (animID.equals(ANIM_ID_CAPSULE) && !dir.equals(Directions.Dir.DOWN))
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
            // TODO - capsule, fish
            default -> 0;
        };

        // animation frame Y offset component
        frameComp = switch (animID) {
            case ANIM_ID_WALK, ANIM_ID_RUN -> frame != 1 ? 1 : 0;
            case ANIM_ID_SURF -> frame;
            // TODO - capsule, fish
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
}
