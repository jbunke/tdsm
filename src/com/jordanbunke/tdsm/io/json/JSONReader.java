package com.jordanbunke.tdsm.io.json;

import java.util.LinkedList;
import java.util.List;

import static com.jordanbunke.tdsm.io.json.JSONConstants.*;

public final class JSONReader {
    private static final int NF = -1;

    public static JSONPair[] readObject(final String contents) {
        final List<String> pairs = readSeries(SCOPE_OPEN, SCOPE_CLOSE, contents);

        return pairs == null
                ? null
                : pairs.stream().map(JSONReader::readPair).toArray(JSONPair[]::new);
    }

    private static JSONArray<Object> readArray(final String contents) {
        final List<String> elems = readSeries(ARR_OPEN, ARR_CLOSE, contents);

        return elems == null
                ? null : new JSONArray<>(elems.stream()
                .map(JSONReader::readValue).toArray(Object[]::new));
    }

    private static List<String> readSeries(
            final char open, final char close, final String contents
    ) {
        final int start = contents.indexOf(open),
                end = contents.lastIndexOf(close);

        if (start == NF || end == NF || start >= end)
            return null;

        final String elems = contents.substring(start + 1, end).trim();
        final List<String> sections = new LinkedList<>();

        if (elems.isEmpty())
            return sections;

        boolean inString = false;
        int arrayCount = 0, scopeCount = 0;

        int processed = 0, ref = 0;

        while (processed < elems.length()) {
            switch (elems.charAt(processed)) {
                case QUOTE -> inString = !inString;
                case SCOPE_CLOSE -> scopeCount--;
                case SCOPE_OPEN -> scopeCount++;
                case ARR_CLOSE -> arrayCount--;
                case ARR_OPEN -> arrayCount++;
                case ELEM_SEP -> {
                    if (arrayCount == 0 && scopeCount == 0 && !inString) {
                        sections.add(elems.substring(ref, processed).trim());
                        ref = processed + 1;
                    }
                }
            }

            processed++;
        }

        sections.add(elems.substring(ref).trim());

        return sections;
    }

    private static JSONPair readPair(final String content) {
        // TODO - more thorough parsing
        final int sepIndex = content.indexOf(K_V_SEP);

        if (sepIndex == NF)
            return null;

        final String key = readStringLit(content.substring(0, sepIndex).trim());
        final Object value = readValue(content.substring(sepIndex + 1).trim());

        return new JSONPair(key, value);
    }

    private static Object readValue(final String contents) {
        return switch (contents) {
            case "null" -> null;
            case "true" -> true;
            case "false" -> false;
            default -> switch (contents.charAt(0)) {
                case SCOPE_OPEN -> readObject(contents);
                case ARR_OPEN -> readArray(contents);
                case QUOTE -> readStringLit(contents);
                default -> {
                    if (isInteger(contents))
                        yield Integer.parseInt(contents);
                    else if (isDouble(contents))
                        yield Double.parseDouble(contents);

                    // TODO - other cases
                    yield null;
                }
            };
        };
    }

    private static String readStringLit(final String enclosed) {
        return enclosed.substring(1, enclosed.length() - 1);
    }

    private static boolean isInteger(final String string) {
        if (string == null || string.isEmpty())
            return false;

        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private static boolean isDouble(final String string) {
        if (string == null || string.isEmpty())
            return false;

        try {
            Double.parseDouble(string);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
