package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.sprite.SpriteSheet;
import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;
import com.jordanbunke.tdsm.util.StringUtils;

import java.util.function.Function;

public final class MaskLayer extends CustomizationLayer {
    private final String name;
    private final CustomizationLayer[] targets;
    private final SpriteConstituent<String> logic;

    public MaskLayer(
            final String id, final CustomizationLayer[] targets,
            final SpriteConstituent<String> logic
    ) {
        super(id);

        name = StringUtils.nameFromID(id);

        this.targets = targets;
        this.logic = logic;
    }

    // scripting inclusion
    @SuppressWarnings("unused")
    public static SpriteConstituent<String> naiveLogic(
            final AbstractACLayer projector, final Function<String, GameImage> getter
    ) {
        return s -> {
            final GameImage ifFail = new GameImage(
                    projector.dims.width(), projector.dims.height());

            try {
                if (!projector.hasChoice())
                    return ifFail;

                final GameImage source = getter.apply(projector.getChoice().id);
                final SpriteSheet sheet = new SpriteSheet(source,
                        projector.dims.width(), projector.dims.height());

                return projector.composer.build(sheet).getSprite(s);
            } catch (Exception e) {
                return ifFail;
            }
        };
    }

    public CustomizationLayer[] getTargets() {
        return targets;
    }

    @Override
    public SpriteConstituent<String> compose() {
        return logic;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean isRendered() {
        return false;
    }

    @Override
    public boolean isNonTrivial() {
        return false;
    }

    @Override
    public void update() {}

    @Override
    public void randomize(final boolean updateSprite) {}

    @Override
    public int calculateExpandedHeight() {
        return 0;
    }
}
