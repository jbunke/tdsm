package com.jordanbunke.tdsm.data.style;

import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.delta_time.utility.math.Pair;
import com.jordanbunke.tdsm.data.Animation;
import com.jordanbunke.tdsm.data.Animation.PlaybackMode;
import com.jordanbunke.tdsm.data.Directions;
import com.jordanbunke.tdsm.data.Directions.Dir;
import com.jordanbunke.tdsm.data.Directions.NumDirs;
import com.jordanbunke.tdsm.data.layer.AssetLayer;
import com.jordanbunke.tdsm.data.layer.ColorSelectionLayer;
import com.jordanbunke.tdsm.data.layer.Layers;
import com.jordanbunke.tdsm.data.layer.builders.ACLBuilder;
import com.jordanbunke.tdsm.data.layer.builders.ALBuilder;
import com.jordanbunke.tdsm.data.layer.support.AssetChoiceTemplate;
import com.jordanbunke.tdsm.data.layer.support.ColorSelection;
import com.jordanbunke.tdsm.data.layer.support.NoAssetChoice;

import java.awt.*;
import java.util.function.Function;

import static com.jordanbunke.tdsm.util.Colors.*;

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
        final ColorSelection skinTones = new ColorSelection(
                "Skin", false,
                new Color(0x7f, 0x49, 0x13),
                new Color(0x3f, 0x19, 0x00),
                new Color(0xbc, 0x87, 0x4b)),
                hairColors = new ColorSelection(
                        "Hair", true);
        final ColorSelectionLayer skinToneL = new ColorSelectionLayer(
                "skin-and-hair", skinTones, hairColors);
        final AssetLayer baseL = ALBuilder.init("base", this)
                .setColorReplacementFunc(VigilanteStyle::skinReplacement)
                .build();
        baseL.addInfluencingSelection(skinTones);
        baseL.addInfluencingSelection(hairColors);

        layers.add(
                skinToneL, baseL,
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

    private static Pair<Integer, Function<Color, Color>> skinReplacement(
            final Color input
    ) {
        final int TARGET_H = 120, LENIENCY = 2, MIN_SAT = 10,
                hue = hue(input), sat = sat(input), index;

        if (input.equals(new Color(0x80, 0, 0xff)))
            index = 1;
        else if (Math.abs(TARGET_H - hue) <= LENIENCY && sat >= MIN_SAT)
            index = 0;
        else
            index = -1;

        return new Pair<>(index, c -> {
            if (index == 1)
                return c;

            final double ch = rgbToHue(c), cv = rgbToValue(c),
                    is = rgbToSat(input), iv = rgbToValue(input);

            return fromHSV(ch, is, cv * iv);
        });
    }

    @Override
    public String name() {
        return "Vigilante";
    }
}
