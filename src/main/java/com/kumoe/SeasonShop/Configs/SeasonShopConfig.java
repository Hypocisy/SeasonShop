package com.kumoe.SeasonShop.Configs;

import com.kumoe.SeasonShop.init.SeasonShop;
import net.minecraftforge.fml.config.ModConfig;

public final class SeasonShopConfig {

    public static boolean enableDebug;
    public static boolean enableActionBar;

    public static Config config = SeasonShop.getInstance().getConfig();

    public static void bake(ModConfig config) {
        // general settings
        initGeneralSettings();
    }

    private static void initGeneralSettings() {
        try {
            enableDebug = config.enableDebug.get();
            enableActionBar = config.enableActionBar.get();
        } catch (Exception var) {
            SeasonShop.LOGGER.trace("An exception was caused trying to load the config for GeneralSettings.\n%s".formatted(var));
        }
    }

}
