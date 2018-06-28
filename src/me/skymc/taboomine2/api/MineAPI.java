package me.skymc.taboomine2.api;

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
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

/**
 * @author sky
 * @since 2018年2月11日 下午2:30:14
 */
public class MineAPI {

    private FileConfiguration blockData;
    private FileConfiguration breakData;
    private FileConfiguration blockConfig;
    private BlockDataManager blockDataManager;
    private BreakDataManager breakDataManager;

    public MineAPI() {
        reloadConfig();
        blockDataManager = new BlockDataManager(this);
        breakDataManager = new BreakDataManager(this);
    }

    public void reloadConfig() {
        blockData = DataUtils.addPluginData("data-block", TabooMine.getInst());
        breakData = DataUtils.addPluginData("data-break", TabooMine.getInst());
        blockConfig = ConfigUtils.saveDefaultConfig(TabooMine.getInst(), "blocks.yml");
    }

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
                return DurabilityAPI.getDurability(item) < section.getInt("durability") ? BreakResult.DURABILITY : BreakResult.ALLOW;
            }
        }
        return BreakResult.DENY;
    }

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
        } catch (Exception e) {
            MsgUtils.warn("材质判断出错: &4" + e.getMessage());
        }
        return type;
    }

    public ConfigurationSection getBlockSection(String name) {
        return blockConfig.getConfigurationSection(name);
    }

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

    // *********************************
    //
    //        Getter and Setter
    //
    // *********************************

    public FileConfiguration getBlockData() {
        return blockData;
    }

    public FileConfiguration getBreakData() {
        return breakData;
    }

    public FileConfiguration getBlockConfig() {
        return blockConfig;
    }

    public BlockDataManager getBlockDataManager() {
        return blockDataManager;
    }

    public BreakDataManager getBreakDataManager() {
        return breakDataManager;
    }
}
