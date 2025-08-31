package dev.magyul.bukkit.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerEvent;

public abstract class CancellablePlayerEvent extends PlayerEvent implements Cancellable {
    private boolean cancelled;

    public CancellablePlayerEvent(Player who) {
        super(who);
        this.cancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
