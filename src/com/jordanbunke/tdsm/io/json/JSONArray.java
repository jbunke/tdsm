package com.jordanbunke.tdsm.io.json;

import java.util.Arrays;

public final class JSONArray<T> {
    private final T[] array;

    public JSONArray(final T[] array) {
        this.array = array;
    }

    private String writeElement(final T element) {
        if (element instanceof String s)
            return "\"" + s + "\"";

        return element.toString();
    }

    @Override
    public String toString() {
        if (array.length == 0)
            return "[]";

        return "[\n" + Arrays.stream(array)
                .map(this::writeElement).map(s -> {
                    final String[] lines = s.split("\n");

                    if (lines.length == 1)
                        return "\t" + s;

                    return Arrays.stream(lines)
                            .map(l -> "\t" + l)
                            .reduce("", (a, b) -> a.isEmpty()
                                    ? b : a + "\n" + b);
                }).reduce("", (a, b) -> a.isEmpty()
                        ? b : a + ",\n" + b) + "\n]";
    }
}
