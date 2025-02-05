package com.jordanbunke.tdsm.data.style;

import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.tdsm.data.Animation;
import com.jordanbunke.tdsm.data.Directions;
import com.jordanbunke.tdsm.data.layer.CustomizationLayer;

// TODO
public abstract class Style {
    private final String id;

    private final Bounds2D dims;
    private final Directions directions;
    private final Animation[] animations;
    private final CustomizationLayer[] layers;

    Style(
            final String id, final Bounds2D dims, final Directions directions,
            final Animation[] animations, final CustomizationLayer[] layers
    ) {
        this.id = id;
        this.dims = dims;
        this.directions = directions;
        this.animations = animations;
        this.layers = layers;
    }

    public abstract String name();
}
