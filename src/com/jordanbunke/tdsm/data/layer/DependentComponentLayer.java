package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.sprite.SpriteSheet;
import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.tdsm.data.func.Composer;
import com.jordanbunke.tdsm.data.layer.support.AssetChoice;
import com.jordanbunke.tdsm.data.layer.support.AssetChoiceTemplate;
import com.jordanbunke.tdsm.data.style.Style;

import java.util.stream.IntStream;

// TODO - influencing selections?
public class DependentComponentLayer extends CustomizationLayer {
    public final int relativeIndex;

    public final Bounds2D dims;
    public final Composer composer;

    private final AssetChoiceLayer ref;

    private final AssetChoice[] choices;
    private int selection;

    private SpriteSheet sheet;

    public DependentComponentLayer(
            final String id, final Style style,
            final AssetChoiceLayer ref, final int relativeIndex
    ) {
        super(id);

        this.relativeIndex = relativeIndex;
        this.ref = ref;

        dims = ref.dims;
        composer = ref.composer;

        final String[] choiceIDs = ref.getAssetChoiceIDs();
        final int choiceCount = choiceIDs.length;

        choices = IntStream.range(0, choiceCount).mapToObj(ref::getChoiceAt)
                .map(ac -> new AssetChoiceTemplate(ac.id,
                        ac.colorReplacementFunc, ac.getColorSelections()))
                .map(act -> act.realize(style, this))
                .toArray(AssetChoice[]::new);

        selection = AssetChoiceLayer.NONE;

        update();
    }

    public boolean isLower() {
        return relativeIndex < 0;
    }

    public boolean isHigher() {
        return relativeIndex > 0;
    }

    // TODO - to super
    private void rebuildSpriteSheet() {
        if (hasChoice())
            sheet = new SpriteSheet(choices[selection].retrieve(),
                    dims.width(), dims.height());
        else
            sheet = null;
    }

    // TODO - to super
    @Override
    public SpriteConstituent<String> compose() {
        if (hasChoice())
            return composer.build(sheet);

        return s -> new GameImage(dims.width(), dims.height());
    }

    // TODO - to super
    public boolean hasChoice() {
        return selection != AssetChoiceLayer.NONE;
    }

    @Override
    public String name() {
        return id;
    }

    // TODO - to super
    @Override
    public boolean isRendered() {
        return true;
    }

    @Override
    public boolean isNonTrivial() {
        return false;
    }

    @Override
    public void update() {
        selection = ref.getChoiceIndex();

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

    // TODO - to super
    AssetChoice getChoiceAt(final int index) {
        return choices[index];
    }
}
