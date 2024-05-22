package com.starseekstudios.mystigui.widget;

import com.starseekstudios.mystigui.Gui;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class Widget implements Cloneable {

    @Nullable
    private Gui.GuiHolder guiHolder;
    private Vector2i size;

    private Consumer<Widget> onReload;

    private Key label;

    public Widget() {
        size = new Vector2i(1, 1);
    }

    protected void setOnReload(Consumer<Widget> onReload) {
        this.onReload = onReload;
    }

    private Optional<Consumer<Widget>> getOnReload() {
        return Optional.ofNullable(onReload);
    }

    public void setLabel(Key label) {
        this.label = label;
    }

    public Optional<Key> getLabel() {
        return Optional.ofNullable(label);
    }

    public void setGuiHolder(@Nullable Gui.GuiHolder guiHolder) {
        this.guiHolder = guiHolder;
    }

    public Optional<Gui.GuiHolder> getGuiHolder() {
        return Optional.ofNullable(guiHolder);
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
        getGuiHolder().ifPresent(Gui.GuiHolder::loadInventory);
    }

    public void onReload() {
        getOnReload().ifPresent(onReload -> onReload.accept(this));
    }

    /**
     * All widgets should render down to the most basic `ItemWidget`
     *
     * @return positions of all the rendered widgets
     */
    public abstract Map<Vector2i, ItemWidget> render();

    @Override
    public Widget clone() {
        try {
            Widget widget = (Widget) super.clone();

            widget.size = (Vector2i) this.size.clone();
            widget.onReload = this.onReload;

            return widget;
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }
}
