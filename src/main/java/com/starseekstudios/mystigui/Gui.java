package com.starseekstudios.mystigui;

import com.starseekstudios.mysticore.MystiCore;
import com.starseekstudios.mysticore.NBTUtils;
import com.starseekstudios.mysticore.interact.UsableItemManager;
import com.starseekstudios.mystigui.widget.FrameWidget;
import com.starseekstudios.mystigui.widget.ItemWidget;
import com.starseekstudios.mystigui.widget.Widget;
import com.starseekstudios.mystigui.widget.WidgetSlot;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.starseekstudios.mysticore.interact.UsableItemManager.UsableItem;

public class Gui extends FrameWidget {

    public static Optional<GuiHolder> getHolder(Inventory inventory) {
        return Optional.ofNullable(inventory.getHolder()).filter(GuiHolder.class::isInstance).map(GuiHolder.class::cast);
    }

    public static BukkitTask close(Plugin plugin, HumanEntity humanEntity) {
        return Bukkit.getScheduler().runTaskLater(plugin, () -> humanEntity.closeInventory(), 1);
    }

    public static BukkitTask close(HumanEntity humanEntity) {
        return close(MystiGui.getInstance(), humanEntity);
    }

    @Deprecated
    public static BukkitTask delayClose(Plugin plugin, HumanEntity humanEntity) {
        return close(plugin, humanEntity);
    }

    public static final NamespacedKey guiItemKey = NamespacedKey.fromString("gui-item", MystiGui.getInstance());

    private static ItemStack setAsGuiItem(ItemStack itemStack) {
        itemStack.editMeta(itemMeta -> {
            NBTUtils.setBool(guiItemKey, itemMeta);
        });
        return itemStack;
    }

    public static boolean checkIsGuiItem(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        return NBTUtils.getBool(guiItemKey, itemStack.getItemMeta());
    }

    public static final int INVENTORY_WIDTH = 9;

    private final int numberOfSlots;
    private final Component initialName;

    private Consumer<InventoryClickEvent> miscellaneousClickAction;

    private Consumer<InventoryCloseEvent> closeAction;

    public Gui(Component initialName, int rows) {
        super(new Vector2i(INVENTORY_WIDTH, rows));

        this.initialName = initialName;
        this.numberOfSlots = rows * INVENTORY_WIDTH;
    }

    public void setMiscellaneousClickAction(Consumer<InventoryClickEvent> clickAction) {
        this.miscellaneousClickAction = clickAction;
    }

    protected Optional<Consumer<InventoryClickEvent>> getMiscellaneousClickAction() {
        return Optional.ofNullable(miscellaneousClickAction);
    }

    public void setCloseAction(Consumer<InventoryCloseEvent> closeAction) {
        this.closeAction = closeAction;
    }

