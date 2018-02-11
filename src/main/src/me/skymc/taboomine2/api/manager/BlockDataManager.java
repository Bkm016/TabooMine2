package me.skymc.taboomine2.api.manager;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import lombok.Getter;
import me.skymc.taboolib.inventory.ItemUtils;
import me.skymc.taboolib.message.MsgUtils;
import me.skymc.taboolib.other.DateUtils;
import me.skymc.taboomine2.api.MineAPI;
import me.skymc.taboomine2.event.BlockMarkEvent;
import me.skymc.taboomine2.event.BlockUnmarkEvent;
import me.skymc.taboomine2.utils.LocationUtils;

/**
 * @author sky
 * @since 2018��2��11�� ����2:42:57
 */
public class BlockDataManager {
	
	@Getter
	private MineAPI API;
	
	/**
	 * ���췽��
	 */
	public BlockDataManager(MineAPI api) {
		this.API = api;
	}
	
	/**
	 * ��ȡ���б���ǵķ���
	 * 
	 * @return {@link Set}
	 */
	public Set<String> getBlocks() {
		return API.getBlockData().getConfigurationSection("").getKeys(false);
	}
	
	/**
	 * ��ӱ�Ƿ���
	 * 
	 * @param block ����
	 * @param type ����
	 */
	public void addBlock(Block block, String type) {
		API.getBlockData().set(LocationUtils.asString(block.getLocation()) + ".type", type);
		Bukkit.getPluginManager().callEvent(new BlockMarkEvent(block, type));
	}
	
	/**
	 * ȡ����Ƿ���
	 * 
	 * @param block ����
	 */
	public void delBlock(Block block) {
		API.getBlockData().set(LocationUtils.asString(block.getLocation()), null);
		Bukkit.getPluginManager().callEvent(new BlockUnmarkEvent(block));
	}
	
	/**
	 * ��������δ���ƻ��ķ���
	 */
	public void resetAllBlock() {
		for (String preLocation : getBlocks()) {
			resetBlock(preLocation);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void resetBlock(String preLocation) {
		// ��ȡ����
		Block block = LocationUtils.asLocation(preLocation).getBlock();
		// ��ȡ����
		ConfigurationSection section = API.getBlockSection(API.getBlockData().getString(preLocation + ".type"));
		// ��Դ�ѱ�ɾ��
		if (section == null) {
			delBlock(block);
			MsgUtils.send("���� &f" + preLocation + " &7����Դ���ò�����, ��ɾ������Դ��");
		}
		// ���û���ƻ���¼
		else if (!API.getBreakData().contains(preLocation + ".date")) {
			try {
				// ���ò���
				block.setType(ItemUtils.asMaterial(section.getString("before").split(":")[0]));
				block.setData(Byte.valueOf(section.getString("before").split(":")[1]));
			}
			catch (Exception e) {
				MsgUtils.warn("�����ʼ������: &4" + e.getMessage());
			}
		}
	}
}
