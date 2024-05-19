package com.kumoe.SeasonShop.events;

import com.kumoe.SeasonShop.api.Configs.SeasonShopConfig;
import com.kumoe.SeasonShop.init.SeasonShop;
import com.kumoe.SeasonShop.network.NetworkHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = SeasonShop.MODID,bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvent {
    @SubscribeEvent
    public static void onCommonSetup(final FMLCommonSetupEvent event) {
        NetworkHandler.registerMessages();
    }

    @SubscribeEvent
    public static void onModConfigLoad(ModConfigEvent.Loading event) {
        ModConfig config = event.getConfig();
        if (config.getSpec() == SeasonShop.getInstance().getConfigSpec()) {
            SeasonShop.getLogger().debug("Loading SeasonShop config");
            SeasonShopConfig.bake(config);
        }
    }
}
