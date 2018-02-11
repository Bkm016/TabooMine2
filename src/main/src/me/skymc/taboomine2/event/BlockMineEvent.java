package me.skymc.taboomine2.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockEvent;

import lombok.Getter;

public class BlockMineEvent extends BlockEvent {
	
	private static final HandlerList handlers = new HandlerList();
	
	@Getter
	private String type;
	
	public BlockMineEvent(Block theBlock, String type) {
		super(theBlock);
		this.type = type;
	}

	public HandlerList getHandlers() {
		return handlers;
	}
  
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
