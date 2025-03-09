package com.jordanbunke.tdsm.menu.pre_export;

import java.util.function.Consumer;
import java.util.function.Supplier;

public record StyleOption(
        String description,
        Supplier<Boolean> checker,
        Consumer<Boolean> setter,
        String infoCode) {
}
