package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;
import com.jordanbunke.delta_time.utility.math.MathPlus;
import com.jordanbunke.delta_time.utility.math.RNG;
import com.jordanbunke.tdsm.data.Sprite;
import com.jordanbunke.tdsm.util.Layout;
import com.jordanbunke.tdsm.util.StringUtils;

import java.util.function.Function;

public final class MathLayer extends CustomizationLayer {
    private final String name;
    private final int min, max;
    private final Function<Integer, String> formatFunc;

    private int value;

    public MathLayer(
            final String id, final String name, final int min, final int max,
            final int defaultValue, final Function<Integer, String> formatFunc
    ) {
        super(id);
        assert max > min;

        this.name = name;
        this.min = min;
        this.max = max;
        this.formatFunc = formatFunc;

        value = MathPlus.bounded(min, defaultValue, max);
    }

    public MathLayer(
            final String id, final int min, final int max,
            final int defaultValue, final Function<Integer, String> formatFunc
    ) {
        this(id, StringUtils.nameFromID(id), min, max, defaultValue, formatFunc);
    }

    public int getValue() {
        return value;
    }

    public String getFormattedValue() {
        return formatFunc.apply(value);
    }

    public boolean isMin() {
        return value == min;
    }

    public boolean isMax() {
        return value == max;
    }

    public void increment() {
        if (value < max) {
            value++;
            Sprite.get().getStyle().update();
        }
    }

    public void decrement() {
        if (value > min) {
            value--;
            Sprite.get().getStyle().update();
        }
    }

    // scripting inclusion
    @SuppressWarnings("unused")
    public void setValue(final int value) {
        this.value = MathPlus.bounded(min, value, max);
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
        return true;
    }

    @Override
    public void update() {}

    @Override
    public void randomize(final boolean updateSprite) {
        if (isLocked())
            return;

        value = RNG.randomInRange(min, max + 1);

        if (updateSprite)
            Sprite.get().getStyle().update();
    }

    @Override
    public int calculateExpandedHeight() {
        return Layout.BASE_EXPANDED_H;
    }
}
