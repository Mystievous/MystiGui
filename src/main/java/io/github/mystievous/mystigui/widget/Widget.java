package io.github.mystievous.mystigui.widget;

import org.bukkit.inventory.ItemStack;
import org.joml.Vector2i;

import java.util.Map;

public abstract class Widget {

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

    public abstract Map<Vector2i, ItemStack> render();
}
