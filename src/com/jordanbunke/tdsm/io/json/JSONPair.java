package com.jordanbunke.tdsm.io.json;

import static com.jordanbunke.tdsm.io.json.JSONConstants.*;

public record JSONPair(String key, Object value) {

    public String write() {
        return QUOTE + key + QUOTE + K_V_SEP + " " + writeValue();
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
