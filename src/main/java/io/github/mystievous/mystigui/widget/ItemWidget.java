package io.github.mystievous.mystigui.widget;

import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.mystigui.MystiGui;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector2i;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
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

    @Nullable
    public static UUID getActionId(ItemStack itemStack) {
        return NBTUtils.getUUID(ACTION_KEY, itemStack.getItemMeta());
    }

    private ItemStack item;

    @Nullable
    private ClickAction clickAction;

    public ItemWidget(ItemStack widgetItem) {
        this.item = widgetItem;
    }

    public void setClickAction(Consumer<InventoryClickEvent> onClick) {
        clickAction = new ClickAction(UUID.randomUUID(), onClick);
        setActionId(item, clickAction.id);
    }

    @Nullable
    public ClickAction getClickAction() {
        return clickAction;
    }

    public ItemStack getItem() {
        return item;
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

        return widget;
    }
}