    protected Optional<Consumer<InventoryCloseEvent>> getCloseAction() {
        return Optional.ofNullable(closeAction);
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
        private final Map<Integer, Map<Vector2i, Widget>> guiWidgetSlots = new HashMap<>();
        private final Map<Key, Widget> labeledWidgets = new HashMap<>();

        private final Map<UUID, Consumer<InventoryClickEvent>> clickActions = new HashMap<>();

        private GuiHolder() {
            cacheWidgets();
            this.inventory = Bukkit.createInventory(this, numberOfSlots, initialName);
            reloadInventory();
        }

        public Optional<Vector2i> getWidgetPosition(Widget widget) {
            for (int layer = guiWidgets.size() - 1; layer >= 0; layer--) {
                var layerWidgets = guiWidgets.get(layer);
                for (Map.Entry<Vector2i, Widget> widgetEntry : layerWidgets.entrySet()) {
                    if (widgetEntry.getValue().equals(widget)) {
                        return Optional.of(widgetEntry.getKey());
                    }
                }
            }
            return Optional.empty();
        }

        public Optional<Widget> getLabeledWidget(Key key) {
            return Optional.ofNullable(labeledWidgets.get(key));
        }

        public <T extends Widget> Optional<? extends T> getLabeledWidget(Class<T> widgetClass, Key key) {
            Optional<? extends Widget> labeledWidget = getLabeledWidget(key);
            if (labeledWidget.isEmpty()) {
                return Optional.empty();
            }

            Widget widget = labeledWidget.get();

            if (widgetClass.isInstance(widget)) {
                T classWidget = (T) widget;
                return Optional.of(classWidget);
            }

            return Optional.empty();
        }

        public void setName(Component name) {
            List<HumanEntity> viewers = this.inventory.getViewers();
            this.inventory = Bukkit.createInventory(this, numberOfSlots, name);
            reloadInventory();
            viewers.forEach(humanEntity -> humanEntity.openInventory(this.inventory));
        }

        public Optional<WidgetSlot> getWidgetForPosition(Vector2i vector2i) {
            for (int layer = guiWidgetSlots.size() - 1; layer >= 0; layer--) {
                var layerSlots = guiWidgetSlots.get(layer);
                if (layerSlots.containsKey(vector2i)) {
                    Widget widget = layerSlots.get(vector2i);
                    if (widget != null) {
                        return Optional.of(new WidgetSlot(widget, vector2i));
                    }
                }
            }
            return Optional.empty();
        }

        private void cacheWidgets() {
            var layers = new ArrayList<>(widgets.entrySet());
            layers.sort(Comparator.comparingInt(Map.Entry::getKey));
            layers.forEach(layer -> {
                var layerWidgets = guiWidgets.getOrDefault(layer.getKey(), new HashMap<>());
                var layerWidgetSlots = guiWidgetSlots.getOrDefault(layer, new HashMap<>());
                Map<Vector2i, Widget> addSlots = new HashMap<>();
                layer.getValue().forEach((position, widget) -> {
                    Vector2i widgetSize = widget.getSize();
                    for (int x = position.x(); x < widgetSize.x() + position.x(); x++) {
                        for (int y = position.y(); y < widgetSize.y() + position.y(); y++) {
                            if (x < 0 || x >= getSize().x() || y < 0 || y >= getSize().y()) {
                                return;
                            }
                            if (layerWidgetSlots.containsKey(new Vector2i(x, y))) {
                                return;
                            }
                            addSlots.put(new Vector2i(x, y), widget);
                        }
                    }
                    Widget cacheWidget = widget.clone();
                    cacheWidget.setGuiHolder(this);
                    layerWidgets.put(position, cacheWidget);
                    cacheWidget.getLabel().ifPresent(key -> labeledWidgets.put(key, cacheWidget));
                });
                layerWidgetSlots.putAll(addSlots);
                guiWidgetSlots.put(layer.getKey(), layerWidgetSlots);
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
                items[vectorToIndex(vector2i)] = setAsGuiItem(itemWidget.getActionItem().clone());
            });
            this.inventory.setContents(items);
        }

        public void onClick(final InventoryClickEvent event) {
            event.setCancelled(true);
            ItemStack itemStack = event.getCurrentItem();
            Optional<UUID> actionId = ItemWidget.getActionId(itemStack);
            actionId.ifPresentOrElse(
                    uuid -> clickActions.get(uuid).accept(event),
                    () -> {
                        if (!checkIsGuiItem(itemStack)) {
                            getMiscellaneousClickAction().ifPresent(miscellaneousClickAction -> miscellaneousClickAction.accept(event));
                        }
                    }
            );
        }

        public void onClose(final InventoryCloseEvent event) {
            getCloseAction().ifPresent(closeAction -> closeAction.accept(event));
        }

        public Vector2i indexToVector(int index) {
            int width = getSize().x();
            int x = index % width;
            int y = index / width;
            return new Vector2i(x, y);
        }

        public int vectorToIndex(Vector2i vector) {
            int x = vector.x();
            int y = vector.y();
            return y * getSize().x() + x;
        }

        @Override
        public @NotNull Inventory getInventory() {
            return this.inventory;
        }
    }

}
