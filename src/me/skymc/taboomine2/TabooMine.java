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
 * @since 2018年2月10日 下午10:46:13
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
        // 载入API
        mineAPI = new MineAPI();
        // 监听器
        Bukkit.getPluginManager().registerEvents(new ListenerBlockBreak(), this);
        // 初始化所有未被破坏的方块
        Bukkit.getScheduler().runTask(this, () -> mineAPI.getBlockDataManager().resetAllBlock());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§7/mine2 set [TYPE] §8—— §f设置方块");
            sender.sendMessage("§7/mine2 remove §8—— §f移除方块");
            sender.sendMessage("§7/mine2 update §8—— §f重载资源");
            sender.sendMessage("§7/mine2 reload §8—— §f重载配置");
        } else if (args[0].equals("reload")) {
            reloadConfig();
            mineAPI.reloadConfig();
            sender.sendMessage("reload ok!");
        } else if (args[0].equals("set") && sender instanceof Player) {
            if (args.length != 2 || !mineAPI.getBlockConfig().contains(args[1])) {
                sender.sendMessage("§4参数错误");
                return true;
            }
            // 获取方块
            Block block = PlayerUtils.getTargetBlock((Player) sender, 10);
            if (block == null || block.getType().equals(Material.AIR)) {
                sender.sendMessage("§4不可设置为空气");
                return true;
            }
            // 获取类型
            String type = mineAPI.getBlockType(block, true);
            if (type != null) {
                sender.sendMessage("§7这个方块已经设置被为: §f" + type);
                return true;
            }
            // 设置类型
            mineAPI.getBlockDataManager().addBlock(block, args[1]);
            mineAPI.getBlockDataManager().resetBlock(LocationUtils.asString(block.getLocation()));
            sender.sendMessage("§7设置成功");
            // 播放特效
            EffLib.FLAME.display(0.5f, 0.5f, 0.5f, 0, 200, block.getLocation().add(0.5, 0, 0.5), 50);
        } else if (args[0].equals("remove") && sender instanceof Player) {
            // 获取方块
            Block block = PlayerUtils.getTargetBlock((Player) sender, 10);
            if (block == null || block.getType().equals(Material.AIR)) {
                sender.sendMessage("§4不可移除空气");
                return true;
            }
            // 获取类型
            String type = mineAPI.getBlockType(block, true);
            if (type == null) {
                sender.sendMessage("§7这个方块没有被标记为资源点");
                return true;
            }
            // 设置类型
            mineAPI.getBlockDataManager().delBlock(block);
            sender.sendMessage("§7删除成功");
            // 播放特效
            EffLib.FLAME.display(0.5f, 0.5f, 0.5f, 0, 200, block.getLocation().add(0.5, 0, 0.5), 50);
        } else if (args[0].equals("update")) {
            long time = System.currentTimeMillis();
            // 重置全部
            if (args.length == 1) {
                mineAPI.getBreakDataManager().update();
            } else {
                mineAPI.getBreakDataManager().update(args[1]);
            }
            // 提示信息
            sender.sendMessage("§7重置成功, 耗时: §f" + (System.currentTimeMillis() - time) + "ms");
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
