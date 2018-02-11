package me.skymc.taboomine2.api;

import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import me.skymc.advanced.item.durability.Durability;
import me.skymc.advanced.item.durability.api.DurabilityAPI;
import me.skymc.taboocode.TabooCodeItem;
import me.skymc.taboolib.fileutils.ConfigUtils;
import me.skymc.taboolib.inventory.ItemUtils;
import me.skymc.taboolib.message.MsgUtils;
import me.skymc.taboolib.playerdata.DataUtils;
import me.skymc.taboomine2.TabooMine;
import me.skymc.taboomine2.api.manager.BlockDataManager;
import me.skymc.taboomine2.api.manager.BreakDataManager;
import me.skymc.taboomine2.result.BreakResult;
import me.skymc.taboomine2.utils.LocationUtils;

/**
 * @author sky
 * @since 2018��2��11�� ����2:30:14
 */
public class MineAPI {
	
	@Getter
	private FileConfiguration blockData;
	
	@Getter
	private FileConfiguration breakData;
	
	@Getter
	private FileConfiguration blockConfig;
	
	@Getter
	private BlockDataManager blockDataManager;
	
	@Getter
	private BreakDataManager breakDataManager;
	
	/**
	 * ���췽��
	 */
	public MineAPI() {
		reloadConfig();
		// ��������
		blockDataManager = new BlockDataManager(this);
		breakDataManager = new BreakDataManager(this);
	}
	
	/**
	 * ���������ļ�
	 */
	public void reloadConfig() {
		blockData = DataUtils.addPluginData("data-block", TabooMine.getInst());
		breakData = DataUtils.addPluginData("data-break", TabooMine.getInst());
		blockConfig = ConfigUtils.saveDefaultConfig(TabooMine.getInst(), "blocks.yml");
	}
	
	/**
	 * �Ƿ������ƻ�����
	 * 
	 * @param item ��Ʒ
	 * @return
	 */
	public BreakResult canBreakBlock(String type, ItemStack item) {
		ConfigurationSection section = getBlockSection(type);
		// û������
		if (!section.contains("allowitems") || section.getStringList("allowitems").size() == 0) {
			return BreakResult.ALLOW;
		}
		// û������
		if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) {
			return BreakResult.DENY;
		}
		// ��ȡè��
		String lore = item.getItemMeta().getLore().toString();
		// �������
		for (String key : section.getStringList("allowitems")) {
			if (lore.contains(key)) {
				// �;ò���
				if (Durability.getDurability(item)[0] < section.getInt("durability")) {
					return BreakResult.DURABILITY;
				}
				else {
					return BreakResult.ALLOW;
				}
			}
		}
		return BreakResult.DENY;
	}
	
	/**
	 * ��ȡ��������
	 * 
	 * @param block ����
	 * @param bypass �Ƿ����������ж�
	 * @return
	 */
	public String getBlockType(Block block, boolean bypass) {
		// ��ȡ����
		String type = blockData.getString(LocationUtils.asString(block.getLocation()) + ".type");
		// ���û�����ݻ����������ж�
		if (bypass || type == null) {
			return type;
		}
		try {
			// ��ȡ����
			ConfigurationSection section = getBlockSection(type);
			// �жϲ���
			if (!block.getType().toString().equals(section.getString("before").split(":")[0]) || block.getData() != Byte.valueOf(section.getString("before").split(":")[1])) {
				return null;
			}
		}
		catch (Exception e) {
			MsgUtils.warn("�����жϳ���: &4" + e.getMessage());
		}
		return type;
	}
	
	/**
	 * ��ȡ��������
	 * 
	 * @param name ����
	 * @return {@link ConfigurationSection}
	 */
	public ConfigurationSection getBlockSection(String name) {
		return blockConfig.getConfigurationSection(name);
	}
	
	/**
	 * �������ƻ�ȡ��Ʒ
	 * 
	 * @param name ����
	 * @return {@link ItemStack}
	 */
	public ItemStack getItemStack(String name) {
		// ���ɷ���
		if (TabooMine.getInst().getConfig().getString("Settings.source").equalsIgnoreCase("taboocode")) {
			return TabooCodeItem.getItem(name, false);
		}
		// �������
		if (TabooMine.getInst().getConfig().getString("Settings.source").equalsIgnoreCase("taboolib")) {
			return ItemUtils.getCacheItem(name);
		}
		// ����
		MsgUtils.warn("�������Ʒ�ṩ���: &4" + TabooMine.getInst().getConfig().getString("Settings.source"), TabooMine.getInst());
		return null;
	}
}
