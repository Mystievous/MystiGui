package io.github.mystievous.mystigui;

import io.github.mystievous.mysticore.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GuiUtil {
    /**
     * Creates an itemstack with the specified values
     * @param name Name of the item
     * @param material Material to make the item
     * @param customModelData Custom model ID of the texture
     * @return The item
     */
    public static @NotNull ItemStack formatItem(@Nullable Component name, @NotNull Material material, @Nullable Integer customModelData) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(TextUtil.noItalic(name));
        meta.setCustomModelData(customModelData);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Creates an itemstack with the specified values
     * @param name Name of the item
     * @param material Material to make the item
     * @param customModelData Custom model ID of the texture
     * @return The item
     */
    public static @NotNull ItemStack formatItem(String name, @NotNull Material material, @Nullable Integer customModelData) {
        return formatItem(Component.text(name), material, customModelData);
    }

    /**
     * Red left arrow item
     *
     * @return the Item
     */
    public static ItemStack backItem() {
        Component name = Component.text("Exit").decoration(TextDecoration.ITALIC, false);
        return formatItem(name, Material.REDSTONE_BLOCK, 1);
    }

    /**
     * Red X item
     *
     * @return the Item
     */
    public static ItemStack exitItem() {
        Component name = Component.text("Exit").decoration(TextDecoration.ITALIC, false);
        return formatItem(name, Material.REDSTONE_BLOCK, 2);
    }
}
