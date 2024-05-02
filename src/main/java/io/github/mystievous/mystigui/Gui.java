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
import java.util.function.Supplier;

public class Gui extends Widget {

    public static final int INVENTORY_WIDTH = 9;

    private final Map<Integer, Map<Vector2i, Widget>> widgets;
    private final Map<Integer, Map<Vector2i, Widget>> widgetSlots;
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

    public static UsableItem createShortcutItem(String tag, ItemStack template, Supplier<Gui> getGui) {
        ItemStack item = NBTUtils.setNoUse(template.clone());
        return UsableItemManager.createItem(tag, item, playerInteractEvent -> {
            playerInteractEvent.getPlayer().openInventory(getGui.get().renderInventory());
        });
    }

    public void putWidget(int layer, Vector2i position, Widget widget) {
        Vector2i widgetSize = widget.getSize();
        Map<Vector2i, Widget> addSlots = new HashMap<>();
        var layerWidgetSlots = widgetSlots.getOrDefault(layer, new HashMap<>());
        for (int x = position.x(); x < widgetSize.x() + position.x(); x++) {
            for (int y = position.y(); y < widgetSize.y() + position.y(); y++) {
                if (x < 0 || x >= getSize().x() || y < 0 || y >= getSize().y()) {
                    MystiGui.pluginLogger().warn("Tried to place widget outside of gui bounds.");
                    return;
                }
                if (layerWidgetSlots.containsKey(new Vector2i(x, y))) {
                    MystiGui.pluginLogger().warn("Tried to place widget overlapping an already occupied slot in the layer.");
                    return;
                }
                addSlots.put(new Vector2i(x, y), widget);
            }
        }
        var layerWidgets = widgets.getOrDefault(layer, new HashMap<>());
        layerWidgets.put(position, widget);
        widgets.put(layer, layerWidgets);

        layerWidgetSlots.putAll(addSlots);
        widgetSlots.put(layer, layerWidgetSlots);
    }

    public void putWidget(Vector2i position, Widget widget) {
        putWidget(0, position, widget);
    }

    public void putLayer(int layer, Map<Vector2i, Widget> widgetMap) {
        widgetMap.forEach((widgetPos, widget) -> putWidget(layer, widgetPos, widget));
    }

    public void putLayer(Map<Vector2i, Widget> widgetMap) {
        putLayer(0, widgetMap);
    }

    public void putAll(Map<Integer, Map<Vector2i, Widget>> layerMap) {
        layerMap.forEach(this::putLayer);
    }

    private Map<Vector2i, ItemWidget> render(Map<Integer, Map<Vector2i, Widget>> widgets) {
        Map<Vector2i, ItemWidget> renderedItems = new HashMap<>();
        var layers = new ArrayList<>(widgets.entrySet());
        layers.sort(Comparator.comparingInt(Map.Entry::getKey));
        layers.forEach(layer -> {
            for (Map.Entry<Vector2i, Widget> widgetEntry : layer.getValue().entrySet()) {
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
        });
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

        private final Map<Integer, Map<Vector2i, Widget>> guiWidgets;

        private final Map<UUID, Consumer<InventoryClickEvent>> clickActions;

        private GuiHolder() {
            guiWidgets = new HashMap<>();
            clickActions = new HashMap<>();
            cacheWidgets();
            this.inventory = Bukkit.createInventory(this, numberOfSlots, name);
            loadInventory();
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
                });
                guiWidgets.put(layer.getKey(), layerWidgets);
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
