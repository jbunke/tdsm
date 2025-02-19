package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;
import com.jordanbunke.delta_time.utility.math.RNG;
import com.jordanbunke.tdsm.data.Sprite;
import com.jordanbunke.tdsm.util.Layout;
import com.jordanbunke.tdsm.util.StringUtils;

public final class ChoiceLayer extends ManualRefreshLayer {
    private final String name;
    private final String[] choices;

    private int selection;

    public ChoiceLayer(
            final String id, final String name, final int initialSelection,
            final String... choices
    ) {
        super(id);

        this.name = name;
        this.choices = choices;

        selection = initialSelection;
    }

    public ChoiceLayer(final String id, final String... choices) {
        this(id, StringUtils.nameFromID(id), 0, choices);
    }

    public void choose(final int selection) {
        if (selection == this.selection)
            return;

        this.selection = selection;
        refreshElement();

        updateDependents();

        Sprite.get().getStyle().update();
    }

    public int getNumChoices() {
        return choices.length;
    }

    public int getSelection() {
        return selection;
    }

    public String getChoice() {
        return choices[selection];
    }

    public String getChoiceAt(final int index) {
        return choices[index];
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

        this.selection = RNG.randomInRange(0, choices.length);
        refreshElement();

        updateDependents();

        if (updateSprite)
            Sprite.get().getStyle().update();
    }

    @Override
    public int calculateExpandedHeight() {
        return Layout.BASE_EXPANDED_H;
    }
}
