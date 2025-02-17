package com.jordanbunke.tdsm.data.layer;

import com.jordanbunke.tdsm.menu.layer.LayerElement;

public abstract class ManualRefreshLayer extends CustomizationLayer {
    private LayerElement element;

    ManualRefreshLayer(final String id) {
        super(id);

        element = null;
    }

    public void setElement(final LayerElement element) {
        this.element = element;
    }

    void refreshElement() {
        if (element != null)
            element.refresh();
    }
}
