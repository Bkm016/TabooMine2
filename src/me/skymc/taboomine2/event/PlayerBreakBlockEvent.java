package me.skymc.taboomine2.event;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerBreakBlockEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean isCancelled;

    private String type;
    private Block block;

    public PlayerBreakBlockEvent(Player player, Block block, String type) {
        super(player);
        this.block = block;
        this.type = type;
    }

    public PlayerBreakBlockEvent call() {
        Bukkit.getPluginManager().callEvent(this);
        return this;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public String getType() {
        return type;
    }

    public Block getBlock() {
        return block;
    }
}
