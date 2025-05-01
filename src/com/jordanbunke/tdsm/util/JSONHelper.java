package com.jordanbunke.tdsm.util;

import com.jordanbunke.json.*;
import com.jordanbunke.stip_parser.ParserSerializer;
import com.jordanbunke.tdsm.data.Animation;
import com.jordanbunke.tdsm.data.Directions.Dir;
import com.jordanbunke.tdsm.data.Edge;
import com.jordanbunke.tdsm.data.Orientation;
import com.jordanbunke.tdsm.data.Sprite;
import com.jordanbunke.tdsm.data.layer.*;
import com.jordanbunke.tdsm.data.layer.support.AssetChoice;
import com.jordanbunke.tdsm.data.layer.support.ColorSelection;
import com.jordanbunke.tdsm.data.style.Style;
import com.jordanbunke.tdsm.data.style.Styles;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;

public final class JSONHelper {
    public static final String STYLE_ID = "style_id", CHOICE = "choice",
            COLORS = "colors", NONE = "%none%",
            CUSTOMIZATION = "customization", FRAMES = "frames",
            CONFIG = "config", DIRECTIONS = "directions",
            ANIMATIONS = "animations", PADDING = "padding", LAYOUT = "layout",
            ORIENTATION = "orientation", WRAP_ACROSS_DIMS = "wrap_across_dims",
            MULTIPLE_ANIMS_PER_DIM = "multiple_anims_per_dim",
            SINGLE_DIM = "single_dim", FRAMES_PER_DIM = "frames_per_dim";

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
            value = null;

