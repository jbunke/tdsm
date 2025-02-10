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
import com.jordanbunke.tdsm.data.Edge;
import com.jordanbunke.tdsm.data.layer.CustomizationLayer;
import com.jordanbunke.tdsm.data.layer.Layers;
import com.jordanbunke.tdsm.util.EnumUtils;
import com.jordanbunke.tdsm.util.Layout;

import java.util.*;
import java.util.stream.IntStream;

import static com.jordanbunke.tdsm.util.Constants.*;

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

    // Sprite sheet sequencing
    private final Set<Animation> animationInclusion;
    private final Set<Directions.Dir> directionInclusion;
    private final List<Animation> animationOrder;
    private final List<Directions.Dir> directionOrder;

    // Sprite sheet layout
    private final Map<Edge, Integer> padding;

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

        animationInclusion = new HashSet<>();
        directionInclusion = new HashSet<>();

        animationOrder = new ArrayList<>();
        directionOrder = new ArrayList<>();

        padding = new HashMap<>();

        initSequencing();
        initLayout();
        update();
    }

    public GameImage renderSpriteSheet() {
        // TODO

        return GameImage.dummy();
    }

    private GameImage renderSpriteForExport(
            final Directions.Dir dir, final Animation anim, final int frame
    ) {
        final GameImage sprite = renderSprite(dir, anim, frame);
        return renderSpriteForExport(sprite);
    }

    private GameImage renderSpriteForExport(
            final GameImage spriteMapSprite
    ) {
        final Bounds2D dims = getExportSpriteDims();
        final Coord2D pos = new Coord2D(
                padding.get(Edge.LEFT), padding.get(Edge.TOP));

        final GameImage sprite = new GameImage(dims.width(), dims.height());
        sprite.draw(spriteMapSprite, pos.x, pos.y);

        return sprite.submit();
    }

    public GameImage renderSprite(
            final Directions.Dir dir, final Animation anim, final int frame
    ) {
        return map.getSprite(String.join(
                SpriteStates.STANDARD_SEPARATOR,
                directions.name(dir), anim.id, String.valueOf(frame)));
    }

    private void initSequencing() {
        Arrays.stream(animations).forEach(a -> {
            animationInclusion.add(a);
            animationOrder.add(a);
        });

        Arrays.stream(directions.order()).forEach(d -> {
            directionInclusion.add(d);
            directionOrder.add(d);
        });
    }

    private void initLayout() {
        EnumUtils.stream(Edge.class).forEach(e -> padding.put(e, 0));

        // TODO - horizontal / vertical
        // TODO - animations per dimension -- boundless?
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
        // TODO - proper handling of masking layers

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

    // override in inheritors if different
    public int getPreviewScaleUp() {
        return Layout.SPRITE_PREVIEW_SCALE_UP;
    }

    public abstract String name();

    // SEQUENCING
    public void updateAnimationInclusion(
            final Animation animation, final boolean included
    ) {
        if (included)
            animationInclusion.add(animation);
        else
            animationInclusion.remove(animation);
    }

    public void reorderAnimation(
            final Animation animation, final int newIndex
    ) {
        animationOrder.remove(animation);
        animationOrder.add(newIndex, animation);
    }

    public boolean isAnimationIncluded(
            final Animation animation
    ) {
        return animationInclusion.contains(animation);
    }

    public Animation[] animationExportOrder() {
        return animationOrder.toArray(Animation[]::new);
    }

    public void updateDirectionInclusion(
            final Directions.Dir dir, final boolean included
    ) {
        if (included)
            directionInclusion.add(dir);
        else
            directionInclusion.remove(dir);
    }

    public void reorderDirection(
            final Directions.Dir dir, final int newIndex
    ) {
        directionOrder.remove(dir);
        directionOrder.add(newIndex, dir);
    }

    public boolean isDirectionIncluded(
            final Directions.Dir dir
    ) {
        return directionInclusion.contains(dir);
    }

    public Directions.Dir[] directionExportOrder() {
        return directionOrder.toArray(Directions.Dir[]::new);
    }

    public boolean exportsASprite() {
        return !(directionInclusion.isEmpty() || animationInclusion.isEmpty());
    }

    public int exportFrameCount() {
        final int animFrames = animationInclusion.stream()
                .map(Animation::frameCount).reduce(0, Integer::sum);
        return animFrames * directionInclusion.size();
    }

    private GameImage firstIncludedSprite() {
        if (!exportsASprite())
            return null;

        Directions.Dir dir = null;
        Animation anim = null;

        for (Directions.Dir d : directionOrder)
            if (directionInclusion.contains(d)) {
                dir = d;
                break;
            }

        for (Animation a : animationOrder)
            if (animationInclusion.contains(a)) {
                anim = a;
                break;
            }

        assert anim != null;
        return renderSprite(dir, anim, 0);
    }

    public GameImage firstIncludedSpritePreview() {
        final GameImage fis = firstIncludedSprite();

        if (fis == null)
            return null;

        return renderSpriteForExport(fis);
    }

    // dims

    public boolean validateEdgePadding(final Edge edge, final int px) {
        final int t = edge == Edge.TOP ? px : padding.get(Edge.TOP),
                l = edge == Edge.LEFT ? px : padding.get(Edge.LEFT),
                b = edge == Edge.BOTTOM ? px : padding.get(Edge.BOTTOM),
                r = edge == Edge.RIGHT ? px : padding.get(Edge.RIGHT);
        final int w = l + dims.width() + r, h = t + dims.height() + b;

        return w >= MIN_SPRITE_EXPORT_W && w <= MAX_SPRITE_EXPORT_W &&
                h >= MIN_SPRITE_EXPORT_H && h <= MAX_SPRITE_EXPORT_H;
    }

    public void setEdgePadding(final Edge edge, final int px) {
        padding.put(edge, px);
    }

    public int getEdgePadding(final Edge edge) {
        return padding.get(edge);
    }

    public Bounds2D getExportSpriteDims() {
        final int t = padding.get(Edge.TOP), l = padding.get(Edge.LEFT),
                b = padding.get(Edge.BOTTOM), r = padding.get(Edge.RIGHT);
        return new Bounds2D(l + dims.width() + r, t + dims.height() + b);
    }

    // TODO - horizontal / vertical

    // TODO - animations per dimension -- boundless?
}
