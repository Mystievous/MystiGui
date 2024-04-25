package io.github.mystievous.mystigui.widget;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector2i;

import java.util.Map;

public abstract class Widget implements Cloneable {

    private Vector2i size;

    public Widget() {
        size = new Vector2i(1,1);
    }

    public int getArea() {
        return size.x() * size.y();
    }

    public Vector2i getSize() {
        return size;
    }

    public void setSize(Vector2i size) {
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

    public boolean emitChange() {
        WidgetChangeEvent event = new WidgetChangeEvent(this);
        Bukkit.getPluginManager().callEvent(event);
        return event.isCancelled();
    }

    public abstract Map<Vector2i, ItemStack> render();

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
