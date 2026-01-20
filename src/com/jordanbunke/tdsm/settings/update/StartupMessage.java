package com.jordanbunke.tdsm.settings.update;

import com.jordanbunke.delta_time.utility.Version;
import com.jordanbunke.json.*;
import com.jordanbunke.tdsm.util.Constants;
import com.jordanbunke.tdsm.util.ParserUtils;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

public final class StartupMessage {
    private static final Path FILEPATH =
            Constants.UPDATE_INFO_FOLDER.resolve("_values.json");
    private static final String UPDATES_CODE = "updates",
            ID_CODE = "id", SINCE_CODE = "since", INVALID = "_";

    public final String id;
    public final Version since;

    private StartupMessage(final String id, final Version since) {
        this.id = id;
        this.since = since;
    }

    public static StartupMessage[] readAll() {
        final String contents = ParserUtils.read(FILEPATH);
        final StartupMessage[] NONE = new StartupMessage[0];

        final JSONPair[] topLevel = JSONReader.readObject(contents);

        if (topLevel.length != 1)
            return NONE;

        final JSONPair updates = topLevel[0];

        if (!(updates.key().equals(UPDATES_CODE) && updates.value() instanceof JSONArray<?> arr))
            return NONE;

        return Arrays.stream(arr.get())
                .filter(element -> element instanceof JSONObject)
                .map(JSONObject.class::cast).map(JSONObject::get)
                .filter(Objects::nonNull)
                .map(pairs -> {
                    String id = INVALID;
                    Version since = new Version(1, 0, 0);

                    for (JSONPair pair : pairs) {
                        switch (pair.key()) {
                            case ID_CODE -> id = String.valueOf(pair.value());
                            case SINCE_CODE ->
                                    since = Version.parse(String.valueOf(pair.value()));
                        }
                    }

                    return new StartupMessage(id, since);
                }).toArray(StartupMessage[]::new);
    }

    @Override
    public String toString() {
        final JSONBuilder jb = new JSONBuilder();

        jb.add(new JSONPair(ID_CODE, id));
        jb.add(new JSONPair(SINCE_CODE, String.valueOf(since)));

        return jb.write();
    }
}
