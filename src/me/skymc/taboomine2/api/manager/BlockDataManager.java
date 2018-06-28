package me.skymc.taboomine2.api.manager;

import me.skymc.taboolib.inventory.ItemUtils;
import me.skymc.taboolib.message.MsgUtils;
import me.skymc.taboomine2.api.MineAPI;
import me.skymc.taboomine2.utils.LocationUtils;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Set;

/**
 * @author sky
 * @since 2018年2月11日 下午2:42:57
 */
public class BlockDataManager {

    private MineAPI API;

    public BlockDataManager(MineAPI api) {
        this.API = api;
    }

    public Set<String> getBlocks() {
        return API.getBlockData().getConfigurationSection("").getKeys(false);
    }

    public void addBlock(Block block, String type) {
        API.getBlockData().set(LocationUtils.asString(block.getLocation()) + ".type", type);
    }

    public void delBlock(Block block) {
        API.getBlockData().set(LocationUtils.asString(block.getLocation()), null);
    }

    public void resetAllBlock() {
        getBlocks().forEach(this::resetBlock);
    }

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
            } catch (Exception e) {
                MsgUtils.warn("方块初始化错误: &4" + e.getMessage());
            }
        }
    }

    // *********************************
    //
    //        Getter and Setter
    //
    // *********************************

    public MineAPI getAPI() {
        return API;
    }
}
