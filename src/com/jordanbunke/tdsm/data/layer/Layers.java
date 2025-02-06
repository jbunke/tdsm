package com.jordanbunke.tdsm.data.layer;

import java.util.ArrayList;
import java.util.List;

public class Layers {
    private final List<CustomizationLayer> layers;

    public Layers() {
        layers = new ArrayList<>();
    }

    public List<CustomizationLayer> get() {
        return layers;
    }
}
