package io.github.mystievous.mystigui.element;

import io.github.mystievous.mysticore.NBTUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class Element {

    public static Element blank() {
        return new Element(new ItemStack(Material.AIR));
    }

    private UUID uuid;
    private ItemStack item;

    public Element(ItemStack item) {
        this.item = item;
    }

    public void setUUID(Plugin plugin, UUID uuid) {
        this.item = NBTUtils.setUniqueID(plugin, item, uuid);
        this.uuid = uuid;
    }

    public ItemStack getItem() {
        return item;
    }

    public boolean isAir() {
        return item.getType().isAir();
    }

}
