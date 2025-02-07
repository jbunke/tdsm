package com.jordanbunke.tdsm.data.layer.builders;

import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.tdsm.data.func.ComposerBuilder;
import com.jordanbunke.tdsm.data.layer.AssetChoiceLayer;
import com.jordanbunke.tdsm.data.layer.support.AssetChoiceTemplate;
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
    private final List<AssetChoiceTemplate> choices;
    private ComposerBuilder composerBuilder;

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

        composerBuilder = style::defaultBuildComposer;
        dims = style.dims;
        name = StringUtils.nameFromID(id);
        choices = new ArrayList<>();
    }

    public AssetChoiceLayer build() {
        return new AssetChoiceLayer(id, name, dims, style,
                choices.toArray(AssetChoiceTemplate[]::new), composerBuilder);
    }

    public ACLBuilder setName(final String name) {
        this.name = name;
        return this;
    }

    public ACLBuilder setDims(final Bounds2D dims) {
        this.dims = dims;
        return this;
    }

    public ACLBuilder addChoices(final AssetChoiceTemplate... toAdd) {
        choices.addAll(Arrays.stream(toAdd).toList());
        return this;
    }

    public ACLBuilder setComposerBuilder(
            final ComposerBuilder composerBuilder
    ) {
        this.composerBuilder = composerBuilder;
        return this;
    }
}
