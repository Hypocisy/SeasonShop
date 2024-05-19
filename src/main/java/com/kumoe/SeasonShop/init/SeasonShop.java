package com.kumoe.SeasonShop.init;

import com.kumoe.SeasonShop.api.Configs.Config;
import com.mojang.logging.LogUtils;
import com.tterrag.registrate.Registrate;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SeasonShop.MODID)
public final class SeasonShop {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "season_shop";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Registrate REGISTRATE = Registrate.create(MODID);
    private static SeasonShop instance;
    final Pair<Config, ForgeConfigSpec> configured = (new ForgeConfigSpec.Builder()).configure(Config::new);

    public SeasonShop() {
        instance = this;
        SeasonShopBlocks.register();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, configured.getRight());
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static SeasonShop getInstance() {
        return instance;
    }

    public Config getConfig() {
        return this.configured.getLeft();
    }

    public ForgeConfigSpec getConfigSpec() {
        return configured.getRight();
    }
}
