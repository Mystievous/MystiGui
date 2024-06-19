package com.starseekstudios.mystigui.widget;

import com.starseekstudios.mysticore.Palette;
import com.starseekstudios.mystigui.Gui;
import com.starseekstudios.mystigui.Icons;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class ListNavigationWidget extends ItemWidget {

    public static ListNavigationWidget previousPageWidget(NamespacedKey listWidgetKey) {
        ItemStack itemStack = Icons.leftArrow(Component.text("Previous Page"), Palette.DISABLED);
        Consumer<InventoryClickEvent> onClick = getOnClick(ListWidget::goToPreviousPage, listWidgetKey);
        ListNavigationWidget widget = new ListNavigationWidget(itemStack, listWidgetKey, true);
        widget.setClickAction(onClick);
        return widget;
    }

    public static ListNavigationWidget nextPageWidget(NamespacedKey listWidgetKey) {
        ItemStack itemStack = Icons.rightArrow(Component.text("Next Page"), Palette.DISABLED);
        Consumer<InventoryClickEvent> onClick = getOnClick(ListWidget::goToNextPage, listWidgetKey);
        ListNavigationWidget widget = new ListNavigationWidget(itemStack, listWidgetKey, false);
        widget.setClickAction(onClick);
        return widget;
    }

    private boolean isPrevious;

    private ListNavigationWidget(ItemStack itemStack, NamespacedKey listWidgetKey, boolean isPrevious) {
        super(itemStack);
        this.isPrevious = isPrevious;
        addOnReload(widget -> {
            widget.getGuiHolder()
                    .flatMap(g -> g.getLabeledWidget(listWidgetKey))
                    .filter(ListWidget.class::isInstance)
                    .map(ListWidget.class::cast)
                    .ifPresent(listWidget -> {
                        boolean enabled = isEnabled(listWidget);
                        ((ItemWidget) widget).modifyItem(modItem -> {
                            modItem.editMeta(LeatherArmorMeta.class, leatherArmorMeta -> {
                                if (enabled) {
                                    leatherArmorMeta.setColor(Palette.PRIMARY.toBukkitColor());
                                } else {
                                    leatherArmorMeta.setColor(Palette.DISABLED.toBukkitColor());
                                }
                            });
                        });
                    });
        });
    }

    private boolean isEnabled(ListWidget listWidget) {
        return (isPrevious && listWidget.hasPreviousPage()) || (!isPrevious && listWidget.hasNextPage());
    }

    @NotNull
    private static Consumer<InventoryClickEvent> getOnClick(Function<ListWidget, Boolean> onListClick, NamespacedKey listWidgetKey) {
        return event -> {
            if (event.getInventory().getHolder() instanceof Gui.GuiHolder guiHolder) {
                guiHolder.getLabeledWidget(listWidgetKey).ifPresent(widget -> {
                    if (widget instanceof ListWidget listWidget) {
                        if (onListClick.apply(listWidget)) {
                            listWidget.reloadInventory();
                        }
                    }
                });
            }
        };
    }

    @Override
    public ListNavigationWidget clone() {
        ListNavigationWidget widget = (ListNavigationWidget) super.clone();

        widget.isPrevious = this.isPrevious;

        return widget;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ListNavigationWidget widget = (ListNavigationWidget) o;
        return isPrevious == widget.isPrevious;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isPrevious);
    }
}
