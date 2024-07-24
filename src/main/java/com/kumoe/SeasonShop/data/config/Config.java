package com.kumoe.SeasonShop.data.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    protected ForgeConfigSpec.BooleanValue enableDebug;
    protected ForgeConfigSpec.IntValue maxBindBlock;
    protected ForgeConfigSpec.DoubleValue defaultPrice;

    public Config(ForgeConfigSpec.Builder builder) {
        builder.push("General settings");
        {
            enableDebug = builder.comment("Show debug info to player?").define("enableDebug", true);
            maxBindBlock = builder.comment("Set max bind blocks for player").defineInRange("maxBindBlock", 2,0,1000);
            defaultPrice = builder.comment("Set max bind blocks for player").defineInRange("defaultPrice", 1f,0f,Double.MAX_VALUE);
        }
        builder.pop();
    }
}

