package com.starseekstudios.mystigui.widget;

import com.starseekstudios.mystigui.Gui;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.*;
import java.util.function.Consumer;

public abstract class Widget implements Cloneable {

    @Nullable
    private Gui.GuiHolder guiHolder;
    private Vector2i size;

    private List<Consumer<Widget>> onReloads = new ArrayList<>();

    private Key label;

    public Widget() {
        size = new Vector2i(1, 1);
    }

    /**
     * Adds a consumer to run on reload.
     * Puts it at the *beginning* of the list,
     * so it will run before any previously set ones.
     *
     * @param onReload Consumer to add to the list.
     */
    public void addOnReload(Consumer<Widget> onReload) {
        onReloads.addFirst(onReload);
    }

    public void setLabel(Key label) {
        this.label = label;
    }

    public Optional<Key> getLabel() {
        return Optional.ofNullable(label);
    }

    public Optional<? extends Widget> getLabeledWidget(Key key) {
        return Optional.empty();
    }

    public <T extends Widget> Optional<? extends T> getLabeledWidget(Class<T> widgetClass, Key key) {
        Optional<? extends Widget> labeledWidget = getLabeledWidget(key);
        if (labeledWidget.isEmpty()) {
            return Optional.empty();
        }

        Widget widget = labeledWidget.get();

        if (widgetClass.isInstance(widget)) {
            T classWidget = (T) widget;
            return Optional.of(classWidget);
        }

        return Optional.empty();
    }

    public Optional<WidgetSlot> getWidgetForPosition(Vector2i position) {
        return Optional.empty();
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

    public Vector2i indexToVector(int index) {
        int width = getSize().x();
        int x = index % width;
        int y = index / width;
        return new Vector2i(x, y);
    }

    public int vectorToIndex(Vector2i vector) {
        int x = vector.x();
        int y = vector.y();
        return y * getSize().x() + x;
    }

    public void reloadInventory() {
        getGuiHolder().ifPresent(Gui.GuiHolder::reloadInventory);
    }

    public void onReload() {
        onReloads.forEach(r -> r.accept(this));
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
            widget.onReloads = new ArrayList<>(this.onReloads);

            return widget;
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Widget widget = (Widget) o;
        return Objects.equals(guiHolder, widget.guiHolder) && Objects.equals(size, widget.size) && Objects.equals(onReloads, widget.onReloads) && Objects.equals(label, widget.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guiHolder, size, onReloads, label);
    }
}
