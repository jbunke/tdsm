package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.delta_time.utility.math.RNG;
import com.jordanbunke.tdsm.data.Sprite;
import com.jordanbunke.tdsm.data.func.Composer;
import com.jordanbunke.tdsm.data.layer.support.AssetChoice;
import com.jordanbunke.tdsm.data.layer.support.AssetChoiceTemplate;
import com.jordanbunke.tdsm.data.layer.support.NoAssetChoice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import static com.jordanbunke.tdsm.util.Layout.*;

public final class AssetChoiceLayer extends AbstractACLayer
        implements ChoosingLayer {
    private final String name;

    private final AssetChoice[] choices;

    private final GameImage[] previews;
    public final NoAssetChoice noAssetChoice;
    public final Coord2D previewCoord;

    private final List<AssetChoiceLayer> matchers;
    private final List<DependentComponentLayer> separatedComponents;

    public AssetChoiceLayer(
            final String id, final String name,
            final Bounds2D dims, final Function<String, GameImage> getter,
            final AssetChoiceTemplate[] choices,
            final Composer composer,
            final NoAssetChoice noAssetChoice, final Coord2D previewCoord
    ) {
        super(id, dims, composer);

        this.name = name;
        this.choices = Arrays.stream(choices)
                .map(a -> a.realize(getter, this))
                .toArray(AssetChoice[]::new);

        this.previews = new GameImage[this.choices.length];
        this.previewCoord = previewCoord;

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

    void addSeparatedComponent(
            final DependentComponentLayer separatedComponent
    ) {
        separatedComponents.add(separatedComponent);

        drawPreviews();
    }

    private void updateSeparatedComponents() {
        for (DependentComponentLayer separatedComponent : separatedComponents)
            separatedComponent.update();
    }

    private void match(final int selection) {
        choose(selection, false);
    }

    // scripting inclusion
    @Override
    public boolean choose(final String id) {
        if (id == null) return false;

        final String[] ids = getAssetChoiceIDs();

        for (int i = 0; i < ids.length; i++)
            if (id.equals(ids[i])) {
                chooseFromScript(i);
                return true;
            }

        return false;
    }

    // scripting inclusion
    @Override
    public void chooseFromScript(final int selection) {
        choose(selection, false);
    }

    @Override
    public void choose(final int selection) {
        choose(selection, true);
    }

    private void choose(final int selection, final boolean updateSprite) {
        select(selection);
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

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean isNonTrivial() {
        return true;
    }

    @Override
    public void update() {
        updateSeparatedComponents();

        for (int i = 0; i < choices.length; i++) {
            choices[i].redraw();
            previews[i] = drawPreview(i);
        }

        rebuildSpriteSheet();
    }

    private void drawPreviews() {
        for (int i = 0; i < choices.length; i++)
            previews[i] = drawPreview(i);
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

    @Override
    public AssetChoice getChoice() {
        return choices[selection];
    }

    @Override
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

    // scripting inclusion
    @Override
    public int getNumChoices() {
        return choices.length;
    }

    public GameImage getPreview(final int index) {
        return previews[index];
    }

    public String[] getAssetChoiceIDs() {
        return Arrays.stream(choices).map(ac -> ac.id).toArray(String[]::new);
    }

    @Override
    public String getID() {
        return id;
    }
}
