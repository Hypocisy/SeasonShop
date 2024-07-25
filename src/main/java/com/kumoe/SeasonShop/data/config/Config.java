package com.kumoe.SeasonShop.data.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    protected ForgeConfigSpec.BooleanValue enableDebug;
    protected ForgeConfigSpec.ConfigValue<String> apiUrl;
    protected ForgeConfigSpec.ConfigValue<String> apiParams;
    protected ForgeConfigSpec.IntValue maxBindBlock;
    protected ForgeConfigSpec.LongValue transactionWindow;
    protected ForgeConfigSpec.IntValue expectedTransactions;
    protected ForgeConfigSpec.DoubleValue priceChangeFactor;
    protected ForgeConfigSpec.DoubleValue minPriceLimit;
    protected ForgeConfigSpec.DoubleValue maxPriceLimit;
    protected ForgeConfigSpec.DoubleValue defaultPrice;

    public Config(ForgeConfigSpec.Builder builder) {
        builder.push("General settings");
        {
            enableDebug = builder.comment("显示debug信息吗?").define("enableDebug", true);
            apiUrl = builder.comment("设置当前请求头像使用的api").define("apiUrl", "https://crafatar.com/avatars/");
            apiParams = builder.comment("设置当前请求头像使用的api").define("apiParams", "?size=16");
            maxBindBlock = builder.comment("设置玩家最多绑定的箱子方块数量").defineInRange("maxBindBlock", 2, 0, 1000);
            transactionWindow = builder.comment("设置滑动窗口的时间(default 1 hours)").defineInRange("transactionWindow", 1000 * 60 * 60L, 0L, Long.MAX_VALUE);
            expectedTransactions = builder.comment("设置预期滑动窗口的“正常”交易量 x(x越大, 降价的速度越慢，x越小，降价的速度越快。").comment("实际交易量>x时将会按比例降价，小于x时按比例涨价").defineInRange("expectedTransactions", 640, 0, Integer.MAX_VALUE);
            priceChangeFactor = builder.comment("设置每个物品对价格的影响因子").defineInRange("priceChangeFactor", 0.005, 0.00001, Double.MAX_VALUE);
            minPriceLimit = builder.comment("设置最低降价到原来基础价格的百分比").defineInRange("minPriceLimit", 0.5, 0.01, Double.MAX_VALUE);
            maxPriceLimit = builder.comment("设置最高涨价价到原来基础价格的百分比").defineInRange("maxPriceLimit", 1.5, 0.01, Double.MAX_VALUE);
            defaultPrice = builder.comment("设置数据包未定义物品的价格").defineInRange("defaultPrice", 1f, 0f, Double.MAX_VALUE);
        }
        builder.pop();
    }
}

