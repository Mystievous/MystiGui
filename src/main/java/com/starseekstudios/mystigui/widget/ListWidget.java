package com.starseekstudios.mystigui.widget;

import com.starseekstudios.mysticore.ItemUtil;
import com.starseekstudios.mysticore.Palette;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.joml.Vector2i;

import java.util.*;
import java.util.function.Function;

public class ListWidget extends Widget {

    private List<ItemWidget> items = new ArrayList<>();

    private HashMap<Vector2i, ItemWidget> placedItems = new HashMap<>();

    private int page;

    private boolean clearEmptySpaces;

    public ListWidget(Vector2i size) {
        super();
        page = 1;
        setSize(size);
        setShowPageButtons(true);
    }

    public static ListWidget filled(Vector2i size, ItemWidget itemWidget) {
        ListWidget listWidget = new ListWidget(size);
        for (int i = 0; i < listWidget.getArea(); i++) {
            listWidget.addWidget(itemWidget);
        }
        listWidget.setShowPageButtons(false);
        return listWidget;
    }

    public static <T> ListWidget fromCollection(Vector2i size, Collection<T> collection, Function<? super T, ItemWidget> toWidget) {
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

    public void setShowPageButtons(boolean showPageButtons) {
        if (showPageButtons) {
            placedItems.put(getSize().sub(2, 0), previousPageWidget(getPage() > 1));
            placedItems.put(getSize().sub(1, 0), nextPageWidget(getPage() < getMaxPage()));
        } else {
            placedItems.remove(getSize().sub(2, 0));
            placedItems.remove(getSize().sub(1, 0));
        }
    }

    public void setClearEmptySpaces(boolean clearEmptySpaces) {
        this.clearEmptySpaces = clearEmptySpaces;
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

    public int getItemsPerPage() {
        return getArea() - placedItems.size();
    }

    public int getPageForIndex(int index) {
        return (int) Math.ceil((float) index / getItemsPerPage());
    }

    public int pageStartIndex(int page) {
        return (page - 1) * getItemsPerPage();
    }

    private static void setPageButtonColor(LeatherArmorMeta leatherArmorMeta, boolean enabled) {
        leatherArmorMeta.setColor(enabled ? Palette.PRIMARY.toBukkitColor() : Palette.DISABLED.toBukkitColor());
        ItemUtil.hideExtraTooltip(leatherArmorMeta);
    }

    private ItemWidget nextPageWidget(boolean enabled) {
        ItemStack arrowItem = ItemUtil.createItem(Component.text("Next Page"), Material.LEATHER_BOOTS, 2);
        arrowItem.editMeta(LeatherArmorMeta.class, itemMeta -> {
            setPageButtonColor(itemMeta, enabled);
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
        Map<Vector2i, ItemWidget> renderedItems = new HashMap<>();

        int startIndex = pageStartIndex(page);
        int endIndex = Math.min(startIndex + getItemsPerPage(), items.size());
        int offsetForPlacedItems = 0;
        for (int i = startIndex; i < endIndex; i++) {
            ItemWidget itemWidget = items.get(i);
            Vector2i position = indexToVector(i - startIndex + offsetForPlacedItems);
            while (placedItems.containsKey(position)) {
                renderedItems.put(position, placedItems.get(position));

                offsetForPlacedItems++;
                position = indexToVector(i - startIndex + offsetForPlacedItems);
            }
            renderedItems.put(position, itemWidget.render().get(new Vector2i()));
        }
        if (clearEmptySpaces) {
            for (int i = endIndex; i < startIndex + getItemsPerPage(); i++) {
                ItemWidget itemWidget = new ItemWidget(new ItemStack(Material.AIR));
                Vector2i position = indexToVector(i - startIndex + offsetForPlacedItems);
                while (placedItems.containsKey(position)) {
                    renderedItems.put(position, placedItems.get(position));

                    offsetForPlacedItems++;
                    position = indexToVector(i - startIndex + offsetForPlacedItems);
                }
                renderedItems.put(position, itemWidget.render().get(new Vector2i()));
            }
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
