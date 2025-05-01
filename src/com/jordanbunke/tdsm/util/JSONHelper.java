package com.jordanbunke.tdsm.util;

import com.jordanbunke.json.*;
import com.jordanbunke.stip_parser.ParserSerializer;
import com.jordanbunke.tdsm.data.layer.*;
import com.jordanbunke.tdsm.data.layer.support.AssetChoice;
import com.jordanbunke.tdsm.data.layer.support.ColorSelection;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public final class JSONHelper {
    public static final String STYLE_ID = "style_id", CHOICE = "choice",
            COLORS = "colors", NONE = "%none%",
            CUSTOMIZATION = "customization", FRAMES = "frames",
            CONFIG = "config", DIRECTIONS = "directions",
            ANIMATIONS = "animations";

    public static JSONArray<String> buildColorSelectionArray(
            final ColorSelection[] selections
    ) {
        final String[] colors = Arrays.stream(selections)
                .map(ColorSelection::getColor)
                .map(c -> "#" + ParserSerializer.serializeColor(c, true))
                .toArray(String[]::new);

        return new JSONArray<>(colors);
    }

    public static Object getLayerJSONValue(final CustomizationLayer layer) {
        final Object value;

        if (layer instanceof MathLayer ml)
            value = ml.getValue();
        else if (layer instanceof AssetChoiceLayer acl) {
            if (acl.hasChoice()) {
                final AssetChoice ac = acl.getChoice();

                value = new JSONObject(new JSONPair(CHOICE, ac.id),
                        new JSONPair(COLORS, buildColorSelectionArray(
                                ac.getColorSelections())));
            } else
                value = NONE;
        }
        else if (layer instanceof ChoiceLayer cl)
            value = cl.getChoice();
        else if (layer instanceof ColorSelectionLayer csl)
            value = buildColorSelectionArray(csl.getSelections());
        else if (layer instanceof DecisionLayer dl)
            value = getLayerJSONValue(dl.getDecision());
        else if (layer instanceof GroupLayer gl) {
            final List<JSONPair> members = new LinkedList<>();

            gl.all().filter(CustomizationLayer::isNonTrivial)
                    .forEach(l -> members.add(
                            new JSONPair(l.id, getLayerJSONValue(l))));

            value = new JSONObject(members.toArray(JSONPair[]::new));
        }
        else
            value = null; // TODO - constant

        return value;
    }
}
