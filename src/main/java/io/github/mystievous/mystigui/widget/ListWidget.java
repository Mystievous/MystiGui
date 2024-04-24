package io.github.mystievous.mystigui.widget;

import org.bukkit.inventory.ItemStack;
import org.joml.Vector2i;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListWidget extends Widget {

    private @Nullable ListWidget nextPage;
    private @Nullable ListWidget prevPage;

    private List<ItemWidget> items;

    public ListWidget(Vector2i size) {
        super();
        this.items = new ArrayList<>();
        setSize(size);
    }

    public void addItem(ItemStack itemStack) {
        items.add(new ItemWidget(itemStack));
    }

    @Override
    public Map<Vector2i, ItemStack> render() {
        Map<Vector2i, ItemStack> renderedItems = new HashMap<>();
        for (int i = 0; i < items.size(); i++) {
            ItemWidget itemWidget = items.get(i);
            renderedItems.put(indexToVector(i), itemWidget.render().get(new Vector2i()));
        }
        return renderedItems;
    }

    public int getFreeArea() {
        return getArea() - countTakenSlots();
    }

    private int countTakenSlots() {
        int output = 0;
        if (prevPage != null) {
            output += 2;
        } else if (nextPage != null) {
            output += 1;
        }

        return output;
    }
}
