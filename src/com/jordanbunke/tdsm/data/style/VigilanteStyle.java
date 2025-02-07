package com.jordanbunke.tdsm.data.style;

import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.data.Animation;
import com.jordanbunke.tdsm.data.Animation.PlaybackMode;
import com.jordanbunke.tdsm.data.Directions;
import com.jordanbunke.tdsm.data.Directions.Dir;
import com.jordanbunke.tdsm.data.Directions.NumDirs;
import com.jordanbunke.tdsm.data.layer.AssetChoiceLayer;
import com.jordanbunke.tdsm.data.layer.AssetChoiceTemplate;
import com.jordanbunke.tdsm.data.layer.Layers;

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
                Animation.make("walk", 3, new Coord2D(0, 1),
                        vertical, PlaybackMode.LOOP),
                Animation.make("death", 5, new Coord2D(0, 39),
                        vertical, PlaybackMode.LOOP),
                // TODO
        };
    }

    private void setUpLayers() {
        layers.add(
                new AssetChoiceLayer("base", this,
                        new AssetChoiceTemplate[] {
                                new AssetChoiceTemplate("black"),
                                new AssetChoiceTemplate("mixed"),
                                new AssetChoiceTemplate("white"),
                        }),
                new AssetChoiceLayer("torso", this,
                        new AssetChoiceTemplate[] {
                                new AssetChoiceTemplate("chestplate-1"),
                        }),
                new AssetChoiceLayer("headwear", this,
                        new AssetChoiceTemplate[] {
                                new AssetChoiceTemplate("helmet-1"),
                                new AssetChoiceTemplate("helmet-2"),
                        })
        );
    }

    @Override
    public String name() {
        return "Vigilante";
    }
}
