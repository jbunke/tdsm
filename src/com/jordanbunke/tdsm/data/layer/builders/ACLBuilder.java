package com.jordanbunke.tdsm.data.layer.builders;

import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.data.func.Composer;
import com.jordanbunke.tdsm.data.layer.AssetChoiceLayer;
import com.jordanbunke.tdsm.data.layer.support.AssetChoiceTemplate;
import com.jordanbunke.tdsm.data.layer.support.NoAssetChoice;
import com.jordanbunke.tdsm.data.style.Style;
import com.jordanbunke.tdsm.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ACLBuilder {
    private final String id;
    private final Style style;

    private String name;
    private Bounds2D dims;
    private Coord2D previewCoord;
    private final List<AssetChoiceTemplate> choices;
    private Composer composer;
    private NoAssetChoice noAssetChoice;

    public static ACLBuilder init(
            final String id, final Style style
    ) {
        return new ACLBuilder(id, style);
    }

    public static ACLBuilder of(
            final String id, final Style style,
            final AssetChoiceTemplate... initialChoices
    ) {
        return init(id, style).addChoices(initialChoices);
    }

    private ACLBuilder(
            final String id, final Style style
    ) {
        this.id = id;
        this.style = style;

        composer = style::defaultBuildComposer;
        dims = style.dims;
        previewCoord = new Coord2D();
        name = StringUtils.nameFromID(id);
        choices = new ArrayList<>();
        noAssetChoice = NoAssetChoice.invalid();
    }

    public AssetChoiceLayer build() {
        return new AssetChoiceLayer(id, name, dims, style,
                choices.toArray(AssetChoiceTemplate[]::new),
                composer, noAssetChoice, previewCoord);
    }

    public ACLBuilder setName(final String name) {
        this.name = name;
        return this;
    }

    public ACLBuilder setDims(final Bounds2D dims) {
        this.dims = dims;
        return this;
    }

    public ACLBuilder setPreviewCoord(final Coord2D previewCoord) {
        this.previewCoord = previewCoord;
        return this;
    }

    public ACLBuilder addChoices(final AssetChoiceTemplate... toAdd) {
        choices.addAll(Arrays.stream(toAdd).toList());
        return this;
    }

    public ACLBuilder setComposer(
            final Composer composer
    ) {
        this.composer = composer;
        return this;
    }

    public ACLBuilder setNoAssetChoice(final NoAssetChoice noAssetChoice) {
        this.noAssetChoice = noAssetChoice;
        return this;
    }
}
