package com.jordanbunke.tdsm.io.json;

import java.util.Arrays;

import static com.jordanbunke.tdsm.io.json.JSONConstants.*;

public final class JSONObject {
    private final JSONPair[] contents;

    public JSONObject(final JSONPair... contents) {
        this.contents = contents;
    }

    @Override
    public String toString() {
        return SCOPE_OPEN + "\n" + Arrays.stream(contents)
                .map(JSONPair::write).map(s -> {
                    final String[] lines = s.split("\n");

                    if (lines.length == 1)
                        return "\t" + s;

                    return Arrays.stream(lines)
                            .map(l -> "\t" + l)
                            .reduce("", (a, b) -> a.isEmpty()
                                    ? b : a + "\n" + b);
                }).reduce("", (a, b) -> a.isEmpty()
                        ? b : a + ELEM_SEP + "\n" + b) + "\n" + SCOPE_CLOSE;
    }
}
