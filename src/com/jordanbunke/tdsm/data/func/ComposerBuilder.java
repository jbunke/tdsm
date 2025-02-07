package com.jordanbunke.tdsm.data.func;

import com.jordanbunke.delta_time.sprite.SpriteSheet;
import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;

@FunctionalInterface
public interface ComposerBuilder {
    SpriteConstituent<String> build(final SpriteSheet sheet);
}
