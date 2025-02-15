package com.kumoe.SeasonShop.api;

import com.kumoe.SeasonShop.init.SeasonShop;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;

public class PluginUtils {
    private static final String SEASON_SHOP = "SeasonShop";

    public static Optional<Plugin> getPlugin(String plugin) {
        return Optional.ofNullable(Bukkit.getPluginManager().getPlugin(plugin));
    }

    static Class<?> getPluginClass() {
        return getPlugin(SEASON_SHOP).orElseThrow().getClass();
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        var method = clazz.getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method;
    }

    public static double getBalance(Player player) {
        try {
            var clazz = getPluginClass();
            Method method = getMethod(clazz, "getBalance", UUID.class);
            return (double) method.invoke(clazz, player.getUUID());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return 0d;
    }

    public static void depositPlayer(Player player, double price) {
        depositPlayer(player.getUUID(), price);
    }

    public static void depositPlayer(UUID player, double price) {
        try {
            var clazz = getPluginClass();
            Method method = getMethod(clazz, "depositPlayer", UUID.class, Double.TYPE);
            method.invoke(clazz, player, price);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void withdrawPlayer(Player player, double price) {
        withdrawPlayer(player.getUUID(), price);
    }

    public static void withdrawPlayer(UUID player, double price) {
        try {
            var clazz = getPluginClass();
            Method method = getMethod(clazz, "withdrawPlayer", UUID.class, Double.TYPE);
            method.invoke(clazz, player, price);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void withdrawPlayer(UUID userUuid, double price, double tax) {
        price += tax;
        withdrawPlayer(userUuid, price);
    }

    public static boolean checkBukkitInstalled() {
        try {
            var server = Bukkit.getServer();
            // server maybe null dou bukkit not installed
            return server != null;
        } catch (NoClassDefFoundError e) {
            SeasonShop.logger().debug("No Bukkit installed on this platform");
        }
        return false;
    }
}
