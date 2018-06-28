package me.skymc.taboomine2.api.manager;

import me.skymc.taboolib.inventory.ItemUtils;
import me.skymc.taboolib.message.MsgUtils;
import me.skymc.taboolib.other.DateUtils;
import me.skymc.taboolib.particle.EffLib;
import me.skymc.taboomine2.TabooMine;
import me.skymc.taboomine2.api.MineAPI;
import me.skymc.taboomine2.event.BlockMineEvent;
import me.skymc.taboomine2.event.BlockRestoreEvent;
import me.skymc.taboomine2.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

/**
 * @author sky
 * @since 2018年2月11日15:08:53
 */
public class BreakDataManager {

    private MineAPI API;

    public BreakDataManager(MineAPI api) {
        this.API = api;

        // 恢复任务
        new BukkitRunnable() {

            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                // 遍历所有已破坏方块
                for (String preLocation : getBlocks()) {
                    ConfigurationSection section = api.getBlockSection(api.getBreakData().getString(preLocation + ".type"));
                    if (section == null) {
                        delBlock(preLocation);
                    }
                    // 如果破坏事件 + 刷新时间 >= 当前时间
                    else if (api.getBreakData().getLong(preLocation + ".date") + DateUtils.formatDate(section.getString("update")) <= System.currentTimeMillis()) {
                        // 删除数据
                        delBlock(preLocation);
                        // 切回主线程
                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                try {
                                    // 获取方块
                                    Block block = LocationUtils.asLocation(preLocation).getBlock();
                                    // 播放粒子
                                    EffLib.SMOKE_LARGE.display(0.5f, 0.5f, 0.5f, 0, 50, block.getLocation().add(0.5, 0, 0.5), 50);
                                    // 设置材质
                                    block.setType(ItemUtils.asMaterial(section.getString("before").split(":")[0]));
                                    block.setData(Byte.valueOf(section.getString("before").split(":")[1]));
                                } catch (Exception e) {
                                    MsgUtils.warn("方块恢复错误: &4" + e.getMessage());
                                }
                            }
                        }.runTask(TabooMine.getInst());
                    }
                }
            }
        }.runTaskTimerAsynchronously(TabooMine.getInst(), 0, 20);
    }

    public Set<String> getBlocks() {
        return API.getBreakData().getConfigurationSection("").getKeys(false);
    }

    public void addBlock(Block block, String type) {
        String location = LocationUtils.asString(block.getLocation());
        API.getBreakData().set(location + ".type", type);
        API.getBreakData().set(location + ".date", System.currentTimeMillis());
        Bukkit.getPluginManager().callEvent(new BlockMineEvent(block, type));
    }

    public void delBlock(Block block) {
        delBlock(LocationUtils.asString(block.getLocation()));
    }

    public void delBlock(String location) {
        if (API.getBreakData().contains(location)) {
            String type = API.getBreakData().getString(location + ".type");
            API.getBreakData().set(location, null);
            Bukkit.getPluginManager().callEvent(new BlockRestoreEvent(LocationUtils.asLocation(location).getBlock(), type));
        }
    }

    public void update() {
        API.getBreakData().getConfigurationSection("").getKeys(false).forEach(x -> API.getBreakData().set(x, null));
        API.getBlockDataManager().resetAllBlock();
    }

    public void update(String type) {
        // 遍历所有已破坏方块
        for (String preLocation : getBlocks()) {
            if (type.equals(API.getBreakData().getString(preLocation + ".type"))) {
                // 删除数据
                API.getBreakData().set(preLocation, null);
                // 更新方块
                API.getBlockDataManager().resetBlock(preLocation);
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
