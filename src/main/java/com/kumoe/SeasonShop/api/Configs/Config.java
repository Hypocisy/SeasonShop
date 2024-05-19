package com.kumoe.SeasonShop.api.Configs;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    protected ForgeConfigSpec.BooleanValue enableDebug;
    protected ForgeConfigSpec.DoubleValue springPrice;
    protected ForgeConfigSpec.DoubleValue summerPrice;
    protected ForgeConfigSpec.DoubleValue autumnPrice;
    protected ForgeConfigSpec.DoubleValue winterPrice;

    public Config(ForgeConfigSpec.Builder builder) {
        builder.push("General settings");
        {
            enableDebug = builder.comment("Show debug info to player?").define("enableDebug", true);
            springPrice  = builder.comment("spring price").defineInRange("springPrice", 1.0, 0.1, Double.MAX_VALUE);
            summerPrice = builder.comment("summer price").defineInRange("summerPrice", 1.2, 0.1, Double.MAX_VALUE);
            autumnPrice = builder.comment("autumn price").defineInRange("autumnPrice", 1.0, 0.1, Double.MAX_VALUE);
            winterPrice = builder.comment("winter price").defineInRange("winterPrice", 1.5, 0.1, Double.MAX_VALUE);
        }
        builder.pop();
    }
}

