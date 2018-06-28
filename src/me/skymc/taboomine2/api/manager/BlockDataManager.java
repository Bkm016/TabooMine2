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
 * @since 2018��2��11�� ����2:42:57
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
            } catch (Exception e) {
                MsgUtils.warn("�����ʼ������: &4" + e.getMessage());
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
