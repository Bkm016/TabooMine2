package me.skymc.taboomine2.event;

import org.bukkit.block.Block;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;

public class BlockRestoreEvent extends BlockEvent {

    private static final HandlerList handlers = new HandlerList();

    private String type;

    public BlockRestoreEvent(Block theBlock, String type) {
        super(theBlock);
        this.type = type;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public String getType() {
        return type;
    }
}
