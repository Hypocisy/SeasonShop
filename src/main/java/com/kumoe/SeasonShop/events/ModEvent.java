package com.kumoe.SeasonShop.events;

import com.kumoe.SeasonShop.Configs.SeasonShopConfig;
import com.kumoe.SeasonShop.init.SeasonShop;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = SeasonShop.MODID,bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvent {
    @SubscribeEvent
    public static void onModConfigLoad(ModConfigEvent event) {
        ModConfig config = event.getConfig();
        if (config.getSpec() == SeasonShop.getInstance().getConfigSpec()) {
            SeasonShop.LOGGER.debug("Loading SeasonShop config");
            SeasonShopConfig.bake(config);
        }
    }
}
