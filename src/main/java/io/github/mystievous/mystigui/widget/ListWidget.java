package io.github.mystievous.mystigui.widget;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
        page = 1;
        this.items = new ArrayList<>();
        setSize(size);
    }

    public void addWidget(ItemWidget itemWidget) {
        items.add(itemWidget);
    }

    public void addItem(ItemStack itemStack) {
        addWidget(new ItemWidget(itemStack));
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void previousPage() {
        page = Math.max(page - 1, 0);
        onChange();
    }

    public void nextPage() {
        page = Math.min(page + 1, getPageForIndex(items.size() - 1));
        onChange();
    }

    public int getPageForIndex(int index) {
        if (index < getArea()) {
            return 1;
        }

        return (int) Math.ceil((float) ((index + 1) + 1 - getArea()) / (getArea() - 2)) + 1;
    }

    public int maxItemsInPage(int page) {
        if (page == 1) {
            int maxPage = getPageForIndex(items.size() - 1);
            if (maxPage == 1) {
                return getArea();
            }
            return getArea() - 1;
        }

        return getArea() - 2;

    }

    public int pageStartIndex(int page) {
        if (page == 1) {
            return 0;
        }

        if (page == 2) {
            return getArea() - 1;
        }

        return (getArea() - 1) + ((getArea() - 2) * page);
    }

    @Override
    public Map<Vector2i, ItemWidget> render() {
        Map<Vector2i, ItemWidget> renderedItems = new HashMap<>();

        int startIndex = pageStartIndex(page);
        int endIndex = Math.min(startIndex + maxItemsInPage(page), items.size());
        for (int i = startIndex; i < endIndex; i++) {
            ItemWidget itemWidget = items.get(i);
            renderedItems.put(indexToVector(i - startIndex), itemWidget.render().get(new Vector2i()));
        }
        if (page == 1 && items.size() > getArea()) {
            ItemWidget nextPageWidget = new ItemWidget(new ItemStack(Material.ARROW));
            nextPageWidget.setClickAction(event -> {
                nextPage();
            });
            renderedItems.put(new Vector2i(getSize().x() - 1, getSize().y() - 1), nextPageWidget);
        }
        if (page > 1) {
            ItemWidget nextPageWidget = new ItemWidget(new ItemStack(Material.ARROW));
            nextPageWidget.setClickAction(event -> nextPage());

            renderedItems.put(new Vector2i(getSize().x() - 1, getSize().y() - 1), nextPageWidget);
            ItemWidget lastPageWidget = new ItemWidget(new ItemStack(Material.ARROW));
            lastPageWidget.setClickAction(event -> previousPage());
            renderedItems.put(new Vector2i(getSize().x() - 2, getSize().y() - 1), lastPageWidget);
        }
        return renderedItems;
    }

    @Override
    public ListWidget clone() {
        ListWidget widget = (ListWidget) super.clone();

        widget.items = new ArrayList<>();
        widget.page = this.page;
        this.items.forEach(widget1 -> widget.addWidget(widget1.clone()));

        return widget;
    }

}
