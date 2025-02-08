package com.jordanbunke.tdsm.menu;

import com.jordanbunke.delta_time.image.GameImage;

public interface Button {
    boolean isSelected();
    boolean isHighlighted();

    default GameImage getAsset(final boolean sOverH) {
        return outcomes(getSelectedAsset(), getHighlightedAsset(),
                getBaseAsset(), sOverH);
    }

    default GameImage getAsset() {
        return getAsset(true);
    }

    GameImage getBaseAsset();
    GameImage getHighlightedAsset();
    GameImage getSelectedAsset();

    default <T> T outcomes(
            final T selected, final T highlighted,
            final T elseOutcome, final boolean sOverH
    ) {
        final boolean priority = sOverH ? isSelected() : isHighlighted(),
                secondary = sOverH ? isHighlighted() : isSelected();
        final T priorityO = sOverH ? selected : highlighted,
                secondaryO = sOverH ? highlighted : selected;

        return priority ? priorityO : (secondary ? secondaryO : elseOutcome);
    }

    default <T> T outcomes(
            final T selected, final T highlighted, final T elseOutcome
    ) {
        return outcomes(selected, highlighted, elseOutcome, true);
    }

    static Button sim(
            final boolean selected, final boolean highlighted
    ) {
        return new Button() {
            @Override
            public boolean isSelected() {
                return selected;
            }

            @Override
            public boolean isHighlighted() {
                return highlighted;
            }

            @Override
            public GameImage getBaseAsset() {
                return GameImage.dummy();
            }

            @Override
            public GameImage getHighlightedAsset() {
                return GameImage.dummy();
            }

            @Override
            public GameImage getSelectedAsset() {
                return GameImage.dummy();
            }
        };
    }
}
