package com.jordanbunke.tdsm.data.layer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Layers {
    private final List<CustomizationLayer> customization, assembly;

    public Layers() {
        assembly = new ArrayList<>();
        customization = new ArrayList<>();
    }

    public List<CustomizationLayer> customization() {
        return customization;
    }

    public List<CustomizationLayer> assembly() {
        return assembly;
    }

    public void addToCustomization(final CustomizationLayer... toAdd) {
        customization.addAll(Arrays.stream(toAdd)
                .filter(CustomizationLayer::isNonTrivial).toList());
    }

    public void addToAssembly(final CustomizationLayer... toAdd) {
        assembly.addAll(Arrays.stream(toAdd).toList());
    }

    public void add(final CustomizationLayer... toAdd) {
        addToCustomization(toAdd);
        addToAssembly(toAdd);
    }
}
