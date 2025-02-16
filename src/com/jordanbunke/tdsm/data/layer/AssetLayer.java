package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.sprite.SpriteSheet;
import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.tdsm.data.func.ColorReplacementFunc;
import com.jordanbunke.tdsm.data.func.Composer;
import com.jordanbunke.tdsm.data.layer.support.ColorSelection;
import com.jordanbunke.tdsm.util.Colors;
import com.jordanbunke.tdsm.util.StringUtils;

import java.awt.*;

public final class AssetLayer extends CustomizationLayer {
    private final Bounds2D dims;
    private final Composer composer;
    private final ColorReplacementFunc colorReplacementFunc;
    private final GameImage asset;
    private SpriteSheet sheet;

    public AssetLayer(
            final String id, final Bounds2D dims,
            final GameImage asset, final Composer composer,
            final ColorReplacementFunc colorReplacementFunc
    ) {
        super(id);

        this.dims = dims;
        this.asset = asset;
        this.composer = composer;
        this.colorReplacementFunc = colorReplacementFunc;

        update();
    }

    @Override
    public SpriteConstituent<String> compose() {
        return composer.build(sheet);
    }

    @Override
    public String name() {
        return StringUtils.nameFromID(id);
    }

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
        sheet = new SpriteSheet(redraw(), dims.width(), dims.height());
    }

    private GameImage redraw() {
        final Color[] colors = getInfluencingSelections().stream()
                .map(ColorSelection::getColor).toArray(Color[]::new);

        if (colors.length == 0)
            return new GameImage(asset);

        return Colors.runColorReplacement(asset, colors, colorReplacementFunc);
    }

    @Override
    public void randomize(final boolean updateSprite) {}

    @Override
    public int calculateExpandedHeight() {
        return 0;
    }
}
