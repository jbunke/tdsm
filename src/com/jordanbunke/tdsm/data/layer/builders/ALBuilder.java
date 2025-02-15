package com.jordanbunke.tdsm.data.layer.builders;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.ResourceLoader;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.tdsm.data.func.ColorReplacementFunc;
import com.jordanbunke.tdsm.data.func.ComposerBuilder;
import com.jordanbunke.tdsm.data.layer.AssetLayer;
import com.jordanbunke.tdsm.data.style.Style;
import com.jordanbunke.tdsm.util.Constants;

import java.nio.file.Path;

public class ALBuilder {
    private final String id;

    private Bounds2D dims;
    private ComposerBuilder composerBuilder;
    private ColorReplacementFunc colorReplacementFunc;
    private GameImage asset;

    public static ALBuilder init(
            final String id, final Style style
    ) {
        return new ALBuilder(id, style);
    }

    private ALBuilder(
            final String id, final Style style
    ) {
        this.id = id;

        this.dims = style.dims;
        this.composerBuilder = style::defaultBuildComposer;
        this.colorReplacementFunc = ColorReplacementFunc.trivial();

        asset = fetchAsset(id, style);
    }

    public ALBuilder setDims(final Bounds2D dims) {
        this.dims = dims;
        return this;
    }

    public ALBuilder setAsset(final GameImage asset) {
        this.asset = asset;
        return this;
    }

    public ALBuilder setComposerBuilder(
            final ComposerBuilder composerBuilder
    ) {
        this.composerBuilder = composerBuilder;
        return this;
    }

    public ALBuilder setColorReplacementFunc(
            final ColorReplacementFunc colorReplacementFunc
    ) {
        this.colorReplacementFunc = colorReplacementFunc;
        return this;
    }

    public AssetLayer build() {
        return new AssetLayer(id, dims, asset,
                composerBuilder, colorReplacementFunc);
    }

    private static GameImage fetchAsset(
            final String id, final Style style
    ) {
        final Path filepath = Constants.ASSET_ROOT_FOLDER
                .resolve(Path.of(style.id, id + ".png"));

        return ResourceLoader.loadImageResource(filepath);
    }
}
