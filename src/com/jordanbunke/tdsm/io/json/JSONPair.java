package com.jordanbunke.tdsm.io.json;

public final class JSONPair {
    final String key;
    final Object value;

    public JSONPair(final String key, final Object value) {
        this.key = key;
        this.value = value;
    }

    public String write() {
        return "\"" + key + "\": " + writeValue();
    }

    private String writeValue() {
        if (value instanceof String s)
            return "\"" + s + "\"";

        return value.toString();
    }

    @Override
    public String toString() {
        return write();
    }
}
