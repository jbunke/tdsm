package com.jordanbunke.tdsm.data.style;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.sprite.SpriteAssembler;
import com.jordanbunke.delta_time.sprite.SpriteMap;
import com.jordanbunke.delta_time.sprite.SpriteSheet;
import com.jordanbunke.delta_time.sprite.SpriteStates;
import com.jordanbunke.delta_time.sprite.constituents.InterpretedSpriteSheet;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.data.Animation;
import com.jordanbunke.tdsm.data.Directions;
import com.jordanbunke.tdsm.data.layer.CustomizationLayer;
import com.jordanbunke.tdsm.data.layer.Layers;
import com.jordanbunke.tdsm.util.Layout;

import java.util.Arrays;
import java.util.stream.IntStream;

// TODO
public abstract class Style {

    static final int DIRECTION = 0, ANIM = 1, FRAME = 2;

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
        return map.getSprite(String.join(
                SpriteStates.STANDARD_SEPARATOR,
                directions.name(dir), anim.id, String.valueOf(frame)));
    }

    private SpriteStates<String> generateSpriteStates() {
        final int highestFrameCount = Arrays.stream(animations)
                .map(Animation::frameCount).reduce(1, Math::max);

        final SpriteStates<String> states = new SpriteStates<>(
                Arrays.stream(directions.order())
                        .map(directions::name)
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

    public final InterpretedSpriteSheet<String> defaultBuildComposer(
            final SpriteSheet sheet
    ) {
        final Coord2D FAIL = new Coord2D();

        return new InterpretedSpriteSheet<>(sheet, id -> {
            final Directions.Dir dir = Directions.get(
                    SpriteStates.extractContributor(DIRECTION, id));
            final String animID =
                    SpriteStates.extractContributor(ANIM, id);
            final int frame = Integer.parseInt(
                    SpriteStates.extractContributor(FRAME, id));

            final int dirIndex = indexOfDir(dir);
            final Animation anim = animFromID(animID);

            if (anim == null)
                return FAIL;

            if (directions.horizontal())
                return anim.coordFunc.apply(frame).displace(dirIndex, 0);
            else
                return anim.coordFunc.apply(frame).displace(0, dirIndex);
        });
    }

    final int indexOfDir(final Directions.Dir dir) {
        for (int i = 0; i < directions.order().length; i++)
            if (dir == directions.order()[i])
                return i;

        return -1;
    }

    final Animation animFromID(final String animID) {
        for (Animation a : animations)
            if (a.id.equals(animID))
                return a;

        return null;
    }

    public int getPreviewScaleUp() {
        return Layout.SPRITE_PREVIEW_SCALE_UP;
    }

    public abstract String name();
}
