package io.github.mystievous.mystigui.page;

import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.mystigui.element.Clickable;
import io.github.mystievous.mystigui.element.Element;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class Gui implements Listener {

    private final Plugin plugin;
    private final Component inventoryTitle;

    private Inventory firstInventory;
    private final List<Inventory> inventories;

    private final HashMap<UUID, Element> elements;
    private boolean eventsRegistered;

    /**
     * Initializes a new GUI
     *
     * @param name          title of the GUI firstInventory
     * @param textureAdjust pixel amount to adjust the character that the graphic is rendered by
     * @param customTexture character that renders the graphic
     * @param titleAdjust   pixel amount to adjust the title, to shift it back over the graphic
     */
    public Gui(Plugin plugin, Component name, int textureAdjust, Component customTexture, int titleAdjust) {
        this.plugin = plugin;
        TextComponent.Builder titleBuilder = Component.text();
        if (textureAdjust != 0) {
            titleBuilder.append(TextUtil.space(textureAdjust));
        }
        if (customTexture != null) {
            titleBuilder.append(customTexture.color(NamedTextColor.WHITE));
        }
        if (titleAdjust != 0) {
            titleBuilder.append(TextUtil.space(titleAdjust));
        }
        titleBuilder.append(name);
        inventoryTitle = titleBuilder.build();
        inventories = new ArrayList<>();
        elements = new HashMap<>();
        eventsRegistered = false;
    }

    /**
     * Creates a simple gui, with only a title
     *
     * @param name the title of the firstInventory
     */
    public Gui(Plugin plugin, Component name) {
        this.plugin = plugin;
        inventoryTitle = name;
        inventories = new ArrayList<>();
        elements = new HashMap<>();
        eventsRegistered = false;
    }

    protected Plugin getPlugin() {
        return plugin;
    }

    public Component getInventoryTitle() {
        return inventoryTitle;
    }

    public Inventory getFirstInventory() {
        return firstInventory;
    }

    public void registerInventory(Inventory inventory) {
        inventories.add(inventory);
    }

    public void setFirstInventory(Inventory firstInventory) {
        this.firstInventory = firstInventory;
    }

    /**
     * Registers element with the listeners
     *
     * @param element Element to add
     */
    public void registerElement(Element element) {
        UUID uuid = UUID.randomUUID();
        element.setUUID(plugin, uuid);
        elements.put(uuid, element);
    }

    /**
     * Loads the elements into the firstInventory
     */
    public abstract void loadGui();

    public boolean matchInventory(Inventory inventory) {
        return inventories.contains(inventory);
    }

    public int numViewers() {
        int count = 0;
        for (Inventory inventory : inventories) {
            count += inventory.getViewers().size();
        }
        return count;
    }

    /**
     * Opens the firstInventory for the specified humanEntity
     *
     * @param humanEntity the entity to open the firstInventory for
     */
    public void openInventory(HumanEntity humanEntity) {
        if (!eventsRegistered) {
            registerEvents();
        }
        humanEntity.openInventory(firstInventory);
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        if (!matchInventory(inventory))
            return;

        if (numViewers() == 1) {
            unregisterEvents();
        }
    }

    /**
     * Handles clicks on the firstInventory, cancelling the event to prevent users taking items, and running the action if it is a button.
     *
     * @param event object representing the event
     */
    @EventHandler
    public void onClick(final InventoryClickEvent event) {
        if (event.isCancelled())
            return;
        if (!matchInventory(event.getInventory()))
            return;
        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType().isAir())
            return;

        if (!NBTUtils.hasUniqueID(plugin, item))
            return;
        UUID itemId = NBTUtils.getUniqueID(plugin, item);
        if (!elements.containsKey(itemId))
            return;

        if (!(event.getWhoClicked() instanceof Player player))
            return;

        Element element = elements.get(itemId);
        if (element instanceof Clickable clickElement) {
            clickElement.use(player);
        }
    }

    public void unregisterEvents() {
        HandlerList.unregisterAll(this);
        eventsRegistered = false;
//        Bukkit.getLogger().info("Events unregistered: " + PlainTextComponentSerializer.plainText().serialize(inventoryTitle));
    }

    public void registerEvents() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        eventsRegistered = true;
//        Bukkit.getLogger().info("Events registered: " + PlainTextComponentSerializer.plainText().serialize(inventoryTitle));
    }

}
