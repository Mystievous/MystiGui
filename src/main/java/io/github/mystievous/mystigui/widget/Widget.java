package io.github.mystievous.mystigui.widget;

import io.github.mystievous.mystigui.Gui;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.Map;

public abstract class Widget implements Cloneable {

    @Nullable
    private Gui.GuiHolder guiHolder;
    private Vector2i size;

    public Widget() {
        size = new Vector2i(1,1);
    }

    public void setGuiHolder(@Nullable Gui.GuiHolder guiHolder) {
        this.guiHolder = guiHolder;
    }

    @Nullable
    public Gui.GuiHolder getGuiHolder() {
        return guiHolder;
    }

    public int getArea() {
        return size.x() * size.y();
    }

    public Vector2i getSize() {
        return size;
    }

    protected void setSize(Vector2i size) {
        this.size = size;
    }

    protected Vector2i indexToVector(int index) {
        int width = getSize().x();
        int x = index % width;
        int y = index / width;
        return new Vector2i(x, y);
    }

    protected int vectorToIndex(Vector2i vector) {
        int x = vector.x();
        int y = vector.y();
        return y * getSize().x() + x;
    }

    public void onChange() {
        if (guiHolder != null) {
            guiHolder.loadInventory();
        }
    }

    /**
     * All widgets should render down to the most basic `ItemWidget`
     * @return positions of all the rendered widgets
     */
    public abstract Map<Vector2i, ItemWidget> render();

    @Override
    public Widget clone() {
        try {
            Widget widget = (Widget) super.clone();

            widget.size = (Vector2i) this.size.clone();

            return widget;
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }
}
