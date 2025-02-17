package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;
import com.jordanbunke.tdsm.util.Layout;

// TODO
public final class ChoiceLayer extends CustomizationLayer {
    ChoiceLayer(String id) {
        super(id);
    }

    @Override
    public SpriteConstituent<String> compose() {
        return s -> GameImage.dummy();
    }

    @Override
    public String name() {
        // TODO
        return null;
    }

    @Override
    public boolean isRendered() {
        return false;
    }

    @Override
    public boolean isNonTrivial() {
        return true;
    }

    @Override
    public void update() {
        // TODO
    }

    @Override
    public void randomize(final boolean updateSprite) {
        // TODO
    }

    @Override
    public int calculateExpandedHeight() {
        return Layout.BASE_EXPANDED_H;
    }
}
