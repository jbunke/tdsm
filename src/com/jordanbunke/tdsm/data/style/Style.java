package com.jordanbunke.tdsm.data.style;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.MenuBuilder;
import com.jordanbunke.delta_time.sprite.SpriteAssembler;
import com.jordanbunke.delta_time.sprite.SpriteMap;
import com.jordanbunke.delta_time.sprite.SpriteSheet;
import com.jordanbunke.delta_time.sprite.SpriteStates;
import com.jordanbunke.delta_time.sprite.constituents.InterpretedSpriteSheet;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.delta_time.utility.math.Pair;
import com.jordanbunke.tdsm.data.Animation;
import com.jordanbunke.tdsm.data.Directions;
import com.jordanbunke.tdsm.data.Edge;
import com.jordanbunke.tdsm.data.Orientation;
import com.jordanbunke.tdsm.data.layer.*;
import com.jordanbunke.tdsm.io.json.JSONArray;
import com.jordanbunke.tdsm.io.json.JSONBuilder;
import com.jordanbunke.tdsm.io.json.JSONObject;
import com.jordanbunke.tdsm.io.json.JSONPair;
import com.jordanbunke.tdsm.util.EnumUtils;
import com.jordanbunke.tdsm.util.Layout;

import java.util.*;
import java.util.stream.IntStream;

import static com.jordanbunke.tdsm.util.Constants.*;

public abstract class Style {

    static final int DIRECTION = 0, ANIM = 1, FRAME = 2;

    public final String id;

    public final Bounds2D dims;
    public final Directions directions;
    public final Animation[] animations;
    public final Layers layers;

    private final SpriteStates<String> states;
    private SpriteMap<String> map;

    // Sprite sheet sequencing
    private final Set<Animation> animationInclusion;
    private final Set<Directions.Dir> directionInclusion;
    private final List<Animation> animationOrder;
    private final List<Directions.Dir> directionOrder;

    // Sprite sheet layout
    private final Map<Edge, Integer> padding;
    private Orientation animationOrientation;
    private boolean multipleAnimsPerDim, singleDim, wrapAnimsAcrossDims;
    private int framesPerDim;

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

        resetSequencing();
        resetPadding();
        resetLayout();

