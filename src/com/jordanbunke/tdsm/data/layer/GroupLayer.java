package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;
import com.jordanbunke.tdsm.data.Sprite;
import com.jordanbunke.tdsm.util.StringUtils;

import java.util.Arrays;
import java.util.stream.Stream;

// TODO
//  Maybe this should extend ManualRefreshLayer
public final class GroupLayer extends CustomizationLayer {
    private final String name;
    private final CustomizationLayer[] members;

    public GroupLayer(final String id) {
        this(id, StringUtils.nameFromID(id));
    }

    public GroupLayer(
            final String id, final String name,
            final CustomizationLayer... members
    ) {
        super(id);

        this.name = name;
        this.members = members;
    }

    @Override
    public SpriteConstituent<String> compose() {
        return s -> GameImage.dummy();
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
        return all().map(CustomizationLayer::isNonTrivial)
                .reduce(false, Boolean::logicalOr);
    }

    @Override
    public void update() {
        // TODO
        all().forEach(CustomizationLayer::update);
    }

    @Override
    public void randomize(final boolean updateSprite) {
        if (isLocked())
            return;

        all().forEach(l -> l.randomize(false));

        if (updateSprite)
            Sprite.get().getStyle().update();
    }

    @Override
    public int calculateExpandedHeight() {
        // TODO
        return 200;
    }

    public Stream<CustomizationLayer> all() {
        return Arrays.stream(members);
    }

    public CustomizationLayer[] getMembers() {
        return members;
    }
}
