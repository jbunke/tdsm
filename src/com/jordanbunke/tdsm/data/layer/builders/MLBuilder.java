package com.jordanbunke.tdsm.data.layer.builders;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.ResourceLoader;
import com.jordanbunke.delta_time.sprite.SpriteSheet;
import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;
import com.jordanbunke.tdsm.data.layer.AbstractACLayer;
import com.jordanbunke.tdsm.data.layer.CustomizationLayer;
import com.jordanbunke.tdsm.data.layer.MaskLayer;
import com.jordanbunke.tdsm.data.style.Style;
import com.jordanbunke.tdsm.util.Constants;

import java.nio.file.Path;

public final class MLBuilder {
    private final String id;
    private final CustomizationLayer[] targets;

    private SpriteConstituent<String> logic;

    public static MLBuilder init(
            final String id, final CustomizationLayer... targets
    ) {
        return new MLBuilder(id, targets);
    }

    private MLBuilder(
            final String id, final CustomizationLayer[] targets
    ) {
        this.id = id;
        this.targets = targets;

        logic = s -> GameImage.dummy();
    }

    public MaskLayer build() {
        return new MaskLayer(id, targets, logic);
    }

    public MLBuilder setLogic(final SpriteConstituent<String> logic) {
        this.logic = logic;
        return this;
    }

    public MLBuilder trySetNaiveLogic(
            final Style style, final AbstractACLayer projector
    ) {
        logic = s -> {
            final GameImage ifFail = new GameImage(
                    style.dims.width(), style.dims.height());

            try {
                if (!projector.hasChoice())
                    return ifFail;

                final String choiceID = projector.getChoice().id;
                final Path filepath = Constants.ASSET_ROOT_FOLDER
                        .resolve(Path.of(style.id, id, choiceID + ".png"));
                final GameImage source = ResourceLoader
                        .loadImageResource(filepath);
                final SpriteSheet sheet = new SpriteSheet(source,
                        projector.dims.width(), projector.dims.height());

                return projector.composer.build(sheet).getSprite(s);
            } catch (Exception e) {
                return ifFail;
            }
        };

        return this;
    }
}
