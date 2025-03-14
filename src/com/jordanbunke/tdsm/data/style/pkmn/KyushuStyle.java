package com.jordanbunke.tdsm.data.style.pkmn;

import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.data.Animation;
import com.jordanbunke.tdsm.data.Animation.PlaybackMode;
import com.jordanbunke.tdsm.data.func.ColorReplacementFunc;
import com.jordanbunke.tdsm.data.layer.AssetChoiceLayer;
import com.jordanbunke.tdsm.data.layer.ColorSelectionLayer;
import com.jordanbunke.tdsm.data.layer.builders.ACLBuilder;
import com.jordanbunke.tdsm.data.layer.support.AssetChoiceTemplate;
import com.jordanbunke.tdsm.data.layer.support.ColorSelection;

import java.util.Arrays;

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
                Animation.init(ANIM_ID_FISH, 4)
                        .setCoordFunc(new Coord2D(9, 0), horizontal).build(),
                Animation.init(ANIM_ID_SURF, 2)
                        .setCoordFunc(new Coord2D(13, 0), horizontal)
                        .setFrameTiming(15).build(),
                Animation.init(ANIM_ID_CAPSULE, 5)
                        .setCoordFunc(new Coord2D(15, 0), horizontal).build(),
        };
    }

    private void setUpLayers() {
        final ColorSelectionLayer skinLayer = new ColorSelectionLayer(
                "skin", "Skin Color", skinTones);

        bodyLayer = ACLBuilder.of("body", this,
                buildTemplates("player-example")).setName("Body Type").build();
        bodyLayer.addInfluencingSelection(skinTones);

        final AssetChoiceLayer headLayer = ACLBuilder.of("head", this,
                buildTemplates("standard-head")).trivialComposer()
                .setDims(HEAD_DIMS).setName("Head Shape").build();
        headLayer.addInfluencingSelection(skinTones);

        // TODO

        layers.addToCustomization(skinLayer, bodyLayer, headLayer);

        // TODO

        layers.addToAssembly(bodyLayer);
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
}
