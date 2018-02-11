package me.skymc.taboomine2.event;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.player.PlayerEvent;

import lombok.Getter;

public class PlayerBreakBlockEvent extends PlayerEvent implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private boolean isCancelled;
	
	@Getter
	private String type;
	@Getter
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

	public HandlerList getHandlers() {
		return handlers;
	}
  
	public static HandlerList getHandlerList() {
		return handlers;
	}

	/* (non-Javadoc)
	 * @see org.bukkit.event.Cancellable#isCancelled()
	 */
	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	/* (non-Javadoc)
	 * @see org.bukkit.event.Cancellable#setCancelled(boolean)
	 */
	@Override
	public void setCancelled(boolean arg0) {
		isCancelled = arg0;
	}
}
