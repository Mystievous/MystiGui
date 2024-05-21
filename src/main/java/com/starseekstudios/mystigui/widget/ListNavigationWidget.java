package com.starseekstudios.mystigui.widget;

import com.starseekstudios.mysticore.ItemUtil;
import com.starseekstudios.mysticore.Palette;
import com.starseekstudios.mystigui.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class ListNavigationWidget extends Widget {

    private final NamespacedKey listWidgetKey;
    private final boolean isPrevious;
    private final ItemStack itemStack;
    private final Consumer<InventoryClickEvent> onClick;

    public static ListNavigationWidget previousPageWidget(NamespacedKey listWidgetKey) {
        return new ListNavigationWidget(listWidgetKey, true);
    }

    public static ListNavigationWidget nextPageWidget(NamespacedKey listWidgetKey) {
        return new ListNavigationWidget(listWidgetKey, false);
    }

    private ListNavigationWidget(NamespacedKey listWidgetKey, boolean isPrevious) {
        super();
        this.listWidgetKey = listWidgetKey;
        this.isPrevious = isPrevious;

        if (isPrevious) {
            itemStack = ItemUtil.createItem(Component.text("Previous Page"), Material.LEATHER_BOOTS, 1);
            onClick = getOnClick(ListWidget::previousPage);
        } else {
            itemStack = ItemUtil.createItem(Component.text("Next Page"), Material.LEATHER_BOOTS, 2);
            onClick = getOnClick(ListWidget::nextPage);
        }
    }

    @NotNull
    private Consumer<InventoryClickEvent> getOnClick(Consumer<ListWidget> onListClick) {
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

    private boolean isEnabled(ListWidget listWidget) {
        return (isPrevious && listWidget.hasPreviousPage()) || (!isPrevious && listWidget.hasNextPage());
    }

    @Override
    public Map<Vector2i, ItemWidget> render(Gui.GuiHolder guiHolder) {
        Optional<Widget> checkWidget = guiHolder.getLabeledWidget(listWidgetKey);
        ItemStack item = itemStack.clone();
        checkWidget.ifPresent(widget -> {
            if (widget instanceof ListWidget listWidget) {
                boolean enabled = isEnabled(listWidget);
                item.editMeta(LeatherArmorMeta.class, leatherArmorMeta -> {
                    if (enabled) {
                        leatherArmorMeta.setColor(Palette.PRIMARY.toBukkitColor());
                    } else {
                        leatherArmorMeta.setColor(Palette.DISABLED.toBukkitColor());
                    }
                    ItemUtil.hideExtraTooltip(leatherArmorMeta);
                });
            }
        });
        ItemWidget widget = new ItemWidget(item);
        widget.setClickAction(onClick);
        return new HashMap<>(Map.of(new Vector2i(), widget));
    }
}
