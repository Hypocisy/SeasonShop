package com.kumoe.SeasonShop.events;

import com.kumoe.SeasonShop.api.ModUtils;
import com.kumoe.SeasonShop.content.shipping.ShippingBinBlockEntity;
import com.kumoe.SeasonShop.content.shipping.ShippingBinMenu;
import com.kumoe.SeasonShop.content.shipping.ShippingBinScreen;
import com.kumoe.SeasonShop.content.shipping.ShopScreen;
import com.kumoe.SeasonShop.data.PlacedBlockOwnerData;
import com.kumoe.SeasonShop.data.config.SeasonShopConfig;
import com.kumoe.SeasonShop.init.SeasonShop;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

import static com.kumoe.SeasonShop.data.SSLangData.*;

@Mod.EventBusSubscriber(modid = SeasonShop.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEvents {

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onScreenOpening(ScreenEvent.Opening event) {
        if (SeasonShopConfig.enableDebug) {
            if (event.getScreen() instanceof ShippingBinScreen) {
                SeasonShop.getLogger().debug("Opening shippingBinScreen");
            }
            if (event.getScreen() instanceof ShopScreen) {
                SeasonShop.getLogger().debug("Opening shippingShopScreen");
            }
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (event.getEntity() instanceof LocalPlayer localPlayer) {
            if (localPlayer.level().isClientSide && localPlayer.containerMenu instanceof ShippingBinMenu) {
                event.getToolTip().add(Component.translatable(SHIPPING_BIN_TOOLTIP_1.key(), ModUtils.getOneItemPrice(event.getItemStack())).withStyle(SHIPPING_BIN_TOOLTIP_1.format()));
                event.getToolTip().add(Component.translatable(SHIPPING_BIN_TOOLTIP_2.key(), ModUtils.getTotalItemPrice(event.getItemStack())).withStyle(SHIPPING_BIN_TOOLTIP_2.format()));
            }
        }
    }

    @SubscribeEvent
    public static void addReloadListener(AddReloadListenerEvent event) {
//        SeasonShop.getLogger().debug("add reload listener  " + SeasonShop.getPriceLoader().toString());
        event.addListener(SeasonShop.getPriceLoader());
    }

    @SubscribeEvent
    public static void onPlayerPlaceBlock(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer && serverPlayer.level() instanceof ServerLevel serverLevel &&
                serverLevel.getBlockEntity(event.getPos()) instanceof ShippingBinBlockEntity shippingBe) {
            PlacedBlockOwnerData data = PlacedBlockOwnerData.get(serverLevel);

            UUID playerUUID = serverPlayer.getUUID();

//            SeasonShop.getLogger().debug("player uuid: " + playerUUID);

            if (playerUUID != null) {
                int count = PlacedBlockOwnerData.getCount(playerUUID);
                if (count < SeasonShopConfig.maxBindBlock) {
                    shippingBe.setOwner(playerUUID);
                    PlacedBlockOwnerData.setCount(playerUUID, count + 1);
                    shippingBe.setChanged();
                    data.setDirty();
                } else {
                    event.setCanceled(true);
                    serverPlayer.displayClientMessage(Component.translatable(SHIPPING_BIN_TIPS.key(), SeasonShopConfig.maxBindBlock).withStyle(SHIPPING_BIN_TIPS.format()), false);
                }

            }

        }
    }

    @SubscribeEvent
    public static void onPlayerBreakBlock(BlockEvent.BreakEvent event) {
        if (event.getPlayer() instanceof ServerPlayer serverPlayer && serverPlayer.level() instanceof ServerLevel serverLevel &&
                serverLevel.getBlockEntity(event.getPos()) instanceof ShippingBinBlockEntity shippingBe) {
            SeasonShop.getLogger().debug(shippingBe.getOwner().toString());
            if (shippingBe.getOwner() != null) {
                UUID playerUUID = shippingBe.getOwner();
                PlacedBlockOwnerData data = PlacedBlockOwnerData.get(serverLevel);
                int count = PlacedBlockOwnerData.getCount(playerUUID);
                if (count > 0) {
                    PlacedBlockOwnerData.setCount(playerUUID, count - 1);
                    data.setDirty();
                }
            }
        }
    }
}
