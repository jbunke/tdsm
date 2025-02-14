package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.sprite.SpriteSheet;
import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.delta_time.utility.math.RNG;
import com.jordanbunke.tdsm.data.func.ComposerBuilder;
import com.jordanbunke.tdsm.data.layer.support.AssetChoice;
import com.jordanbunke.tdsm.data.layer.support.AssetChoiceTemplate;
import com.jordanbunke.tdsm.data.layer.support.NoAssetChoice;
import com.jordanbunke.tdsm.data.style.Style;

import java.util.Arrays;
import java.util.stream.IntStream;

public final class AssetChoiceLayer extends CustomizationLayer {
    public static final int NONE = -1;

    public final Bounds2D dims;
    private final String name;

    private final AssetChoice[] choices;
    private int selection;

    private final GameImage[] previews;
    private final ComposerBuilder composerBuilder;
    private final NoAssetChoice noAssetChoice;
    private final Coord2D previewCoord;
    private SpriteSheet sheet;

    public AssetChoiceLayer(
            final String id, final String name,
            final Bounds2D dims, final Style style,
            final AssetChoiceTemplate[] choices,
            final ComposerBuilder composerBuilder,
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

        this.composerBuilder = composerBuilder;
        this.noAssetChoice = noAssetChoice;

        this.selection = noAssetChoice.valid ? NONE : 0;

        update();
    }

    public void select(final int selection) {
        this.selection = selection;

        rebuildSpriteSheet();
        refreshElement();
    }

    private void rebuildSpriteSheet() {
        if (hasChoice())
            sheet = new SpriteSheet(choices[selection].retrieve(),
                    dims.width(), dims.height());
        else
            sheet = null;
    }

    @Override
    public SpriteConstituent<String> getComposer() {
        if (hasChoice())
            return composerBuilder.build(sheet);

        return s -> GameImage.dummy();
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
    public void update() {
        for (int i = 0; i < choices.length; i++) {
            choices[i].redraw();
            previews[i] = choices[i].retrieve().section(previewCoord.x,
                    previewCoord.y, dims.width(), dims.height());
        }

        rebuildSpriteSheet();
        refreshElement();
    }

    @Override
    public void randomize() {
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
    }

    private boolean hasChoice() {
        return selection != NONE;
    }

    public String getChoiceID() {
        if (!hasChoice())
            return null;

        return choices[selection].id;
    }

    public int[] getIndices() {
        return IntStream.range(noAssetChoice.valid
                ? NONE : 0, choices.length).toArray();
    }

    public GameImage getPreview(final int index) {
        return previews[index];
    }
}
