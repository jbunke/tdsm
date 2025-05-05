package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.tdsm.data.layer.support.AssetChoice;
import com.jordanbunke.tdsm.data.layer.support.AssetChoiceTemplate;
import com.jordanbunke.tdsm.data.layer.support.ColorSelection;
import com.jordanbunke.tdsm.util.StringUtils;

import java.util.function.Function;
import java.util.stream.IntStream;

public class DependentComponentLayer extends AbstractACLayer {
    public final int relativeIndex;

    private final AssetChoiceLayer ref;

    private final AssetChoice[] choices;

    public DependentComponentLayer(
            final String id, final Function<String, GameImage> getter,
            final AssetChoiceLayer ref, final int relativeIndex
    ) {
        super(id, ref.dims, ref.composer);

        this.relativeIndex = relativeIndex;
        this.ref = ref;

        final String[] choiceIDs = ref.getAssetChoiceIDs();
        final int choiceCount = choiceIDs.length;

        choices = IntStream.range(0, choiceCount).mapToObj(ref::getChoiceAt)
                .map(ac -> new AssetChoiceTemplate(ac.id,
                        ac.colorReplacementFunc, ac.getColorSelections()))
                .map(act -> act.realize(getter, this))
                .toArray(AssetChoice[]::new);

        addInfluencingSelections(ref.getInfluencingSelections()
                .toArray(ColorSelection[]::new));

        update();
        ref.addSeparatedComponent(this);
    }

    public boolean isLower() {
        return relativeIndex < 0;
    }

    public boolean isHigher() {
        return relativeIndex > 0;
    }

    @Override
    public String name() {
        return StringUtils.nameFromID(id);
    }

    @Override
    public boolean isNonTrivial() {
        return false;
    }

    @Override
    public void update() {
        selection = ref.selection;

        for (AssetChoice choice : choices)
            choice.redraw();

        rebuildSpriteSheet();
    }

    @Override
    public void randomize(final boolean updateSprite) {}

    @Override
    public int calculateExpandedHeight() {
        return 0;
    }

    @Override
    AssetChoice getChoiceAt(final int index) {
        return choices[index];
    }

    @Override
    public AssetChoice getChoice() {
        return choices[selection];
    }
}
