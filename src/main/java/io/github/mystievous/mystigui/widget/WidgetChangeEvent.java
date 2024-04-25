package io.github.mystievous.mystigui.widget;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WidgetChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    private final Widget widget;
    private boolean cancelled;

    public WidgetChangeEvent(Widget widget) {
        this.widget = widget;
        cancelled = false;
    }

    public Widget getWidget() {
        return widget;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
