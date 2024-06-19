package com.starseekstudios.mystigui.widget;

import com.starseekstudios.mystigui.Gui;
import com.starseekstudios.mystigui.MystiGui;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.*;

public class FrameWidget extends Widget {

    protected Map<Integer, Map<Vector2i, Widget>> widgets = new HashMap<>();
    protected Map<Key, Widget> labeledWidgets = new HashMap<>();
    protected Map<Integer, Map<Vector2i, Widget>> widgetSlots = new HashMap<>();

    public FrameWidget(Vector2i size) {
        super();
        this.setSize(size);
        addOnReload(widget -> {
            ((FrameWidget) widget).widgets.values().forEach((layers) -> layers.values().forEach(Widget::onReload));
        });
    }

    @Override
    public Optional<WidgetSlot> getWidgetForPosition(Vector2i position) {
        if (position.x() >= getSize().x() || position.y() >= getSize().y()) {
            return Optional.empty();
        }

        for (int layer = widgetSlots.size() - 1; layer >= 0; layer--) {
            var layerSlots = widgetSlots.get(layer);
            if (layerSlots.containsKey(position)) {
                Widget widget = layerSlots.get(position);
                var layerWidgets = widgets.get(layer);
                Optional<Map.Entry<Vector2i, Widget>> selectedWidget = layerWidgets.entrySet().stream().filter(vector2iWidgetEntry -> vector2iWidgetEntry.getValue().equals(widget)).findFirst();
                if (selectedWidget.isPresent()) {
                    return selectedWidget.map(widgetEntry -> new WidgetSlot(widgetEntry.getValue(), new Vector2i(position).sub(widgetEntry.getKey())));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<? extends Widget> getLabeledWidget(Key key) {
        return Optional.ofNullable(labeledWidgets.get(key));
    }

    @Override
    public void setGuiHolder(Gui.@Nullable GuiHolder guiHolder) {
        super.setGuiHolder(guiHolder);
        widgets.values().forEach((layers) -> layers.values().forEach(widget -> widget.setGuiHolder(guiHolder)));
    }

    public void putWidget(int layer, Vector2i position, Widget widget) {
        Vector2i widgetSize = widget.getSize();
        Map<Vector2i, Widget> addSlots = new HashMap<>();
        var layerWidgetSlots = widgetSlots.getOrDefault(layer, new HashMap<>());
        for (int x = position.x(); x < widgetSize.x() + position.x(); x++) {
            for (int y = position.y(); y < widgetSize.y() + position.y(); y++) {
                if (x < 0 || x >= getSize().x() || y < 0 || y >= getSize().y()) {
                    MystiGui.pluginLogger().warn(String.format("Tried to place a widget outside of frame bounds.\nWidget: %s\nPosition: %s\nLabel: %s", widget.getClass().getName(), new Vector2i(x, y), widget.getLabel().orElse(null)));
                    return;
                }
                if (layerWidgetSlots.containsKey(new Vector2i(x, y))) {
                    MystiGui.pluginLogger().warn(String.format("Tried to place widget overlapping an already occupied slot in the layer.\nWidget: %s\nPosition: %s\nLabel: %s", widget, new Vector2i(x, y), widget.getLabel().orElse(null)));
                    return;
                }
                addSlots.put(new Vector2i(x, y), widget);
            }
        }
        var layerWidgets = widgets.getOrDefault(layer, new HashMap<>());
        layerWidgets.put(position, widget);
        widget.getLabel().ifPresent(key -> labeledWidgets.put(key, widget));
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

    @Override
    public Map<Vector2i, ItemWidget> render() {
        return render(widgets);
    }

    protected Map<Vector2i, ItemWidget> render(Map<Integer, Map<Vector2i, Widget>> widgets) {
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
    public FrameWidget clone() {
        FrameWidget widget = (FrameWidget) super.clone();
        widget.widgets = new HashMap<>();
        widget.widgetSlots = new HashMap<>();
        widget.labeledWidgets = new HashMap<>();

        Map<Integer, Map<Vector2i, Widget>> widgets = new HashMap<>();
        this.widgets.forEach((integer, vector2iWidgetMap) -> {
            Map<Vector2i, Widget> layer = new HashMap<>();
            vector2iWidgetMap.forEach((vector2i, widget1) -> {
                layer.put(new Vector2i(vector2i), widget1.clone());
            });
            widgets.put(integer, layer);
        });
        widget.putAll(widgets);

        return widget;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FrameWidget that = (FrameWidget) o;
        return Objects.equals(widgets, that.widgets) && Objects.equals(labeledWidgets, that.labeledWidgets) && Objects.equals(widgetSlots, that.widgetSlots);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), widgets, labeledWidgets, widgetSlots);
    }

}
