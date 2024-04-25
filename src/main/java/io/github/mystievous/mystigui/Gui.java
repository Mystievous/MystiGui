package io.github.mystievous.mystigui;

import io.github.mystievous.mystigui.widget.Widget;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import java.util.*;

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

    private Map<Vector2i, ItemStack> render(Map<Vector2i, Widget> widgets) {
        Map<Vector2i, ItemStack> renderedItems = new HashMap<>();
        for (Map.Entry<Vector2i, Widget> widgetEntry : widgets.entrySet()) {
            Vector2i widgetPosition = widgetEntry.getKey();
            Widget widget = widgetEntry.getValue();

            Map<Vector2i, ItemStack> items = widget.render();

            for (Map.Entry<Vector2i, ItemStack> itemEntry : items.entrySet()) {
                Vector2i itemPosition = itemEntry.getKey();
                ItemStack itemValue = itemEntry.getValue();

                Vector2i pos = new Vector2i(widgetPosition).add(itemPosition);

                renderedItems.put(pos, itemValue);
            }

        }
        return renderedItems;
    }

    @Override
    public Map<Vector2i, ItemStack> render() {
        return render(widgets);
    }

    public GuiHolder renderInventory() {
        return new GuiHolder();
    }

    public class GuiHolder implements InventoryHolder {

        private final Inventory inventory;

        private final Map<Vector2i, Widget> guiWidgets;

        private GuiHolder() {
            guiWidgets = new HashMap<>();
            cacheWidgets();
            this.inventory = Bukkit.createInventory(this, numberOfSlots, name);
            loadInventory();
        }

        private void cacheWidgets() {
            widgets.forEach((vector2i, widget) -> {
                guiWidgets.put(vector2i, widget.clone());
            });
        }

        private void loadInventory() {
            ItemStack[] items = new ItemStack[inventory.getSize()];
            render(guiWidgets).forEach((vector2i, itemStack) -> {
                items[vectorToIndex(vector2i)] = itemStack.clone();
            });
            this.inventory.setContents(items);
        }

        @Override
        public @NotNull Inventory getInventory() {
            return this.inventory;
        }
    }

}
