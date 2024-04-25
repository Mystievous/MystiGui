package io.github.mystievous.mystigui.widget;

import org.bukkit.inventory.ItemStack;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListWidget extends Widget {

    private List<ItemWidget> items;

    private int page;

    public ListWidget(Vector2i size) {
        super();
        page = 0;
        this.items = new ArrayList<>();
        setSize(size);
    }

    public void addItem(ItemStack itemStack) {
        items.add(new ItemWidget(itemStack));
    }

    public void addWidget(ItemWidget itemWidget) {
        items.add(itemWidget);
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }


    public int getPageForIndex(int index) {
        if (index < getArea()) {
            return 1;
        }

        return (int) Math.ceil((float) ((index + 1) + 1 - getArea()) / (getArea() - 2));
    }

    public int pageStartIndex() {
        if (page == 1) {
            return 0;
        }

        if (page == 2) {
            return getArea() - 1;
        }

        return (getArea() - 1) + ((getArea() - 2) * page);
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

    @Override
    public ListWidget clone() {
        ListWidget widget = (ListWidget) super.clone();

        widget.items = new ArrayList<>();
        this.items.forEach(widget1 -> {
            widget.addWidget(widget1.clone());
        });

        return widget;
    }

}
