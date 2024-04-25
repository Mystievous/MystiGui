package io.github.mystievous.mystigui.widget;

import org.bukkit.inventory.ItemStack;
import org.joml.Vector2i;

import java.util.HashMap;
import java.util.Map;

public class ItemWidget extends Widget {

    private ItemStack item;

    public ItemWidget(ItemStack widgetItem) {
        this.item = widgetItem;
    }

    @Override
    public Map<Vector2i, ItemStack> render() {
        Map<Vector2i, ItemStack> output = new HashMap<>();
        output.put(new Vector2i(), item);
        return output;
    }

    @Override
    public ItemWidget clone() {
        ItemWidget widget = (ItemWidget) super.clone();

        widget.item = this.item.clone();

        return widget;
    }
}
