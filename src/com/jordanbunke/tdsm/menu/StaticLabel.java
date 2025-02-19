package com.jordanbunke.tdsm.menu;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.menu_elements.visual.StaticMenuElement;
import com.jordanbunke.delta_time.text.Text;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.util.Colors;
import com.jordanbunke.tdsm.util.ProgramFont;

import java.awt.*;

import static com.jordanbunke.tdsm.util.Layout.*;

public class StaticLabel extends StaticMenuElement {
    public StaticLabel(
            final Coord2D position, final Anchor anchor, final GameImage image
    ) {
        super(position, anchor, image);
    }

    public static Builder init(
            final Coord2D position, final String text
    ) {
        return new Builder(position, text);
    }

    public static class Builder {
        private final Coord2D position;
        private final String text;

        private Anchor anchor;
        private Color color;
        private double textSize;
        private ProgramFont font;
        private Text.Orientation orientation;

        Builder(
                final Coord2D position, final String text
        ) {
            this.position = position;
            this.text = text;

            anchor = Anchor.LEFT_TOP;
            color = Colors.darkSystem();
            textSize = 1.0;
            font = ProgramFont.LARGE;
            orientation = Text.Orientation.CENTER;
        }

        public StaticLabel build() {
            return new StaticLabel(position, anchor,
                    font.getBuilder(textSize, orientation, color)
                            .addText(text).build().draw());
        }

        public Builder setAnchor(final Anchor anchor) {
            this.anchor = anchor;
            return this;
        }

        public Builder setColor(final Color color) {
            this.color = color;
            return this;
        }

        public Builder setMini() {
            this.font = ProgramFont.MINI;
            return this;
        }

        public Builder setOrientation(final Text.Orientation orientation) {
            this.orientation = orientation;
            return this;
        }

        public Builder setTextSize(final double textSize) {
            this.textSize = textSize;
            return this;
        }
    }

    public Coord2D follow() {
        return getRenderPosition().displace(getWidth(), 0);
    }

    public Coord2D followTB() {
        return follow().displace(POST_LABEL_BUFFER_X, POST_LABEL_OFFSET_Y);
    }

    /**
     * Provides a position to render an element a standard amount of pixels
     * after the label.
     * */
    public Coord2D followTBStandard() {
        return followTBStandard(0);
    }

    public Coord2D followTBStandard(final int additionalX) {
        return getRenderPosition().displace(
                STANDARD_FOLLOW_X + additionalX, POST_LABEL_OFFSET_Y);
    }

    public Coord2D followIcon17() {
        return followTB().displace(-POST_LABEL_BUFFER_X, ICON_TEXTBOX_RELATIVE_DIFF_Y);
    }
}
