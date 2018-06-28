package me.skymc.taboomine2.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * @author sky
 * @since 2018年2月11日 下午3:00:31
 */
public class LocationUtils {

    public static String asString(Location location) {
        return location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }

    public static Location asLocation(String location) {
        try {
            return new Location(Bukkit.getWorld(location.split(",")[0]), Double.valueOf(location.split(",")[1]), Double.valueOf(location.split(",")[2]), Double.valueOf(location.split(",")[3]));
        } catch (Exception e) {
            return null;
        }
    }
}
