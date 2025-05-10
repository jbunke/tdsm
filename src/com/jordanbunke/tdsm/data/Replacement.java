package com.jordanbunke.tdsm.data;

import java.awt.*;
import java.util.function.Function;

public record Replacement(int index, Function<Color, Color> func) {
}
