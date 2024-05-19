package com.kumoe.SeasonShop.api.Configs;

import com.kumoe.SeasonShop.init.SeasonShop;
import net.minecraftforge.fml.config.ModConfig;

public final class SeasonShopConfig {

    public static boolean enableDebug;
    public static double springPrice;
    public static double summerPrice;
    public static double autumnPrice;
    public static double winterPrice;

    public static Config config = SeasonShop.getInstance().getConfig();

    public static void bake(ModConfig config) {
        // general settings
        initGeneralSettings();
    }

    private static void initGeneralSettings() {
        try {
            enableDebug = config.enableDebug.get();
            springPrice = config.springPrice.get();
            summerPrice = config.summerPrice.get();
            autumnPrice = config.autumnPrice.get();
            winterPrice = config.winterPrice.get();
        } catch (Exception var) {
            SeasonShop.getLogger().trace("An exception was caused trying to load the config for GeneralSettings.\n%s".formatted(var));
        }
    }

}
