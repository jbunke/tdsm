package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;

// TODO
public final class AssetLayer extends CustomizationLayer {
    AssetLayer(String id) {
        super(id);
    }

    @Override
    public SpriteConstituent<String> getComposer() {
        return null;
    }

    @Override
    public String name() {
        return null;
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

    }

    @Override
    public void randomize() {

    }

    @Override
    public int calculateExpandedHeight() {
        return 0;
    }
}
