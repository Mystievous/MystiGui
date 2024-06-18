package com.starseekstudios.mystigui.widget;

import com.starseekstudios.mysticore.Color;
import com.starseekstudios.mysticore.Palette;
import com.starseekstudios.mystigui.Gui;
import com.starseekstudios.mystigui.Icons;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.*;
import java.util.function.Function;

public class ListWidget extends Widget {

    private List<ItemWidget> items = new ArrayList<>();
    protected Map<Key, ItemWidget> labeledWidgets = new HashMap<>();

    private int page;

    private boolean clearEmptySpaces;
    private boolean showPageButtons = true;

    public ListWidget(Vector2i size) {
        super();
        page = 1;
        setSize(size);
        setOnReload((widget) -> {
            if (widget instanceof ListWidget listWidget) {
                listWidget.items.forEach(Widget::onReload);
            }
        });
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
    public Optional<WidgetSlot> getWidgetForPosition(Vector2i position) {
        if (position.x() >= getSize().x() || position.y() >= getSize().y()) {
            return Optional.empty();
        }

        int index = vectorToIndex(position);
        if (index >= getItemsPerPage()) {
            return Optional.empty();
        }
        int itemIndex = pageStartIndex(getPage()) + index;
        return Optional.of(new WidgetSlot(items.get(itemIndex), new Vector2i()));
    }

    @Override
    public Optional<? extends ItemWidget> getLabeledWidget(Key key) {
        return Optional.ofNullable(labeledWidgets.get(key));
    }

    public void remove(ItemWidget itemWidget) {
        itemWidget.getLabel().ifPresent(labeledWidgets::remove);
        items.remove(itemWidget);
    }

    public void remove(int index) {
        ItemWidget itemWidget = items.get(index);
        itemWidget.getLabel().ifPresent(labeledWidgets::remove);
        items.remove(index);
    }

    public void sort(Comparator<? super ItemWidget> comparator) {
        items.sort(comparator);
    }

    @Override
    public void setGuiHolder(Gui.@Nullable GuiHolder guiHolder) {
        super.setGuiHolder(guiHolder);
        items.forEach(itemWidget -> itemWidget.setGuiHolder(guiHolder));
    }

    @Override
    public void setSize(Vector2i size) {
        super.setSize(size);
    }

    public void addWidget(int index, ItemWidget widget) {
        items.add(index, widget);
        widget.getLabel().ifPresent(key -> labeledWidgets.put(key, widget));
    }

    public void addWidget(ItemWidget itemWidget) {
        items.add(itemWidget);
        itemWidget.getLabel().ifPresent(key -> labeledWidgets.put(key, itemWidget));
    }

    public void addAll(Collection<ItemWidget> widgets) {
        items.addAll(widgets);
        widgets.stream()
                .filter(itemWidget -> itemWidget.getLabel().isPresent())
                .forEach(itemWidget -> labeledWidgets.put(itemWidget.getLabel().get(), itemWidget));
    }

    public void setShowPageButtons(boolean showPageButtons) {
        this.showPageButtons = showPageButtons;
    }

    public void setClearEmptySpaces(boolean clearEmptySpaces) {
        this.clearEmptySpaces = clearEmptySpaces;
    }

    public void addItem(int index, ItemStack item) {
        items.add(index, new ItemWidget(item));
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

    public boolean goToPreviousPage() {
        if (page != 1) {
            page = Math.max(page - 1, 1);
            return true;
        }
        return false;
    }

    public boolean goToNextPage() {
        if (page != getMaxPage()) {
            page = Math.min(page + 1, getMaxPage());
            return true;
        }
        return false;
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

    private ItemWidget nextPageWidget(boolean enabled) {
        Color color = enabled ? Palette.PRIMARY : Palette.DISABLED;
        ItemStack arrowItem = Icons.rightArrow(Component.text("Next Page"), color);
        ItemWidget nextPageWidget = new ItemWidget(arrowItem);
        nextPageWidget.setClickAction(event -> {
            if (goToNextPage()) {
                reloadInventory();
            }
        });
        return nextPageWidget;
    }

    private ItemWidget previousPageWidget(boolean enabled) {
        Color color = enabled ? Palette.PRIMARY : Palette.DISABLED;
        ItemStack arrowItem = Icons.leftArrow(Component.text("Previous Page"), color);
        ItemWidget previousPageWidget = new ItemWidget(arrowItem);
        previousPageWidget.setClickAction(event -> {
            if (goToPreviousPage()) {
                reloadInventory();
            }
        });
        return previousPageWidget;
    }

    @Override
    public Map<Vector2i, ItemWidget> render() {
        Map<Vector2i, ItemWidget> renderedItems = new HashMap<>();

        int startIndex = pageStartIndex(page);
        int endSize = Math.min(startIndex + getItemsPerPage(), items.size());
        for (int i = startIndex; i < endSize; i++) {
            ItemWidget itemWidget = items.get(i);
            Vector2i position = indexToVector(i - startIndex);
            renderedItems.put(position, itemWidget.render().get(new Vector2i()));
        }
        if (clearEmptySpaces) {
            for (int i = endSize; i < startIndex + getItemsPerPage(); i++) {
                ItemWidget itemWidget = new ItemWidget(new ItemStack(Material.AIR));
                Vector2i position = indexToVector(i - startIndex);
                renderedItems.put(position, itemWidget.render().get(new Vector2i()));
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
        this.items.forEach(widget1 -> widget.addWidget(widget1.clone()));

        return widget;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ListWidget that = (ListWidget) o;
        return page == that.page && clearEmptySpaces == that.clearEmptySpaces && showPageButtons == that.showPageButtons && Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), items, page, clearEmptySpaces, showPageButtons);
    }
}
