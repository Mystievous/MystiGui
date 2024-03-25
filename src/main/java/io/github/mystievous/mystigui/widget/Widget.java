package io.github.mystievous.mystigui.widget;

public abstract class Widget {

    private GuiTransform transform;

    public Widget(GuiTransform transform) {
        this.transform = transform;
    }
}
