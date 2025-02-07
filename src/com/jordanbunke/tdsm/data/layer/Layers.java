package com.jordanbunke.tdsm.data.layer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Layers {
    private final List<CustomizationLayer> layers;

    public Layers() {
        layers = new ArrayList<>();
    }

    public List<CustomizationLayer> get() {
        return layers;
    }

    public void add(final CustomizationLayer... toAdd) {
        layers.addAll(Arrays.stream(toAdd).toList());
    }
}
