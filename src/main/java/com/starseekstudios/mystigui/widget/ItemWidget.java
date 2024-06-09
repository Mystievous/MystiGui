package com.starseekstudios.mystigui.widget;

import com.starseekstudios.mysticore.NBTUtils;
import com.starseekstudios.mystigui.MystiGui;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector2i;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class ItemWidget extends Widget {

    public record ClickAction(UUID id, Consumer<InventoryClickEvent> onClick) {
    }

    public static final NamespacedKey ACTION_KEY = new NamespacedKey(MystiGui.getInstance(), "gui-action-id");

    public static void setActionId(ItemStack item, UUID id) {
        item.editMeta(itemMeta -> {
            NBTUtils.setUUID(ACTION_KEY, itemMeta, id);
        });
    }

    public static Optional<UUID> getActionId(ItemStack itemStack) {
        if (itemStack == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(NBTUtils.getUUID(ACTION_KEY, itemStack.getItemMeta()));
    }

    private ItemStack item;

    @Nullable
    private ClickAction clickAction;

    public ItemWidget(ItemStack widgetItem) {
        this.item = widgetItem;
    }

    public void setClickAction(Consumer<InventoryClickEvent> onClick) {
        clickAction = new ClickAction(UUID.randomUUID(), onClick);
    }

    public Optional<ClickAction> getClickAction() {
        return Optional.ofNullable(clickAction);
    }

    public ItemStack getItem() {
        return item;
    }

    public ItemStack getActionItem() {
        ItemStack itemStack = getItem();
        getClickAction().ifPresent(clickAction1 -> setActionId(getItem(), clickAction1.id));
        return itemStack;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public void modifyItem(Consumer<ItemStack> consumer) {
        consumer.accept(item);
    }

    @Override
    public void setOnReload(Consumer<Widget> onReload) {
        super.setOnReload(onReload);
    }

    @Override
    public Map<Vector2i, ItemWidget> render() {
        Map<Vector2i, ItemWidget> output = new HashMap<>();
        output.put(new Vector2i(), this);
        return output;
    }

    @Override
    public ItemWidget clone() {
        ItemWidget widget = (ItemWidget) super.clone();

        widget.item = this.item.clone();
        widget.clickAction = this.clickAction;

        return widget;
    }


}
