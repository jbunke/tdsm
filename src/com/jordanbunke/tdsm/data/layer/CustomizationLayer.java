package com.jordanbunke.tdsm.data.layer;

public abstract class CustomizationLayer {
    private boolean expanded;

    public void expand() {
        expanded = true;
    }

    public void collapse() {
        expanded = false;
    }

    public boolean isExpanded() {
        return expanded;
    }

    abstract String name();

    @Override
    public String toString() {
        return name();
    }
}
