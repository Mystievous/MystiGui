package com.starseekstudios.mystigui;

import com.starseekstudios.mysticore.NBTUtils;
import com.starseekstudios.mysticore.interact.UsableItemManager;
import com.starseekstudios.mystigui.widget.FrameWidget;
import com.starseekstudios.mystigui.widget.ItemWidget;
import com.starseekstudios.mystigui.widget.Widget;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.starseekstudios.mysticore.interact.UsableItemManager.UsableItem;

public class Gui extends FrameWidget {

    public static final int INVENTORY_WIDTH = 9;

    private final int numberOfSlots;
    private final Component initialName;

    public Gui(Component initialName, int rows) {
        super(new Vector2i(INVENTORY_WIDTH, rows));

        this.initialName = initialName;
        this.numberOfSlots = rows * INVENTORY_WIDTH;
    }

    public static UsableItem createShortcutItem(String tag, ItemStack template, Supplier<Gui> getGui) {
        ItemStack item = NBTUtils.setNoUse(template.clone());
        return UsableItemManager.createItem(tag, item, playerInteractEvent -> {
            playerInteractEvent.getPlayer().openInventory(getGui.get().renderInventory());
        });
    }

    public Inventory renderInventory() {
        return new GuiHolder().getInventory();
    }

    public class GuiHolder implements InventoryHolder {

        private Inventory inventory;

        private final Map<Integer, Map<Vector2i, Widget>> guiWidgets = new HashMap<>();
        private final Map<Key, Widget> labeledWidgets = new HashMap<>();

        private final Map<UUID, Consumer<InventoryClickEvent>> clickActions = new HashMap<>();

        private GuiHolder() {
            cacheWidgets();
            this.inventory = Bukkit.createInventory(this, numberOfSlots, initialName);
            reloadInventory();
        }

        public Optional<Widget> getLabeledWidget(Key key) {
            return Optional.ofNullable(labeledWidgets.get(key));
        }

        public void setName(Component name) {
            List<HumanEntity> viewers = this.inventory.getViewers();
            this.inventory = Bukkit.createInventory(this, numberOfSlots, name);
            reloadInventory();
            viewers.forEach(humanEntity -> humanEntity.openInventory(this.inventory));
        }

        private void cacheWidgets() {
            var layers = new ArrayList<>(widgets.entrySet());
            layers.sort(Comparator.comparingInt(Map.Entry::getKey));
            layers.forEach(layer -> {
                var layerWidgets = guiWidgets.getOrDefault(layer.getKey(), new HashMap<>());
                layer.getValue().forEach((vector2i, widget) -> {
                    Widget cacheWidget = widget.clone();
                    cacheWidget.setGuiHolder(this);
                    layerWidgets.put(vector2i, cacheWidget);
                    cacheWidget.getLabel().ifPresent(key -> labeledWidgets.put(key, cacheWidget));
                });
                guiWidgets.put(layer.getKey(), layerWidgets);
            });
        }

        public void reloadInventory() {
            ItemStack[] items = new ItemStack[inventory.getSize()];
            guiWidgets.values().forEach(widgets -> widgets.values().forEach(Widget::onReload));
            render(guiWidgets).forEach((vector2i, itemWidget) -> {
                itemWidget.getClickAction().ifPresent(clickAction -> {
                    clickActions.put(clickAction.id(), clickAction.onClick());
                });
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
