package com.jordanbunke.tdsm.data;

import com.jordanbunke.tdsm.util.EnumUtils;
import com.jordanbunke.tdsm.util.StringUtils;

public enum Orientation {
    HORIZONTAL, VERTICAL;

    public String animationDim() {
        return this == HORIZONTAL ? "row" : "column";
    }

    public String complementaryAdverb() {
        return EnumUtils.next(this).name().toLowerCase() + "ly";
    }

    public String format() {
        return StringUtils.nameFromID(name().toLowerCase());
    }
}
