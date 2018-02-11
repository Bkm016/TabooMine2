package me.skymc.taboomine2.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockEvent;

import lombok.Getter;

public class BlockUnmarkEvent extends BlockEvent {
	
	private static final HandlerList handlers = new HandlerList();
	
	public BlockUnmarkEvent(Block theBlock) {
		super(theBlock);
	}

	public HandlerList getHandlers() {
		return handlers;
	}
  
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
