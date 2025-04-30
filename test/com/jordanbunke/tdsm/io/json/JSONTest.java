package com.jordanbunke.tdsm.io.json;

import com.jordanbunke.tdsm.util.ParserUtils;

import java.nio.file.Path;

public class JSONTest {
    public static void main(final String[] args) {
        final String contents = ParserUtils.read(Path.of("test-json.json"));

        final JSONPair[] parsed = JSONReader.readObject(contents);
    }
}
