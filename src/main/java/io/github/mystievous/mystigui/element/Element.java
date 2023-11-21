package io.github.mystievous.mystigui.element;

import io.github.mystievous.mysticore.NBTUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class Element {

    public static Element blank() {
        return new Element(new ItemStack(Material.AIR));
    }

    public static String UUID_TAG = "element-uuid";

    public static NamespacedKey UUID_KEY(Plugin plugin) {
        return NamespacedKey.fromString(UUID_TAG, plugin);
    }

    private final ItemStack item;

    public Element(ItemStack item) {
        this.item = item;
    }

    public void setUUID(Plugin plugin, UUID uuid) {
        NamespacedKey key = UUID_KEY(plugin);
        NBTUtils.applyToItemMeta(item, itemMeta -> NBTUtils.setUUID(key, itemMeta, uuid));
    }

    public ItemStack getItem() {
        return item;
    }

    public boolean isAir() {
        return item.getType().isAir();
    }

}
