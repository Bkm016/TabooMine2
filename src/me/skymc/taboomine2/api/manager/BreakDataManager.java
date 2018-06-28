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
 * @since 2018��2��11��15:08:53
 */
public class BreakDataManager {

    private MineAPI API;

    public BreakDataManager(MineAPI api) {
        this.API = api;

        // �ָ�����
        new BukkitRunnable() {

            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                // �����������ƻ�����
                for (String preLocation : getBlocks()) {
                    ConfigurationSection section = api.getBlockSection(api.getBreakData().getString(preLocation + ".type"));
                    if (section == null) {
                        delBlock(preLocation);
                    }
                    // ����ƻ��¼� + ˢ��ʱ�� >= ��ǰʱ��
                    else if (api.getBreakData().getLong(preLocation + ".date") + DateUtils.formatDate(section.getString("update")) <= System.currentTimeMillis()) {
                        // ɾ������
                        delBlock(preLocation);
                        // �л����߳�
                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                try {
                                    // ��ȡ����
                                    Block block = LocationUtils.asLocation(preLocation).getBlock();
                                    // ��������
                                    EffLib.SMOKE_LARGE.display(0.5f, 0.5f, 0.5f, 0, 50, block.getLocation().add(0.5, 0, 0.5), 50);
                                    // ���ò���
                                    block.setType(ItemUtils.asMaterial(section.getString("before").split(":")[0]));
                                    block.setData(Byte.valueOf(section.getString("before").split(":")[1]));
                                } catch (Exception e) {
                                    MsgUtils.warn("����ָ�����: &4" + e.getMessage());
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
        // �����������ƻ�����
        for (String preLocation : getBlocks()) {
            if (type.equals(API.getBreakData().getString(preLocation + ".type"))) {
                // ɾ������
                API.getBreakData().set(preLocation, null);
                // ���·���
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
