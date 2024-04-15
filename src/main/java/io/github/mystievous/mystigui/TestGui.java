package io.github.mystievous.mystigui;

import io.github.mystievous.mysticore.NBTUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class TestGui implements InventoryHolder {

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 27);
        ItemStack item = new ItemStack(Material.STICK);
        ItemMeta meta = item.getItemMeta();
        NBTUtils.setBool(new NamespacedKey("mctc", "item"), meta);
        item.setItemMeta(meta);
        inventory.addItem(item);
        return inventory;
    }
}
