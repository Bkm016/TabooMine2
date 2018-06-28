package me.skymc.taboomine2;

import me.skymc.taboolib.particle.EffLib;
import me.skymc.taboolib.player.PlayerUtils;
import me.skymc.taboomine2.api.MineAPI;
import me.skymc.taboomine2.listener.ListenerBlockBreak;
import me.skymc.taboomine2.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author sky
 * @since 2018��2��10�� ����10:46:13
 */
public class TabooMine extends JavaPlugin {

    private static Plugin inst;
    private static MineAPI mineAPI;

    @Override
    public void onLoad() {
        inst = this;
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        // ����API
        mineAPI = new MineAPI();
        // ������
        Bukkit.getPluginManager().registerEvents(new ListenerBlockBreak(), this);
        // ��ʼ������δ���ƻ��ķ���
        Bukkit.getScheduler().runTask(this, () -> mineAPI.getBlockDataManager().resetAllBlock());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("��7/mine2 set [TYPE] ��8���� ��f���÷���");
            sender.sendMessage("��7/mine2 remove ��8���� ��f�Ƴ�����");
            sender.sendMessage("��7/mine2 update ��8���� ��f������Դ");
            sender.sendMessage("��7/mine2 reload ��8���� ��f��������");
        } else if (args[0].equals("reload")) {
            reloadConfig();
            mineAPI.reloadConfig();
            sender.sendMessage("reload ok!");
        } else if (args[0].equals("set") && sender instanceof Player) {
            if (args.length != 2 || !mineAPI.getBlockConfig().contains(args[1])) {
                sender.sendMessage("��4��������");
                return true;
            }
            // ��ȡ����
            Block block = PlayerUtils.getTargetBlock((Player) sender, 10);
            if (block == null || block.getType().equals(Material.AIR)) {
                sender.sendMessage("��4��������Ϊ����");
                return true;
            }
            // ��ȡ����
            String type = mineAPI.getBlockType(block, true);
            if (type != null) {
                sender.sendMessage("��7��������Ѿ����ñ�Ϊ: ��f" + type);
                return true;
            }
            // ��������
            mineAPI.getBlockDataManager().addBlock(block, args[1]);
            mineAPI.getBlockDataManager().resetBlock(LocationUtils.asString(block.getLocation()));
            sender.sendMessage("��7���óɹ�");
            // ������Ч
            EffLib.FLAME.display(0.5f, 0.5f, 0.5f, 0, 200, block.getLocation().add(0.5, 0, 0.5), 50);
        } else if (args[0].equals("remove") && sender instanceof Player) {
            // ��ȡ����
            Block block = PlayerUtils.getTargetBlock((Player) sender, 10);
            if (block == null || block.getType().equals(Material.AIR)) {
                sender.sendMessage("��4�����Ƴ�����");
                return true;
            }
            // ��ȡ����
            String type = mineAPI.getBlockType(block, true);
            if (type == null) {
                sender.sendMessage("��7�������û�б����Ϊ��Դ��");
                return true;
            }
            // ��������
            mineAPI.getBlockDataManager().delBlock(block);
            sender.sendMessage("��7ɾ���ɹ�");
            // ������Ч
            EffLib.FLAME.display(0.5f, 0.5f, 0.5f, 0, 200, block.getLocation().add(0.5, 0, 0.5), 50);
        } else if (args[0].equals("update")) {
            long time = System.currentTimeMillis();
            // ����ȫ��
            if (args.length == 1) {
                mineAPI.getBreakDataManager().update();
            } else {
                mineAPI.getBreakDataManager().update(args[1]);
            }
            // ��ʾ��Ϣ
            sender.sendMessage("��7���óɹ�, ��ʱ: ��f" + (System.currentTimeMillis() - time) + "ms");
        }
        return true;
    }

    // *********************************
    //
    //        Getter and Setter
    //
    // *********************************

    public static Plugin getInst() {
        return inst;
    }

    public static MineAPI getMineAPI() {
        return mineAPI;
    }
}
