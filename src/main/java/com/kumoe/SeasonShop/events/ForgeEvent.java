package com.kumoe.SeasonShop.events;

import com.kumoe.SeasonShop.api.Configs.SeasonShopConfig;
import com.kumoe.SeasonShop.content.shipping.ShippingBinScreen;
import com.kumoe.SeasonShop.content.shipping.ShopScreen;
import com.kumoe.SeasonShop.init.SeasonShop;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SeasonShop.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEvent {
    @SubscribeEvent
    public static void onScreenOpening(ScreenEvent.Opening event) {
        if (SeasonShopConfig.enableDebug) {
            if (event.getScreen() instanceof ShippingBinScreen shippingBinScreen) {
                SeasonShop.getLogger().debug("Opening shippingBinScreen: ");
            }
            if (event.getScreen() instanceof ShopScreen shippingShopScreen) {
                SeasonShop.getLogger().debug("Opening shippingShopScreen");
            }
        }
    }

}
