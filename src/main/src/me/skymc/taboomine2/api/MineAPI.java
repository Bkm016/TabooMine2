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
 * @since 2018年2月11日 下午2:30:14
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
	 * 构造方法
	 */
	public MineAPI() {
		reloadConfig();
		// 载入管理层
		blockDataManager = new BlockDataManager(this);
		breakDataManager = new BreakDataManager(this);
	}
	
	/**
	 * 重载配置文件
	 */
	public void reloadConfig() {
		blockData = DataUtils.addPluginData("data-block", TabooMine.getInst());
		breakData = DataUtils.addPluginData("data-break", TabooMine.getInst());
		blockConfig = ConfigUtils.saveDefaultConfig(TabooMine.getInst(), "blocks.yml");
	}
	
	/**
	 * 是否允许破坏方块
	 * 
	 * @param item 物品
	 * @return
	 */
	public BreakResult canBreakBlock(String type, ItemStack item) {
		ConfigurationSection section = getBlockSection(type);
		// 没有配置
		if (!section.contains("allowitems") || section.getStringList("allowitems").size() == 0) {
			return BreakResult.ALLOW;
		}
		// 没有描述
		if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) {
			return BreakResult.DENY;
		}
		// 获取猫叔
		String lore = item.getItemMeta().getLore().toString();
		// 检查描述
		for (String key : section.getStringList("allowitems")) {
			if (lore.contains(key)) {
				// 耐久不足
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
	 * 获取方块类型
	 * 
	 * @param block 方块
	 * @param bypass 是否跳过材质判断
	 * @return
	 */
	public String getBlockType(Block block, boolean bypass) {
		// 获取数据
		String type = blockData.getString(LocationUtils.asString(block.getLocation()) + ".type");
		// 如果没有数据或跳过材质判断
		if (bypass || type == null) {
			return type;
		}
		try {
			// 获取配置
			ConfigurationSection section = getBlockSection(type);
			// 判断材质
			if (!block.getType().toString().equals(section.getString("before").split(":")[0]) || block.getData() != Byte.valueOf(section.getString("before").split(":")[1])) {
				return null;
			}
		}
		catch (Exception e) {
			MsgUtils.warn("材质判断出错: &4" + e.getMessage());
		}
		return type;
	}
	
	/**
	 * 获取方块配置
	 * 
	 * @param name 名称
	 * @return {@link ConfigurationSection}
	 */
	public ConfigurationSection getBlockSection(String name) {
		return blockConfig.getConfigurationSection(name);
	}
	
	/**
	 * 根据名称获取物品
	 * 
	 * @param name 名称
	 * @return {@link ItemStack}
	 */
	public ItemStack getItemStack(String name) {
		// 禁忌法典
		if (TabooMine.getInst().getConfig().getString("Settings.source").equalsIgnoreCase("taboocode")) {
			return TabooCodeItem.getItem(name, false);
		}
		// 禁忌书库
		if (TabooMine.getInst().getConfig().getString("Settings.source").equalsIgnoreCase("taboolib")) {
			return ItemUtils.getCacheItem(name);
		}
		// 警告
		MsgUtils.warn("错误的物品提供插件: &4" + TabooMine.getInst().getConfig().getString("Settings.source"), TabooMine.getInst());
		return null;
	}
}
