package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.sprite.SpriteSheet;
import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.delta_time.utility.math.RNG;
import com.jordanbunke.tdsm.data.Sprite;
import com.jordanbunke.tdsm.data.func.Composer;
import com.jordanbunke.tdsm.data.layer.support.AssetChoice;
import com.jordanbunke.tdsm.data.layer.support.AssetChoiceTemplate;
import com.jordanbunke.tdsm.data.layer.support.NoAssetChoice;
import com.jordanbunke.tdsm.data.style.Style;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static com.jordanbunke.tdsm.util.Layout.*;

public final class AssetChoiceLayer extends CustomizationLayer {
    public static final int NONE = -1;

    public final Bounds2D dims;
    public final Composer composer;
    private final String name;

    private final AssetChoice[] choices;
    private int selection;

    private final GameImage[] previews;
    public final NoAssetChoice noAssetChoice;
    public final Coord2D previewCoord;
    private SpriteSheet sheet;

    private final List<AssetChoiceLayer> matchers;
    private final List<DependentComponentLayer> separatedComponents;

    public AssetChoiceLayer(
            final String id, final String name,
            final Bounds2D dims, final Style style,
            final AssetChoiceTemplate[] choices,
            final Composer composer,
            final NoAssetChoice noAssetChoice, final Coord2D previewCoord
    ) {
        super(id);

        this.name = name;
        this.dims = dims;
        this.choices = Arrays.stream(choices)
                .map(a -> a.realize(style, this))
                .toArray(AssetChoice[]::new);

        this.previews = new GameImage[this.choices.length];
        this.previewCoord = previewCoord;

        this.composer = composer;
        this.noAssetChoice = noAssetChoice;

        selection = noAssetChoice.valid ? NONE : 0;

        matchers = new ArrayList<>();
        separatedComponents = new ArrayList<>();

        update();
    }

    public static void parallelMatchers(
            final AssetChoiceLayer a, final AssetChoiceLayer b
    ) {
        a.addMatcher(b);
        b.addMatcher(a);
    }

    public void addMatcher(final AssetChoiceLayer matcher) {
        matchers.add(matcher);
    }

    public void attemptToMatchChoice(final AssetChoiceLayer ref) {
        if (!ref.hasChoice()) {
            if (hasChoice() && noAssetChoice.valid)
                match(NONE);

            return;
        }

        final AssetChoice toMatch = ref.getChoice();

        if (hasChoice() && getChoice().id.equals(toMatch.id))
            return;

        for (int i = 0; i < choices.length; i++)
            if (toMatch.id.equals(choices[i].id)) {
                match(i);
                return;
            }
    }

    private void updateMatchers() {
        for (AssetChoiceLayer matcher : matchers)
            matcher.attemptToMatchChoice(this);
    }

    public void addSeparatedComponent(
            final DependentComponentLayer separatedComponent
    ) {
        separatedComponents.add(separatedComponent);
    }

    private void updateSeparatedComponents() {
        for (DependentComponentLayer separatedComponent : separatedComponents)
            separatedComponent.update();
    }

    private void match(final int selection) {
        choose(selection, false);
    }

    public void choose(final int selection) {
        choose(selection, true);
    }

    private void choose(final int selection, final boolean updateSprite) {
        select(selection);
        updateSeparatedComponents();
        update();

        updateMatchers();
        updateDependents();

        if (updateSprite)
            Sprite.get().getStyle().update();
    }

    private void select(final int selection) {
        if (selection == this.selection)
            return;

        this.selection = selection;

        // rebuildSpriteSheet();
    }

    private void rebuildSpriteSheet() {
        if (hasChoice())
            sheet = new SpriteSheet(choices[selection].retrieve(),
                    dims.width(), dims.height());
        else
            sheet = null;
    }

    @Override
    public SpriteConstituent<String> compose() {
        if (hasChoice())
            return composer.build(sheet);

        return s -> new GameImage(dims.width(), dims.height());
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean isRendered() {
        return true;
    }

    @Override
    public boolean isNonTrivial() {
        return true;
    }

    @Override
    public void update() {
        for (int i = 0; i < choices.length; i++) {
            choices[i].redraw();
            previews[i] = drawPreview(i);
        }

        rebuildSpriteSheet();
    }

    private GameImage drawPreview(final int index) {
        final GameImage preview = new GameImage(dims.width(), dims.height());

        // Lower
        final DependentComponentLayer[] lower = separatedComponents.stream()
                .filter(DependentComponentLayer::isLower)
                .sorted(Comparator.comparingInt(dcl -> dcl.relativeIndex))
                .toArray(DependentComponentLayer[]::new);

        for (DependentComponentLayer dcl : lower)
            preview.draw(dcl.getChoiceAt(index).retrieve(),
                    -previewCoord.x, -previewCoord.y);

        // This layer
        preview.draw(choices[index].retrieve(), -previewCoord.x, -previewCoord.y);

        // Higher
        final DependentComponentLayer[] higher = separatedComponents.stream()
                .filter(DependentComponentLayer::isHigher)
                .sorted(Comparator.comparingInt(dcl -> dcl.relativeIndex))
                .toArray(DependentComponentLayer[]::new);

        for (DependentComponentLayer dcl : higher)
            preview.draw(dcl.getChoiceAt(index).retrieve(),
                    -previewCoord.x, -previewCoord.y);

        return preview.submit();
    }

    @Override
    public void randomize(final boolean updateSprite) {
        if (isLocked())
            return;

        final int index;

        if (noAssetChoice.valid) {
            if (noAssetChoice.equalRandomOdds)
                index = RNG.randomInRange(NONE, choices.length);
            else {
                final boolean noChoice = RNG.prob(noAssetChoice.randomProb);

                index = noChoice ? NONE : RNG.randomInRange(0, choices.length);
            }
        } else
            index = RNG.randomInRange(0, choices.length);

        select(index);

        if (hasChoice())
            choices[selection].randomize();

        updateSeparatedComponents();
        update();

        updateMatchers();
        updateDependents();

        if (updateSprite)
            Sprite.get().getStyle().update();
    }

    public int maxSelectors() {
        return Arrays.stream(choices)
                .map(c -> c.getColorSelections().length)
                .reduce(0, Math::max);
    }

    @Override
    public int calculateExpandedHeight() {
        final int maxSelectors = maxSelectors();
        final boolean hasSelectors = maxSelectors > 0;

        return ASSETS_BASE_H + dims.height() + (hasSelectors ?
                POST_ASSETS_COL_SEL_BASE_H +
                        (maxSelectors > MAX_SELECTORS_WO_SCROLL
                                ? HORZ_SCROLL_BAR_H + 4 : 0) : 0);
    }

    public boolean hasChoice() {
        return selection != NONE;
    }

    public AssetChoice getChoice() {
        return choices[selection];
    }

    public AssetChoice getChoiceAt(final int index) {
        return choices[index];
    }

    public int getChoiceIndex() {
        return selection;
    }

    public int[] getIndices() {
        return IntStream.range(noAssetChoice.valid
                ? NONE : 0, choices.length).toArray();
    }

    public GameImage getPreview(final int index) {
        return previews[index];
    }

    public String[] getAssetChoiceIDs() {
        return Arrays.stream(choices).map(ac -> ac.id).toArray(String[]::new);
    }
}