        update();
    }

    public GameImage renderSpriteSheet() {
        final Bounds2D spriteDims = getExportSpriteDims();
        final int spriteW = spriteDims.width(), spriteH = spriteDims.height(),
                spritesX = getSpritesX(), spritesY = getSpritesY(),
                w = spriteW * spritesX, h = spriteH * spritesY;
        final GameImage spriteSheet = new GameImage(w, h);

        final Directions.Dir[] dirs = directionOrder.stream()
                .filter(directionInclusion::contains)
                .toArray(Directions.Dir[]::new);
        final Animation[] anims = animationOrder.stream()
                .filter(animationInclusion::contains)
                .toArray(Animation[]::new);

        for (int d = 0; d < dirs.length; d++) {
            final Directions.Dir dir = dirs[d];

            for (int a = 0; a < anims.length; a++) {
                final Animation anim = anims[a];

                for (int f = 0; f < anim.frameCount(); f++) {
                    final GameImage sprite = renderSpriteForExport(dir, anim, f);
                    final Coord2D coord = getSpriteCoord(
                            dirs.length, anims, d, a, f);

                    spriteSheet.draw(sprite,
                            spriteW * coord.x, spriteH * coord.y);
                }
            }
        }

        return spriteSheet.submit();
    }

    public Bounds2D getSpriteSheetDims() {
        final Bounds2D spriteDims = getExportSpriteDims();
        final int spriteW = spriteDims.width(), spriteH = spriteDims.height(),
                spritesX = getSpritesX(), spritesY = getSpritesY(),
                w = spriteW * spritesX, h = spriteH * spritesY;

        return new Bounds2D(w, h);
    }

    public String buildJSON() {
        final JSONBuilder jb = new JSONBuilder();

        final Bounds2D spriteDims = getExportSpriteDims();
        final int spriteW = spriteDims.width(), spriteH = spriteDims.height(),
                spritesX = getSpritesX(), spritesY = getSpritesY(),
                w = spriteW * spritesX, h = spriteH * spritesY;

        jb.add(new JSONPair("size", new JSONObject(
                new JSONPair("w", w), new JSONPair("h", h),
                new JSONPair("sprite_w", spriteW),
                new JSONPair("sprite_h", spriteH))));

        final List<JSONObject> frames = new ArrayList<>();

        final Directions.Dir[] dirs = directionOrder.stream()
                .filter(directionInclusion::contains)
                .toArray(Directions.Dir[]::new);
        final Animation[] anims = animationOrder.stream()
                .filter(animationInclusion::contains)
                .toArray(Animation[]::new);

        jb.add(new JSONPair("data", new JSONObject(
                new JSONPair("directions", new JSONArray<>(
                        Arrays.stream(dirs).map(directions::name)
                                .map(s -> "\"" + s + "\"")
                                .toArray(String[]::new))),
                new JSONPair("animations", new JSONArray<>(
                        Arrays.stream(anims).map(a -> new JSONObject(
                                new JSONPair("id", a.id),
                                new JSONPair("frame_count", a.frameCount())
                        )).toArray(JSONObject[]::new))))));

        for (int d = 0; d < dirs.length; d++) {
            final Directions.Dir dir = dirs[d];

            for (int a = 0; a < anims.length; a++) {
                final Animation anim = anims[a];

                for (int f = 0; f < anim.frameCount(); f++) {
                    final Coord2D coord = getSpriteCoord(
                            dirs.length, anims, d, a, f);

                    final String dirID = directions.name(dir),
                            id = String.join(SpriteStates.STANDARD_SEPARATOR,
                                    dirID, anim.id, String.valueOf(f));
                    final JSONObject frame = new JSONObject(
                            new JSONPair("id", id),
                            new JSONPair("direction", dirID),
                            new JSONPair("animation", anim.id),
                            new JSONPair("anim_frame", f),
                            new JSONPair("x", coord.x * spriteW),
                            new JSONPair("y", coord.y * spriteH),
                            new JSONPair("coord_x", coord.x),
                            new JSONPair("coord_y", coord.y),
                            new JSONPair("w", spriteW),
                            new JSONPair("h", spriteH));
                    frames.add(frame);
                }
            }
        }

        jb.add(new JSONPair("frames",
                new JSONArray<>(frames.toArray(JSONObject[]::new))));

        return jb.write();
    }

    private Coord2D getSpriteCoord(
            final int dirCount, final Animation[] anims,
            final int d, final int a, final int f
    ) {
        final Coord2D coord = getSpriteCoordGeneric(dirCount, anims, d, a, f);
        final int animDim = coord.x, dirDim = coord.y;

        return animationOrientation == Orientation.HORIZONTAL
                ? coord
                : new Coord2D(dirDim, animDim);
    }

    private Coord2D getSpriteCoordGeneric(
            final int dirCount, final Animation[] anims,
            final int d, final int a, final int f
    ) {
        final int animDim, dirDim;

        // pre-processing
        int framesProcessed = 0;

        for (int i = 0; i < a; i++)
            framesProcessed += anims[i].frameCount();

        final int totalF = framesProcessed + f;

        // coordinate logic
        if (multipleAnimsPerDim) {
            if (singleDim) {
                animDim = totalF;
                dirDim = d;
            } else if (wrapAnimsAcrossDims) {
                animDim = totalF % framesPerDim;
                dirDim = (dirCount * (totalF / framesPerDim)) + d;
            } else {
                final int[] framesPerAnim = Arrays.stream(anims)
                        .map(Animation::frameCount)
                        .mapToInt(i -> i).toArray();

                int dim = 0, framesInDim = 0;

                for (int i = 0; i <= a; i++) {
                    final int animFrameCount = framesPerAnim[i];

                    if (framesInDim + animFrameCount > framesPerDim) {
                        dim++;
                        framesInDim = 0;
                    }

                    if (i < a) {
                        framesInDim += animFrameCount;
                    }
                }

                animDim = framesInDim + f;
                dirDim = (dirCount * dim) + d;
            }
        } else {
            animDim = f;
            dirDim = (dirCount * a) + d;
        }

        return new Coord2D(animDim, dirDim);
    }

    private int getSpritesX() {
        return getSpritesDim(Orientation.HORIZONTAL);
    }

    private int getSpritesY() {
        return getSpritesDim(Orientation.VERTICAL);
    }

    private int getSpritesDim(final Orientation check) {
        if (animationOrientation == check) {
            if (multipleAnimsPerDim)
                return singleDim ? animationInclusion.stream()
                        .map(Animation::frameCount).reduce(0, Integer::sum)
                        : framesPerDim;

            return longestAnimFrameCount();
        } else {
            final int dirCount = directionInclusion.size();

            if (multipleAnimsPerDim) {
                if (singleDim)
                    return dirCount;
                else {
                    final int animFrames = animationInclusion.stream()
                            .map(Animation::frameCount).reduce(0, Integer::sum);

                    if (wrapAnimsAcrossDims)
                        return dirCount * ((animFrames / framesPerDim) +
                                (animFrames % framesPerDim > 0 ? 1 : 0));
                    else {
                        final int[] framesPerAnim = animationOrder.stream()
                                .filter(animationInclusion::contains)
                                .map(Animation::frameCount)
                                .mapToInt(i -> i).toArray();

                        int dims = 1, framesInDim = 0;

                        for (int animFrameCount : framesPerAnim) {
                            if (framesInDim + animFrameCount > framesPerDim) {
                                dims++;
                                framesInDim = animFrameCount;
                            } else {
                                framesInDim += animFrameCount;
                            }
                        }

                        return dirCount * dims;
                    }
                }
            } else
                return dirCount * animationInclusion.size();
        }
    }

    private void addLayerIfRendered(
            final List<CustomizationLayer> renderLayers,
            final CustomizationLayer candidate
    ) {
        if (candidate instanceof DecisionLayer dl)
            addLayerIfRendered(renderLayers, dl.getDecision());
        else if (candidate instanceof GroupLayer gl)
            gl.all().forEach(l -> addLayerIfRendered(renderLayers, l));
        else if (candidate.isRendered())
            renderLayers.add(candidate);
    }

    public List<Pair<String, GameImage>> renderStipExport() {
        final List<CustomizationLayer> renderLayers = new ArrayList<>();
        final List<Pair<String, GameImage>> stipRep = new ArrayList<>();

        // Set up render layers
        for (CustomizationLayer layer : layers.get())
            addLayerIfRendered(renderLayers, layer);


        for (CustomizationLayer layer : renderLayers) {
            // Skip empty layers
            if (layer instanceof AssetChoiceLayer acl && !acl.hasChoice())
                continue;

            renderLayers.stream().map(l -> l.id)
                    .forEach(map.assembler::disableLayer);

            map.assembler.enableLayer(layer.id);
            map.redraw();

            final Pair<String, GameImage> layerRep = new Pair<>(
                    layer.name(), renderSpriteSheet());
            stipRep.add(layerRep);
        }

        // Reset layer settings
        renderLayers.stream().map(l -> l.id)
                .forEach(map.assembler::enableLayer);
        map.redraw();

        return stipRep;
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

    public void resetSequencing() {
        animationInclusion.clear();
        directionInclusion.clear();
        animationOrder.clear();
        directionOrder.clear();

        Arrays.stream(animations).forEach(a -> {
            animationInclusion.add(a);
            animationOrder.add(a);
        });

        Arrays.stream(directions.order()).forEach(d -> {
            directionInclusion.add(d);
            directionOrder.add(d);
        });
    }

    public void resetPadding() {
        padding.clear();
        EnumUtils.stream(Edge.class).forEach(e -> padding.put(e, 0));
    }

    public void resetLayout() {
        animationOrientation = Orientation.HORIZONTAL;
        multipleAnimsPerDim = false;
        singleDim = true;
        framesPerDim = DEF_FRAMES_PER_DIM;
        wrapAnimsAcrossDims = false;
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
                            anim.id, String.valueOf(f));
        }

        return states;
    }

    public void randomize() {
        layers.get().forEach(l -> l.randomize(false));
        update();
    }

    public void update() {
        final SpriteAssembler<String, String> assembler =
                new SpriteAssembler<>(dims.width(), dims.height());

        for (CustomizationLayer layer : layers.get())
            addLayerToAssembler(assembler, layer);

        considerations(assembler);

        map = new SpriteMap<>(assembler, states);
    }

    void considerations(final SpriteAssembler<String, String> assembler) {}

    private void addLayerToAssembler(
            final SpriteAssembler<String, String> assembler,
            final CustomizationLayer layer
    ) {
        if (layer instanceof DecisionLayer dl)
            addLayerToAssembler(assembler, dl.getDecision());
        else if (layer instanceof MaskLayer ml)
            assembler.addMask(ml.id, ml.compose(), ml.getTarget().id);
        else if (layer instanceof GroupLayer gl)
            gl.all().forEach(l -> addLayerToAssembler(assembler, l));
        else if (layer.isRendered())
            assembler.addLayer(layer.id, layer.compose());
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
    public abstract boolean shipping();
    public abstract boolean hasSettings();
    public abstract void buildSettingsMenu(final MenuBuilder mb);

    // SEQUENCING
    public void updateAnimationInclusion(
            final Animation animation, final boolean included
    ) {
        if (included)
            animationInclusion.add(animation);
        else
            animationInclusion.remove(animation);

        if (included && animation.frameCount() > framesPerDim)
            setFramesPerDim(animation.frameCount());
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

    public int longestAnimFrameCount() {
        return animationInclusion.stream()
                .map(Animation::frameCount).reduce(1, Math::max);
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

        assert dir != null && anim != null;
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

    public void setAnimationOrientation(
            final Orientation animationOrientation
    ) {
        this.animationOrientation = animationOrientation;
    }

    public void setFramesPerDim(final int framesPerDim) {
        this.framesPerDim = framesPerDim;
    }

    public void setMultipleAnimsPerDim(final boolean multipleAnimsPerDim) {
        this.multipleAnimsPerDim = multipleAnimsPerDim;
    }

    public void setSingleDim(final boolean singleDim) {
        this.singleDim = singleDim;
    }

    public void setWrapAnimsAcrossDims(final boolean wrapAnimsAcrossDims) {
        this.wrapAnimsAcrossDims = wrapAnimsAcrossDims;
    }

    public Orientation getAnimationOrientation() {
        return animationOrientation;
    }

    public int getFramesPerDim() {
        return framesPerDim;
    }

    public boolean isMultipleAnimsPerDim() {
        return multipleAnimsPerDim;
    }

    public boolean isSingleDim() {
        return singleDim;
    }

    public boolean isWrapAnimsAcrossDims() {
        return wrapAnimsAcrossDims;
    }
}
