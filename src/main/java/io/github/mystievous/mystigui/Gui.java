package io.github.mystievous.mystigui;

import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.mysticore.interact.UsableItemManager;
import io.github.mystievous.mystigui.widget.ItemWidget;
import io.github.mystievous.mystigui.widget.Widget;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import static io.github.mystievous.mysticore.interact.UsableItemManager.UsableItem;

import java.util.*;
import java.util.function.Consumer;

public class Gui extends Widget {

    public static final int INVENTORY_WIDTH = 9;

    private final Map<Vector2i, Widget> widgets;
    private final Map<Vector2i, Widget> widgetSlots;
    private final int numberOfSlots;
    private final Component name;

    public Gui(Component name, int rows) {
        super();
        this.widgets = new HashMap<>();
        this.widgetSlots = new HashMap<>();

        this.name = name;
        this.numberOfSlots = rows * INVENTORY_WIDTH;
        this.setSize(new Vector2i(INVENTORY_WIDTH, rows));
    }

    public UsableItem createShortcutItem(String tag, ItemStack template) {
        ItemStack item = NBTUtils.setNoUse(template.clone());
        return UsableItemManager.createItem(tag, item, playerInteractEvent -> {
            playerInteractEvent.getPlayer().openInventory(renderInventory());
        });
    }

    public void putWidget(Vector2i position, Widget widget) {
        Vector2i widgetSize = widget.getSize();
        Map<Vector2i, Widget> addSlots = new HashMap<>();
        for (int x = position.x(); x < widgetSize.x() + position.x(); x++) {
            for (int y = position.y(); y < widgetSize.y() + position.y(); y++) {
                if (widgetSlots.containsKey(new Vector2i(x, y))) {
                    MystiGui.pluginLogger().warn("Tried to place widget overlapping already occupied slot.");
                    return;
                }
                addSlots.put(new Vector2i(x, y), widget);
            }
        }
        widgets.put(position, widget);
        widgetSlots.putAll(addSlots);
    }

    private Map<Vector2i, ItemWidget> render(Map<Vector2i, Widget> widgets) {
        Map<Vector2i, ItemWidget> renderedItems = new HashMap<>();
        for (Map.Entry<Vector2i, Widget> widgetEntry : widgets.entrySet()) {
            Vector2i widgetPosition = widgetEntry.getKey();
            Widget widget = widgetEntry.getValue();

            Map<Vector2i, ItemWidget> items = widget.render();

            for (Map.Entry<Vector2i, ItemWidget> itemEntry : items.entrySet()) {
                Vector2i itemPosition = itemEntry.getKey();
                ItemWidget itemValue = itemEntry.getValue();

                Vector2i pos = new Vector2i(widgetPosition).add(itemPosition);

                renderedItems.put(pos, itemValue);
            }

        }
        return renderedItems;
    }

    @Override
    public Map<Vector2i, ItemWidget> render() {
        return render(widgets);
    }

    public Inventory renderInventory() {
        return new GuiHolder().getInventory();
    }

    public class GuiHolder implements InventoryHolder {

        private final Inventory inventory;

        private final Map<Vector2i, Widget> guiWidgets;

        private final Map<UUID, Consumer<InventoryClickEvent>> clickActions;

        private GuiHolder() {
            guiWidgets = new HashMap<>();
            clickActions = new HashMap<>();
            cacheWidgets();
            this.inventory = Bukkit.createInventory(this, numberOfSlots, name);
            loadInventory();
        }

        private void cacheWidgets() {
            widgets.forEach((vector2i, widget) -> {
                Widget cacheWidget = widget.clone();
                cacheWidget.setGuiHolder(this);
                guiWidgets.put(vector2i, cacheWidget);
            });
        }

        public void loadInventory() {
            ItemStack[] items = new ItemStack[inventory.getSize()];
            render(guiWidgets).forEach((vector2i, itemWidget) -> {
                ItemWidget.ClickAction clickAction = itemWidget.getClickAction();
                if (clickAction != null) {
                    clickActions.put(clickAction.id(), clickAction.onClick());
                }
                items[vectorToIndex(vector2i)] = itemWidget.getItem().clone();
            });
            this.inventory.setContents(items);
        }

        public void onClick(final InventoryClickEvent event) {
            if (event.isCancelled()) return;

            event.setCancelled(true);
            ItemStack itemStack = event.getCurrentItem();
            if (itemStack == null) return;
            UUID actionId = ItemWidget.getActionId(itemStack);
            if (actionId != null && clickActions.containsKey(actionId)) {
                clickActions.get(actionId).accept(event);
            }
        }

        @Override
        public @NotNull Inventory getInventory() {
            return this.inventory;
        }
    }

}
