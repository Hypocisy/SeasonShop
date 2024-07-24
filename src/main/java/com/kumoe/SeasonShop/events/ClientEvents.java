package com.kumoe.SeasonShop.events;

import com.kumoe.SeasonShop.api.ModUtils;
import com.kumoe.SeasonShop.content.screen.ShippingBinScreen;
import com.kumoe.SeasonShop.content.screen.ShopScreen;
import com.kumoe.SeasonShop.data.config.SeasonShopConfig;
import com.kumoe.SeasonShop.init.SeasonShop;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = SeasonShop.MODID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinLevelEvent event) {
        // cache player image
        if (event.getEntity().level().isClientSide()) {
            new Thread(() -> ModUtils.cachePlayerAvatar(event.getEntity().getUUID())).start();
        }
    }

    @SubscribeEvent
    public static void onScreenOpening(ScreenEvent.Opening event) {
        if (SeasonShopConfig.enableDebug) {
            if (event.getScreen() instanceof ShippingBinScreen) {
                SeasonShop.logger().debug("Opening shippingBinScreen");
            }
            if (event.getScreen() instanceof ShopScreen) {
                SeasonShop.logger().debug("Opening shippingShopScreen");
            }
        }
    }
}
