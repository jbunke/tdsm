package com.jordanbunke.tdsm.data.style;

import com.jordanbunke.delta_time.image.GameImage;
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

    public final Bounds2D dims;
    public final Directions directions;
    public final Animation[] animations;
    final Layers layers;

    private final SpriteStates<String> states;
    private SpriteMap<String> map;

    Style(
            final String id, final Bounds2D dims, final Directions directions,
            final Animation[] animations, final Layers layers
    ) {
        this.id = id;
        this.dims = dims;
        this.directions = directions;
        this.animations = animations;
        this.layers = layers;

        states = generateSpriteStates();
        update();
    }

    public GameImage renderSprite(
            final Directions.Dir dir, final Animation anim, final int frame
    ) {
        return map.getSprite(
                String.join(SpriteStates.STANDARD_SEPARATOR,
                        dir.toString(), anim.id, String.valueOf(frame)));
    }

    private SpriteStates<String> generateSpriteStates() {
        final int highestFrameCount = Arrays.stream(animations)
                .map(Animation::frameCount).reduce(1, Math::max);

        final SpriteStates<String> states = new SpriteStates<>(
                Arrays.stream(directions.order())
                        .map(Directions.Dir::toString)
                        .toArray(String[]::new),
                Arrays.stream(animations)
                        .map(a -> a.id)
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

        return states;
    }

    public void randomize() {
        layers.get().forEach(CustomizationLayer::randomize);
        update();
    }

    public void update() {
        final SpriteAssembler<String, String> assembler =
                new SpriteAssembler<>(dims.width(), dims.height());

        for (CustomizationLayer layer : layers.get())
            if (layer.rendered)
                assembler.addLayer(layer.id, layer.getComposer());

        map = new SpriteMap<>(assembler, states);
    }

    final int indexOfDir(final Directions.Dir dir) {
        for (int i = 0; i < directions.order().length; i++)
            if (dir == directions.order()[i])
                return i;

        return -1;
    }

    final int startingIndexForAnim(final String anim) {
        int totalFrames = 0;

        for (Animation a : animations) {
            if (a.id.equals(anim))
                return totalFrames;
            else
                totalFrames += a.frameCount();
        }

        return -1;
    }

    public abstract String name();
}
