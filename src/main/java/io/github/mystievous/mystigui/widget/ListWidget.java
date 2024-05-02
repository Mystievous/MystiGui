package io.github.mystievous.mystigui.widget;

import io.github.mystievous.mysticore.ItemUtil;
import io.github.mystievous.mysticore.Palette;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.joml.Vector2i;

import java.util.*;
import java.util.function.Function;

public class ListWidget extends Widget {

    private List<ItemWidget> items;

    private int page;

    public ListWidget(Vector2i size) {
        super();
        page = 1;
        this.items = new ArrayList<>();
        setSize(size);
    }

    public static ListWidget filled(Vector2i size, ItemWidget itemWidget) {
        ListWidget listWidget = new ListWidget(size);
        for (int i = 0; i < listWidget.getArea(); i++) {
            listWidget.addWidget(itemWidget);
        }
        return listWidget;
    }

    public static <T> ListWidget fromCollection(Vector2i size, Collection<T> collection, Function<T, ItemWidget> toWidget) {
        ListWidget listWidget = new ListWidget(size);
        listWidget.addAll(collection.stream().map(toWidget).toList());
        return listWidget;
    }

    @Override
    public void setSize(Vector2i size) {
        super.setSize(size);
    }

    public void addWidget(ItemWidget itemWidget) {
        items.add(itemWidget);
    }

    public void addAll(Collection<ItemWidget> widgets) {
        items.addAll(widgets);
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
        page = Math.min(page + 1, getMaxPage());
        onChange();
    }

    public int getMaxPage() {
        return getPageForIndex(items.size() - 1);
    }

    public int getPageForIndex(int index) {
        if (index < getArea()) {
            return 1;
        }

        return (int) Math.ceil((float) ((index + 1) + 1 - getArea()) / (getArea() - 2)) + 1;
    }

    public int maxItemsInPage(int page) {
        if (page == 1) {
            if (getMaxPage() == 1) {
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

    private static void setPageButtonColor(LeatherArmorMeta leatherArmorMeta, boolean enabled) {
        leatherArmorMeta.setColor(enabled ? Palette.PRIMARY.toBukkitColor() : Palette.GRAYED_OUT.toBukkitColor());
        leatherArmorMeta.addItemFlags(ItemFlag.HIDE_DYE);
        leatherArmorMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    }

    private ItemWidget nextPageWidget(boolean enabled) {
        ItemStack arrowItem = ItemUtil.createItem(Component.text("Next Page"), Material.LEATHER_BOOTS, 2);
        arrowItem.editMeta(itemMeta -> {
            if (!(itemMeta instanceof LeatherArmorMeta leatherArmorMeta)) return;
            setPageButtonColor(leatherArmorMeta, enabled);
        });
        ItemWidget nextPageWidget = new ItemWidget(arrowItem);
        nextPageWidget.setClickAction(event -> {
            nextPage();
        });
        return nextPageWidget;
    }

    private ItemWidget previousPageWidget(boolean enabled) {
        ItemStack arrowItem = ItemUtil.createItem(Component.text("Previous Page"), Material.LEATHER_BOOTS, 1);
        arrowItem.editMeta(itemMeta -> {
            if (!(itemMeta instanceof LeatherArmorMeta leatherArmorMeta)) return;
            setPageButtonColor(leatherArmorMeta, enabled);
        });
        ItemWidget previousPageWidget = new ItemWidget(arrowItem);
        previousPageWidget.setClickAction(event -> {
            previousPage();
        });
        return previousPageWidget;
    }

    @Override
    public Map<Vector2i, ItemWidget> render() {
        Runnable beforeRender = beforeRender();
        if (beforeRender != null) beforeRender.run();

        Map<Vector2i, ItemWidget> renderedItems = new HashMap<>();

        int startIndex = pageStartIndex(page);
        int endIndex = Math.min(startIndex + maxItemsInPage(page), items.size());
        for (int i = startIndex; i < endIndex; i++) {
            ItemWidget itemWidget = items.get(i);
            renderedItems.put(indexToVector(i - startIndex), itemWidget.render().get(new Vector2i()));
        }
        boolean hasMorePages = getMaxPage() > page;
        if (page == 1 && items.size() > getArea()) {
            renderedItems.put(new Vector2i(getSize().x() - 1, getSize().y() - 1), nextPageWidget(hasMorePages));
        }
        if (page > 1) {
            renderedItems.put(new Vector2i(getSize().x() - 1, getSize().y() - 1), nextPageWidget(hasMorePages));

            renderedItems.put(new Vector2i(getSize().x() - 2, getSize().y() - 1), previousPageWidget(true));
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