        return value;
    }

    // TODO - GUI button
    @SuppressWarnings("unused")
    public static List<String> loadFromJSON(
            final JSONPair[] pairs, final boolean gui
    ) {
        Style style = null;

        final List<String> errorList = new LinkedList<>();

        if (pairs == null) {
            errorList.add("Argument could not be parsed as JSON");
            return errorList;
        }

        for (JSONPair pair : pairs) {
            switch (pair.key()) {
                case STYLE_ID -> {
                    final String value = String.valueOf(pair.value());
                    style = EnumUtils.stream(Styles.class).map(Styles::get)
                            .filter(s -> s.id.equals(value))
                            .findFirst().orElse(null);

                    if (gui && style != null)
                        Sprite.get().setStyle(style);
                }
                case CUSTOMIZATION -> {
                    if (style != null && pair.value() instanceof JSONObject o) {
                        final JSONPair[] layerPairs = o.get();
                        final List<CustomizationLayer> layers =
                                style.layers.customization();

                        setLayersFromJSON(layerPairs,
                                layerIDMap(layers.toArray(
                                        CustomizationLayer[]::new)),
                                errorList);

                        style.update();
                    }
                }
                case CONFIG -> {
                    if (style != null && pair.value() instanceof JSONObject o)
                        setConfigFromJSON(style, o.get());
                }
            }
        }

        if (style == null)
            errorList.add("Could not identify a sprite style from the JSON content");

        return errorList;
    }

    private static void setConfigFromJSON(
            final Style style, final JSONPair[] pairs
    ) {
        for (JSONPair pair : pairs) {
            switch (pair.key()) {
                case DIRECTIONS -> {
                    if (pair.value() instanceof JSONArray<?> arr) {
                        final Dir[] dirs = Arrays.stream(arr.get())
                                .map(String::valueOf).map(String::toUpperCase)
                                .filter(s -> EnumUtils.matches(s, Dir.class))
                                .map(Dir::valueOf).toArray(Dir[]::new);
                        style.setExportDirections(dirs);
                    }
                }
                case ANIMATIONS -> {
                    if (pair.value() instanceof JSONObject o) {
                        final Map<String, Animation> animIDMap =
                                idMap(a -> a.id, style.animations);
                        final Animation[] anims = Arrays.stream(o.get())
                                .map(JSONPair::key)
                                .filter(animIDMap::containsKey)
                                .map(animIDMap::get)
                                .toArray(Animation[]::new);
                        style.setExportAnimations(anims);
                    }
                }
                case PADDING -> {
                    if (pair.value() instanceof JSONObject o)
                        for (JSONPair edge : o.get())
                            style.setEdgePadding(
                                    Edge.valueOf(edge.key().toUpperCase()),
                                    (Integer) edge.value());
                }
                case LAYOUT -> {
                    if (pair.value() instanceof JSONObject o)
                        setLayoutFromJSON(style, o.get());
                }
            }
        }
    }

    private static void setLayoutFromJSON(
            final Style style, final JSONPair[] pairs
    ) {
        for (JSONPair pair : pairs) {
            switch (pair.key()) {
                case ORIENTATION -> style.setAnimationOrientation(
                        Orientation.valueOf(
                                String.valueOf(pair.value()).toUpperCase()));
                case MULTIPLE_ANIMS_PER_DIM -> style.setMultipleAnimsPerDim(
                        Boolean.parseBoolean(String.valueOf(pair.value())));
                case SINGLE_DIM -> style.setSingleDim(
                        Boolean.parseBoolean(String.valueOf(pair.value())));
                case FRAMES_PER_DIM -> style.setFramesPerDim(
                        (Integer) pair.value());
                case WRAP_ACROSS_DIMS -> style.setWrapAnimsAcrossDims(
                        Boolean.parseBoolean(String.valueOf(pair.value())));
            }
        }
    }

    private static void setLayersFromJSON(
            final JSONPair[] layerPairs,
            final Map<String, CustomizationLayer> layerIDMap,
            final List<String> errorList
    ) {
        for (JSONPair layerPair : layerPairs) {
            final String id = layerPair.key();
            final Object value = layerPair.value();

            if (!layerIDMap.containsKey(id)) continue;

            setLayerFromJSON(layerIDMap.get(id), id, value, errorList);
        }
    }

    private static void setLayerFromJSON(
            final CustomizationLayer layer,
            final String id, final Object value,
            final List<String> errorList
    ) {
        if (layer instanceof MathLayer ml && value instanceof Integer i)
            ml.setValue(i);
        else if (layer instanceof ChoiceLayer cl &&
                value instanceof String s) {
            if (!cl.choose(s))
                errorList.add("\"" + s +
                        "\" isn't a valid choice for layer \"" + id + "\"");
        }
        else if (layer instanceof GroupLayer gl &&
                value instanceof JSONObject o)
            setLayersFromJSON(o.get(), layerIDMap(
                    gl.all().toArray(CustomizationLayer[]::new)), errorList);
        else if (layer instanceof ColorSelectionLayer csl &&
                value instanceof JSONArray<?> arr)
            setColorSelectionsFromJSON(csl.getSelections(),
                    Arrays.stream(arr.get()).map(String::valueOf)
                            .toArray(String[]::new));
        else if (layer instanceof AssetChoiceLayer acl) {
            if (value instanceof JSONObject o)
                setACLFromJSON(acl, o.get());
            else if (NONE.equals(value))
                acl.chooseFromScript(AssetChoiceLayer.NONE);
        }
        else if (layer instanceof DecisionLayer dl)
            setLayerFromJSON(dl.getDecision(), id, value, errorList);
    }

    private static void setACLFromJSON(
            final AssetChoiceLayer acl, final JSONPair[] pairs
    ) {
        AssetChoice choice = null;

        for (JSONPair pair : pairs) {
            switch (pair.key()) {
                case CHOICE -> {
                    if (pair.value() instanceof String id && acl.choose(id))
                        choice = acl.getChoice();
                }
                case COLORS -> {
                    if (choice != null &&
                            pair.value() instanceof JSONArray<?> arr)
                        setColorSelectionsFromJSON(choice.getColorSelections(),
                                Arrays.stream(arr.get()).map(String::valueOf)
                                        .toArray(String[]::new));
                }
            }
        }
    }

    private static void setColorSelectionsFromJSON(
            final ColorSelection[] colorSelections,
            final String[] colorCodes
    ) {
        if (colorSelections.length != colorCodes.length)
            return;

        final Color[] colors = Arrays.stream(colorCodes)
                .map(JSONHelper::parseColorCode).toArray(Color[]::new);

        for (int i = 0; i < colorSelections.length; i++)
            colorSelections[i].setColor(colors[i], false);
    }

    private static Color parseColorCode(final String code) {
        if (code == null || !(code.startsWith("#") && code.length() == 7))
            return null;

        return ParserSerializer.deserializeColor(code.substring(1));
    }

    private static Map<String, CustomizationLayer> layerIDMap(
            final CustomizationLayer... layers
    ) {
        return idMap(l -> l.id, layers);
    }

    private static <T> Map<String, T> idMap(
            final Function<T, String> idGetter, final T[] elements
    ) {
        final Map<String, T> idMap = new HashMap<>();

        for (T element : elements)
            idMap.put(idGetter.apply(element), element);

        return idMap;
    }
}
