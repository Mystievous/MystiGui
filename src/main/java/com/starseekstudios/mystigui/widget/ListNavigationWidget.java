package com.starseekstudios.mystigui.widget;

import com.starseekstudios.mysticore.Palette;
import com.starseekstudios.mystigui.Gui;
import com.starseekstudios.mystigui.Icons;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ListNavigationWidget extends ItemWidget {

    public static ListNavigationWidget previousPageWidget(NamespacedKey listWidgetKey) {
        ItemStack itemStack = Icons.leftArrow(Component.text("Previous Page"), Palette.DISABLED);
        Consumer<InventoryClickEvent> onClick = getOnClick(ListWidget::previousPage, listWidgetKey);
        ListNavigationWidget widget = new ListNavigationWidget(itemStack, listWidgetKey, true);
        widget.setClickAction(onClick);
        return widget;
    }

    public static ListNavigationWidget nextPageWidget(NamespacedKey listWidgetKey) {
        ItemStack itemStack = Icons.rightArrow(Component.text("Next Page"), Palette.DISABLED);
        Consumer<InventoryClickEvent> onClick = getOnClick(ListWidget::nextPage, listWidgetKey);
        ListNavigationWidget widget = new ListNavigationWidget(itemStack, listWidgetKey, false);
        widget.setClickAction(onClick);
        return widget;
    }

    private final NamespacedKey listWidgetKey;
    private final boolean isPrevious;

    private ListNavigationWidget(ItemStack itemStack, NamespacedKey listWidgetKey, boolean isPrevious) {
        super(itemStack);
        this.listWidgetKey = listWidgetKey;
        this.isPrevious = isPrevious;
        setOnReload(widget -> {
            widget.getGuiHolder().ifPresent(guiHolder -> {
                guiHolder.getLabeledWidget(listWidgetKey).ifPresent(checkWidget -> {
                    if (checkWidget instanceof ListWidget listWidget) {
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
                    }
                });
            });
        });
    }

    private boolean isEnabled(ListWidget listWidget) {
        return (isPrevious && listWidget.hasPreviousPage()) || (!isPrevious && listWidget.hasNextPage());
    }

    @NotNull
    private static Consumer<InventoryClickEvent> getOnClick(Consumer<ListWidget> onListClick, NamespacedKey listWidgetKey) {
        return event -> {
            if (event.getInventory().getHolder() instanceof Gui.GuiHolder guiHolder) {
                guiHolder.getLabeledWidget(listWidgetKey).ifPresent(widget -> {
                    if (widget instanceof ListWidget listWidget) {
                        onListClick.accept(listWidget);
                    }
                });
            }
        };
    }

}
