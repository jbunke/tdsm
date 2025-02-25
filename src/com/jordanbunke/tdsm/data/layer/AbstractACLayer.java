package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.sprite.SpriteSheet;
import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.tdsm.data.func.Composer;
import com.jordanbunke.tdsm.data.layer.support.AssetChoice;

public abstract class AbstractACLayer extends CustomizationLayer {
    public static final int NONE = -1;

    public final Bounds2D dims;
    public final Composer composer;

    int selection;

    private SpriteSheet sheet;

    AbstractACLayer(
            final String id, final Bounds2D dims, final Composer composer
    ) {
        super(id);

        this.dims = dims;
        this.composer = composer;
    }

    public abstract AssetChoice getChoice();
    abstract AssetChoice getChoiceAt(final int index);

    @Override
    public boolean isRendered() {
        return true;
    }

    void rebuildSpriteSheet() {
        if (hasChoice())
            sheet = new SpriteSheet(getChoice().retrieve(),
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

    public boolean hasChoice() {
        return selection != NONE;
    }
}
