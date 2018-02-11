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
 * @since 2018年2月11日 下午2:42:57
 */
public class BlockDataManager {
	
	@Getter
	private MineAPI API;
	
	/**
	 * 构造方法
	 */
	public BlockDataManager(MineAPI api) {
		this.API = api;
	}
	
	/**
	 * 获取所有被标记的方块
	 * 
	 * @return {@link Set}
	 */
	public Set<String> getBlocks() {
		return API.getBlockData().getConfigurationSection("").getKeys(false);
	}
	
	/**
	 * 添加标记方块
	 * 
	 * @param block 方块
	 * @param type 类型
	 */
	public void addBlock(Block block, String type) {
		API.getBlockData().set(LocationUtils.asString(block.getLocation()) + ".type", type);
		Bukkit.getPluginManager().callEvent(new BlockMarkEvent(block, type));
	}
	
	/**
	 * 取消标记方块
	 * 
	 * @param block 方块
	 */
	public void delBlock(Block block) {
		API.getBlockData().set(LocationUtils.asString(block.getLocation()), null);
		Bukkit.getPluginManager().callEvent(new BlockUnmarkEvent(block));
	}
	
	/**
	 * 重置所有未被破坏的方块
	 */
	public void resetAllBlock() {
		for (String preLocation : getBlocks()) {
			resetBlock(preLocation);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void resetBlock(String preLocation) {
		// 获取方块
		Block block = LocationUtils.asLocation(preLocation).getBlock();
		// 获取配置
		ConfigurationSection section = API.getBlockSection(API.getBlockData().getString(preLocation + ".type"));
		// 资源已被删除
		if (section == null) {
			delBlock(block);
			MsgUtils.send("坐标 &f" + preLocation + " &7的资源配置不存在, 已删除该资源点");
		}
		// 如果没有破坏记录
		else if (!API.getBreakData().contains(preLocation + ".date")) {
			try {
				// 设置材质
				block.setType(ItemUtils.asMaterial(section.getString("before").split(":")[0]));
				block.setData(Byte.valueOf(section.getString("before").split(":")[1]));
			}
			catch (Exception e) {
				MsgUtils.warn("方块初始化错误: &4" + e.getMessage());
			}
		}
	}
}
