package com.jordanbunke.tdsm.menu;

import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.tdsm.util.Constants;
import com.jordanbunke.tdsm.util.Layout;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class DynamicTextbox extends Textbox {
    private final Supplier<String> getter;

    public DynamicTextbox(
            final Coord2D position, final int width, final Anchor anchor,
            final String prefix, final String suffix,
            final Predicate<String> textValidator,
            final Supplier<String> getter, final Consumer<String> setter,
            final int maxLength
    ) {
        super(position, width, anchor, prefix, getter.get(),
                suffix, textValidator, setter, maxLength);

        this.getter = getter;
    }

    public static Builder init(
            final Coord2D position, final Supplier<String> getter,
            final Consumer<String> setter
    ) {
        return new Builder(position, getter, setter);
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);

        if (!isTyping())
            setText(getter.get());
    }

    public static class Builder {
        private final Coord2D position;
        private final Supplier<String> getter;
        private final Consumer<String> setter;
        private int width, maxLength;
        private Anchor anchor;
        private String prefix, suffix;
        private Predicate<String> textValidator;

        Builder(
                final Coord2D position, final Supplier<String> getter,
                final Consumer<String> setter
        ) {
            this.position = position;
            this.getter = getter;
            this.setter = setter;

            width = Layout.STANDARD_TEXTBOX_W;
            maxLength = Constants.DEF_TEXTBOX_CHAR_MAX;

            anchor = Anchor.LEFT_TOP;
            prefix = "";
            suffix = "";

            textValidator = s -> true;
        }

        public Builder setWidth(final int width) {
            this.width = Math.max(1, width);
            return this;
        }

        public Builder setMaxLength(final int maxLength) {
            this.maxLength = Math.max(1, maxLength);
            return this;
        }

        public Builder setAnchor(final Anchor anchor) {
            this.anchor = anchor;
            return this;
        }

        public Builder setPrefix(final String prefix) {
            this.prefix = prefix;
            return this;
        }

        public Builder setSuffix(final String suffix) {
            this.suffix = suffix;
            return this;
        }

        public Builder setTextValidator(
                final Predicate<String> textValidator
        ) {
            this.textValidator = textValidator;
            return this;
        }

        public DynamicTextbox build() {
            return new DynamicTextbox(position, width, anchor,
                    prefix, suffix, textValidator, getter, setter, maxLength);
        }
    }
}
