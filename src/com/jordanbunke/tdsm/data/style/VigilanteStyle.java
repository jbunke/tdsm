package com.jordanbunke.tdsm.data.style;

import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.data.Animation;
import com.jordanbunke.tdsm.data.Animation.PlaybackMode;
import com.jordanbunke.tdsm.data.Directions;
import com.jordanbunke.tdsm.data.Directions.Dir;
import com.jordanbunke.tdsm.data.Directions.NumDirs;
import com.jordanbunke.tdsm.data.func.ColorReplacementFunc;
import com.jordanbunke.tdsm.data.layer.ColorSelectionLayer;
import com.jordanbunke.tdsm.data.layer.support.AssetChoiceTemplate;
import com.jordanbunke.tdsm.data.layer.Layers;
import com.jordanbunke.tdsm.data.layer.builders.ACLBuilder;
import com.jordanbunke.tdsm.data.layer.support.ColorSelection;
import com.jordanbunke.tdsm.data.layer.support.NoAssetChoice;

import java.awt.*;

public final class VigilanteStyle extends Style {
    private static final VigilanteStyle INSTANCE;

    private static final String ID = "classic";
    private static final Bounds2D DIMS = new Bounds2D(32, 32);

    static {
        INSTANCE = new VigilanteStyle();
    }

    private VigilanteStyle() {
        super(ID, DIMS, setUpDirections(), setUpAnimations(), new Layers());

        setUpLayers();
        update();
    }

    public static VigilanteStyle get() {
        return INSTANCE;
    }

    private static Directions setUpDirections() {
        return new Directions(NumDirs.FOUR, true,
                Dir.RIGHT, Dir.LEFT, Dir.DOWN, Dir.UP);
    }

    private static Animation[] setUpAnimations() {
        final boolean vertical = false;

        return new Animation[] {
                Animation.make("idle", 1, new Coord2D(0, 0),
                        vertical, PlaybackMode.LOOP),
                Animation.make("walk", 2, new Coord2D(0, 1),
                        vertical, PlaybackMode.LOOP),
                Animation.make("box", 2, new Coord2D(0, 3),
                        vertical, PlaybackMode.LOOP),
                Animation.make("reload_1", 3, new Coord2D(0, 18),
                        vertical, PlaybackMode.LOOP),
                Animation.make("reload_2", 3, new Coord2D(0, 30),
                        vertical, PlaybackMode.LOOP),
                Animation.make("death", 5, new Coord2D(0, 39),
                        vertical, PlaybackMode.LOOP)
                // TODO
        };
    }

    private void setUpLayers() {
        final ColorSelectionLayer skinTone = new ColorSelectionLayer(
                "skin-tone", new ColorSelection("", false,
                new Color(0x7f, 0x49, 0x13),
                new Color(0x3f, 0x19, 0x00),
                new Color(0xbc, 0x87, 0x4b))),
                ex1 = new ColorSelectionLayer("Example 1",
                        new ColorSelection("Sel 1", true),
                        new ColorSelection("Sel 2", true),
                        new ColorSelection("Sel 3", true),
                        new ColorSelection("Sel 4", true),
                        new ColorSelection("Sel 5", true),
                        new ColorSelection("Sel 6", true),
                        new ColorSelection("Sel 7", true),
                        new ColorSelection("Sel 8", true),
                        new ColorSelection("Sel 9", true)),
                ex2 = new ColorSelectionLayer("Example 2",
                        new ColorSelection("Hair", true),
                        new ColorSelection("Brows", true),
                        new ColorSelection("Other", true));

        layers.add(
                skinTone, ex1, ex2,
                ACLBuilder.of("base", this,
                                new AssetChoiceTemplate("black"),
                                new AssetChoiceTemplate("mixed"),
                                new AssetChoiceTemplate("mixed",
                                        new ColorSelection[] {
                                                new ColorSelection("Nr. 1", false),
                                                new ColorSelection("Nr. 2", true),
                                                new ColorSelection("Nr. 3", true),
                                                new ColorSelection("Nr. 4", true),
                                                new ColorSelection("Nr. 5", true),
                                                // new ColorSelection("Nr. 6", true),
                                        },
                                        ColorReplacementFunc.trivial()),
                                new AssetChoiceTemplate("mixed"),
                                new AssetChoiceTemplate("white"))
                        .build(),
                ACLBuilder.of("torso", this,
                                new AssetChoiceTemplate("chestplate-1"))
                        .setNoAssetChoice(NoAssetChoice.equal()).build(),
                ACLBuilder.of("headwear", this,
                                new AssetChoiceTemplate("helmet-1"),
                                new AssetChoiceTemplate("helmet-2"))
                        .setNoAssetChoice(NoAssetChoice.prob(0.1))
                        .build()
        );
    }

    @Override
    public String name() {
        return "Vigilante";
    }
}
