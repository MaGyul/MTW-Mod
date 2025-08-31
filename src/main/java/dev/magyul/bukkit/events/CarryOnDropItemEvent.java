package dev.magyul.bukkit.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CarryOnDropItemEvent extends CancellablePlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final ItemStack itemStack;

    public CarryOnDropItemEvent(@NotNull Player who, @NotNull ItemStack itemStack) {
        super(who);
        this.itemStack = itemStack;
    }

    public @NotNull ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
