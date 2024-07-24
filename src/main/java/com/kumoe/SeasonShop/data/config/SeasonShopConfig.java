package com.kumoe.SeasonShop.data.config;

import com.kumoe.SeasonShop.init.SeasonShop;
import net.minecraftforge.fml.config.ModConfig;

public final class SeasonShopConfig {

    public static boolean enableDebug;
    public static int maxBindBlock;
    public static double defaultPrice;

    public static Config config = SeasonShop.getInstance().getConfig();

    public static void bake(ModConfig config) {
        // general settings
        initGeneralSettings();
    }

    private static void initGeneralSettings() {
        try {
            enableDebug = config.enableDebug.get();
            maxBindBlock = config.maxBindBlock.get();
            defaultPrice = config.defaultPrice.get();
        } catch (Exception var) {
            SeasonShop.getLogger().trace("An exception was caused trying to load the config for GeneralSettings.\n%s".formatted(var));
        }
    }

}
