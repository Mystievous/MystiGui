package com.starseekstudios.mystigui.widget;

import com.starseekstudios.mysticore.ItemUtil;
import com.starseekstudios.mysticore.Palette;
import com.starseekstudios.mystigui.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.joml.Vector2i;

import java.util.*;
import java.util.function.Function;

public class ListWidget extends Widget {

    private List<ItemWidget> items = new ArrayList<>();

    private int page;

    private boolean clearEmptySpaces;
    private boolean showPageButtons = true;

    public ListWidget(Vector2i size) {
        super();
        page = 1;
        setSize(size);
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
        this.showPageButtons = showPageButtons;
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

    public boolean hasNextPage() {
        return getPage() < getMaxPage();
    }

    public boolean hasPreviousPage() {
        return getPage() > 1;
    }

    public void previousPage() {
        if (page != 1) {
            page = Math.max(page - 1, 1);
            onChange();
        }
    }

    public void nextPage() {
        if (page != getMaxPage()) {
            page = Math.min(page + 1, getMaxPage());
            onChange();
        }
    }

    public int getMaxPage() {
        return getPageForIndex(items.size() - 1);
    }

    public int getItemsPerPage() {
        if (showPageButtons) {
            return getArea() - 2;
        } else {
            return getArea();
        }
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
    public Map<Vector2i, ItemWidget> render(Gui.GuiHolder guiHolder) {
        Map<Vector2i, ItemWidget> renderedItems = new HashMap<>();

        int startIndex = pageStartIndex(page);
        int endSize = Math.min(startIndex + getItemsPerPage(), items.size());
        for (int i = startIndex; i < endSize; i++) {
            ItemWidget itemWidget = items.get(i);
            Vector2i position = indexToVector(i - startIndex);
            renderedItems.put(position, itemWidget.render(guiHolder).get(new Vector2i()));
        }
        if (clearEmptySpaces) {
            for (int i = endSize; i < startIndex + getItemsPerPage(); i++) {
                ItemWidget itemWidget = new ItemWidget(new ItemStack(Material.AIR));
                Vector2i position = indexToVector(i - startIndex);
                renderedItems.put(position, itemWidget.render(guiHolder).get(new Vector2i()));
            }
        }
        if (showPageButtons) {
            renderedItems.put(new Vector2i(getSize()).sub(2, 1), previousPageWidget(getPage() > 1));
            renderedItems.put(new Vector2i(getSize()).sub(1, 1), nextPageWidget(getPage() < getMaxPage()));
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