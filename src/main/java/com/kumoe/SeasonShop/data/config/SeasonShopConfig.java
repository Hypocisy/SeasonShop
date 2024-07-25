package com.kumoe.SeasonShop.data.config;

import com.kumoe.SeasonShop.init.SeasonShop;

public final class SeasonShopConfig {

    public static boolean enableDebug;
    public static String apiUrl;
    public static String apiParams;
    public static int maxBindBlock;
    public static long transactionWindow;
    public static int expectedTransactions;
    public static double priceChangeFactor;
    public static double minPriceLimit;
    public static double maxPriceLimit;
    public static double defaultPrice;

    public static Config config = SeasonShop.getInstance().getConfig();

    public static void bake() {
        // general settings
        initGeneralSettings();
    }

    private static void initGeneralSettings() {
        try {
            enableDebug = config.enableDebug.get();
            maxBindBlock = config.maxBindBlock.get();
            apiUrl = config.apiUrl.get();
            apiParams = config.apiParams.get();
            transactionWindow = config.transactionWindow.get();
            expectedTransactions = config.expectedTransactions.get();
            priceChangeFactor = config.priceChangeFactor.get();
            minPriceLimit = config.minPriceLimit.get();
            maxPriceLimit = config.maxPriceLimit.get();
            defaultPrice = config.defaultPrice.get();
        } catch (Exception var) {
            SeasonShop.logger().trace("An exception was caused trying to load the config for GeneralSettings.\n%s".formatted(var));
        }
    }

}
