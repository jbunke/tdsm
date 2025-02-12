package com.jordanbunke.tdsm.io.json;

import java.util.Arrays;
import java.util.Objects;

public final class JSONArray<T> {
    private final T[] array;

    public JSONArray(final T[] array) {
        this.array = array;
    }

    @Override
    public String toString() {
        return "[\n" + Arrays.stream(array)
                .map(Objects::toString).map(s -> {
                    final String[] lines = s.split("\n");

                    if (lines.length == 1)
                        return "\t" + s;

                    return Arrays.stream(lines)
                            .map(l -> "\t" + l)
                            .reduce("", (a, b) -> a.equals("")
                                    ? b : a + "\n" + b);
                }).reduce("", (a, b) -> a.equals("")
                        ? b : a + ",\n" + b) + "\n]";
    }
}
