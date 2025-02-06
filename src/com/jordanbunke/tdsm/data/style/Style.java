package com.jordanbunke.tdsm.data.style;

import com.jordanbunke.delta_time.sprite.SpriteAssembler;
import com.jordanbunke.delta_time.sprite.SpriteMap;
import com.jordanbunke.delta_time.sprite.SpriteStates;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.tdsm.data.Animation;
import com.jordanbunke.tdsm.data.Directions;
import com.jordanbunke.tdsm.data.layer.CustomizationLayer;
import com.jordanbunke.tdsm.data.layer.Layers;

import java.util.Arrays;
import java.util.stream.IntStream;

// TODO
public abstract class Style {
    public final String id;

    private final Bounds2D dims;
    private final Directions directions;
    private final Animation[] animations;
    final Layers layers;

    Style(
            final String id, final Bounds2D dims, final Directions directions,
            final Animation[] animations, final Layers layers
    ) {
        this.id = id;
        this.dims = dims;
        this.directions = directions;
        this.animations = animations;
        this.layers = layers;
    }

    public void build() {
        final int highestFrameCount = Arrays.stream(animations)
                .map(Animation::frameCount).reduce(1, Math::max);

        final SpriteStates<String> states = new SpriteStates<>(
                Arrays.stream(directions.order())
                        .map(Directions.Dir::toString)
                        .toArray(String[]::new),
                Arrays.stream(animations)
                        .map(Animation::toString)
                        .toArray(String[]::new),
                IntStream.range(0, highestFrameCount)
                        .mapToObj(String::valueOf)
                        .toArray(String[]::new)
        );

        for (Animation anim : animations) {
            if (anim.frameCount() < highestFrameCount)
                for (int f = anim.frameCount(); f < highestFrameCount; f++)
                    states.removeMutuallyExclusiveContributors(
                            anim.toString(), String.valueOf(f));
        }

        for (String spriteID : states.getValidSpriteIDs())
            System.out.println(spriteID);

        final SpriteAssembler<String, String> assembler =
                new SpriteAssembler<>(dims.width(), dims.height());

        for (CustomizationLayer layer : layers.get())
            if (layer.rendered)
                assembler.addLayer(layer.id, layer.getComposer());

        final SpriteMap<String> map = new SpriteMap<>(assembler, states);
    }

    public abstract String name();
}
