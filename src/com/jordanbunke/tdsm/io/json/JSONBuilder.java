package com.jordanbunke.tdsm.io.json;

import java.util.ArrayList;
import java.util.List;

import static com.jordanbunke.tdsm.io.json.JSONConstants.*;

// TODO - strip "json" package into separate project and use as dependency
public final class JSONBuilder {
    private final List<JSONPair> jsonPairs;

    public JSONBuilder() {
        this.jsonPairs = new ArrayList<>();
    }

    public void add(final JSONPair jsonPair) {
        jsonPairs.add(jsonPair);
    }

    public String write() {
        final StringBuilder sb = new StringBuilder();

        sb.append(SCOPE_OPEN);
        sb.append("\n");

        for (int i = 0; i < jsonPairs.size(); i++) {
            final JSONPair value = jsonPairs.get(i);

            final String[] lines = value.write().split("\n");

            for (int l = 0; l < lines.length; l++) {
                sb.append("\t").append(lines[l]);

                if (l + 1 < lines.length) sb.append("\n");
            }

            if (i + 1 < jsonPairs.size()) sb.append(ELEM_SEP);

            sb.append("\n");
        }

        sb.append(SCOPE_CLOSE);
        sb.append("\n");

        return sb.toString();
    }
}
