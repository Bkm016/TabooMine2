package me.skymc.taboomine2.listener;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.skymc.advanced.item.durability.Durability;
import me.skymc.taboolib.display.TitleUtils;
import me.skymc.taboolib.inventory.ItemUtils;
import me.skymc.taboolib.message.MsgUtils;
import me.skymc.taboolib.other.NumberUtils;
import me.skymc.taboolib.sound.SoundPack;
import me.skymc.taboomine2.TabooMine;
import me.skymc.taboomine2.event.PlayerBreakBlockEvent;
import me.skymc.taboomine2.result.BreakResult;

/**
 * @author sky
 * @since 2018��2��11�� ����4:00:27
 */
public class ListenerBlockBreak implements Listener {
	
	@SuppressWarnings("deprecation")
	@EventHandler (priority = EventPriority.LOWEST)
	public void onBreak(BlockBreakEvent e) {
		String type = TabooMine.getMineAPI().getBlockType(e.getBlock(), false);
		if (type == null) {
			return;
		}
		else {
			e.setCancelled(true);
			// ������ǿ���
			if (!e.getPlayer().getItemInHand().getType().equals(Material.AIR)) {
				// ˢ�±���
				e.getPlayer().updateInventory();
			}
		}
		
		// ��ȡ�ƻ����
		BreakResult result = TabooMine.getMineAPI().canBreakBlock(type, e.getPlayer().getInventory().getItemInMainHand());
		if (result == BreakResult.DENY) {
			// ���ű���
			TitleUtils.sendTitle(e.getPlayer(), 
					TabooMine.getInst().getConfig().getString("Settings.result.deny.title").replace("&", "��"), 
					TabooMine.getInst().getConfig().getString("Settings.result.deny.subtitle").replace("&", "��"), 
					10, 60, 10);
			// ������Ч
			new SoundPack(TabooMine.getInst().getConfig().getString("Settings.result.deny.sound")).play(e.getPlayer());
			return;
		}
		if (result == BreakResult.DURABILITY) {
			// ���ű���
			TitleUtils.sendTitle(e.getPlayer(), 
					TabooMine.getInst().getConfig().getString("Settings.result.durability.title").replace("&", "��"), 
					TabooMine.getInst().getConfig().getString("Settings.result.durability.subtitle").replace("&", "��"), 
					10, 60, 10);
			// ������Ч
			new SoundPack(TabooMine.getInst().getConfig().getString("Settings.result.durability.sound")).play(e.getPlayer());
			return;
		}
		
		PlayerBreakBlockEvent event = new PlayerBreakBlockEvent(e.getPlayer(), e.getBlock(), type).call();
		if (event.isCancelled()) {
			return;
		}
		
		// ��ȡ����
		ConfigurationSection section = TabooMine.getMineAPI().getBlockSection(type);
		
		// ��ӱ��
		TabooMine.getMineAPI().getBreakDataManager().addBlock(e.getBlock(), type);
		
		// ���ò���
		e.getBlock().setType(ItemUtils.asMaterial(section.getString("after").split(":")[0]));
		e.getBlock().setData(Byte.valueOf(section.getString("after").split(":")[1]));
		
		// �۳��;�
		if (section.contains("allowitems") || section.getStringList("allowitems").size() > 0) {
			Durability.setDurability(e.getPlayer(), e.getPlayer().getInventory().getItemInMainHand(), section.getInt("durability") * -1, true);
		}
		
		// ������Ʒ
		new BukkitRunnable() {
			
			@Override
			public void run() {
				// ������Ʒ
				for (String drop : section.getStringList("drops")) {
					try {
						// �жϼ���
						if (NumberUtils.getRand().nextDouble() <= Double.valueOf(drop.split("\\|")[0])) {
							// ��ȡ��Ʒ
							ItemStack item = TabooMine.getMineAPI().getItemStack(drop.split("\\|")[1]).clone();
							// ��������
							item.setAmount(NumberUtils.getInteger(drop.split("\\|")[2]));
							// ������Ʒ
							e.getBlock().getWorld().dropItem(section.getString("position").equalsIgnoreCase("block") ? e.getBlock().getLocation().add(0.5, 0, 0.5) : e.getPlayer().getLocation(), item);
						}
					}
					catch (Exception err) {
						MsgUtils.warn("������ִ���: &4" + err.getMessage());
					}
				}
			}
		}.runTask(TabooMine.getInst());
	}
}
