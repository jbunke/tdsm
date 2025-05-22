package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.sprite.constituents.SpriteConstituent;
import com.jordanbunke.delta_time.utility.math.RNG;
import com.jordanbunke.tdsm.data.Sprite;
import com.jordanbunke.tdsm.util.Layout;
import com.jordanbunke.tdsm.util.StringUtils;

public final class ChoiceLayer extends ManualRefreshLayer
        implements ChoosingLayer {
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

    // scripting inclusion
    @Override
    public boolean choose(final String choice) {
        if (choice == null) return false;

        for (int i = 0; i < choices.length; i++)
            if (choice.equals(getChoiceAt(i))) {
                chooseFromScript(i);
                return true;
            }

        return false;
    }

    // scripting inclusion
    @Override
    public void chooseFromScript(final int selection) {
        choose(selection, false);
    }

    @Override
    public void choose(final int selection) {
        choose(selection, true);
    }

    private void choose(final int selection, final boolean updateSprite) {
        if (selection == this.selection)
            return;

        this.selection = selection;
        refreshElement();

        updateDependents();

        if (updateSprite)
            Sprite.get().getStyle().update();
    }

    @Override
    public int getNumChoices() {
        return choices.length;
    }

    public int getChoiceIndex() {
        return selection;
    }

    public String getChoice() {
        return choices[selection];
    }

    public String getChoiceAt(final int index) {
        return choices[index];
    }

    // interface compliance
    @Override
    public String getChoiceID() {
        return getChoice();
    }

    // interface compliance
    @Override
    public String getChoiceIDAt(int index) {
        return getChoiceAt(index);
    }

    // interface compliance
    @Override
    public boolean hasChoice() {
        return true;
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

    @Override
    public String getID() {
        return id;
    }
}
