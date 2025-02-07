package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.sprite.SpriteSheet;
import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.RNG;
import com.jordanbunke.tdsm.data.style.Style;
import com.jordanbunke.tdsm.util.StringUtils;

import java.util.Arrays;
import java.util.function.Function;

public final class AssetChoiceLayer extends CustomizationLayer {
    private final Bounds2D dims;
    private final String name;

    private final AssetChoice[] choices;
    private int selection;

    private final GameImage[] previews;
    private final Function<SpriteSheet, SpriteConstituent<String>> composerBuilder;
    private SpriteSheet sheet;

    public AssetChoiceLayer(
            final String id, final Style style,
            final AssetChoiceTemplate[] choices
    ) {
        this(id, style, choices, style::defaultBuildComposer);
    }

    public AssetChoiceLayer(
            final String id, final Style style,
            final AssetChoiceTemplate[] choices,
            final Function<SpriteSheet, SpriteConstituent<String>> composerBuilder
    ) {
        this(id, style.dims, style, choices, composerBuilder);
    }

    public AssetChoiceLayer(
            final String id, final Bounds2D dims,
            final Style style, final AssetChoiceTemplate[] choices,
            final Function<SpriteSheet, SpriteConstituent<String>> composerBuilder
    ) {
        this(id, StringUtils.nameFromID(id), dims,
                style, choices, composerBuilder);
    }

    public AssetChoiceLayer(
            final String id, final String name,
            final Bounds2D dims, final Style style,
            final AssetChoiceTemplate[] choices,
            final Function<SpriteSheet, SpriteConstituent<String>> composerBuilder
    ) {
        super(id, true);

        this.name = name;
        this.dims = dims;
        this.choices = Arrays.stream(choices)
                .map(a -> a.realize(style, this))
                .toArray(AssetChoice[]::new);
        this.selection = 0;

        this.previews = new GameImage[this.choices.length];
        this.composerBuilder = composerBuilder;

        update();
    }

    public void select(final int selection) {
        this.selection = selection;
        rebuildSpriteSheet();
    }

    private void rebuildSpriteSheet() {
        sheet = new SpriteSheet(choices[selection].retrieve(),
                dims.width(), dims.height());
    }

    @Override
    public SpriteConstituent<String> getComposer() {
        return composerBuilder.apply(sheet);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void update() {
        for (int i = 0; i < choices.length; i++) {
            choices[i].redraw();
            previews[i] = choices[i].retrieve().section(0, 0, dims.width(), dims.height());
        }

        sheet = new SpriteSheet(choices[selection].retrieve(),
                dims.width(), dims.height());
    }

    @Override
    public void randomize() {
        final int index = RNG.randomInRange(0, choices.length);
        select(index);

        choices[selection].randomize();

        update();
    }
}
