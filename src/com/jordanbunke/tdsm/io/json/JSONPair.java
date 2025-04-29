package com.jordanbunke.tdsm.io.json;

import static com.jordanbunke.tdsm.io.json.JSONConstants.*;

public final class JSONPair {
    private final String key;
    private final Object value;

    public JSONPair(final String key, final Object value) {
        this.key = key;
        this.value = value;
    }

    public String write() {
        return QUOTE + key + QUOTE + ": " + writeValue();
    }

    private String writeValue() {
        if (value instanceof String s)
            return QUOTE + s + QUOTE;

        return value.toString();
    }

    @Override
    public String toString() {
        return write();
    }
}
