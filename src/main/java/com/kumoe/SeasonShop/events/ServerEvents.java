package com.kumoe.SeasonShop.events;

import com.kumoe.SeasonShop.api.ModUtils;
import com.kumoe.SeasonShop.content.block.entity.ShippingBinBlockEntity;
import com.kumoe.SeasonShop.content.command.SeasonShopCommand;
import com.kumoe.SeasonShop.content.menu.ShippingBinMenu;
import com.kumoe.SeasonShop.data.PlacedBlockOwnerData;
import com.kumoe.SeasonShop.data.config.SeasonShopConfig;
import com.kumoe.SeasonShop.init.SeasonShop;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

import static com.kumoe.SeasonShop.data.SSLangData.*;

@Mod.EventBusSubscriber(modid = SeasonShop.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEvents {
    @SubscribeEvent
    public static void addReloadListener(AddReloadListenerEvent event) {
        event.addListener(SeasonShop.getPriceLoader());
    }

    @SubscribeEvent
    public static void onPlayerPlaceBlock(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer && serverPlayer.level() instanceof ServerLevel serverLevel &&
                serverLevel.getBlockEntity(event.getPos()) instanceof ShippingBinBlockEntity shippingBe) {
            PlacedBlockOwnerData data = PlacedBlockOwnerData.get(serverLevel);

            UUID playerUUID = serverPlayer.getUUID();
            int count = data.getCount(playerUUID);
            if (count < SeasonShopConfig.maxBindBlock) {
                shippingBe.setOwner(playerUUID);
                data.setCount(playerUUID, count + 1);
                shippingBe.setChanged();
                data.setDirty();
            } else {
                event.setCanceled(true);
                serverPlayer.displayClientMessage(ModUtils.getLangComponent(SHIPPING_BIN_TIPS, SeasonShopConfig.maxBindBlock), false);
            }
        }
    }

    @SubscribeEvent
    public static void onCommandRegister(final RegisterCommandsEvent event) {
        SeasonShopCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onPlayerBreakBlock(BlockEvent.BreakEvent event) {
        if (event.getPlayer() instanceof ServerPlayer serverPlayer && serverPlayer.level() instanceof ServerLevel serverLevel &&
                serverLevel.getBlockEntity(event.getPos()) instanceof ShippingBinBlockEntity shippingBe) {
            // get shipping block entity's owner id
            UUID playerUUID = shippingBe.getOwner();
            SeasonShop.logger().debug(playerUUID.toString());
            PlacedBlockOwnerData data = PlacedBlockOwnerData.get(serverLevel);
            int count = data.getCount(playerUUID);
            if (count > 0) {
                data.setCount(playerUUID, count - 1);
                data.setDirty();
            }
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        Player player = event.getEntity();
        if (player != null) {
            if (player.level().isClientSide()) {
                if (event.getEntity().containerMenu instanceof ShippingBinMenu) {
                    event.getToolTip().add(ModUtils.getLangComponent(SHIPPING_BIN_TOOLTIP_1, ModUtils.getOneItemPrice(event.getItemStack())));
                    event.getToolTip().add(ModUtils.getLangComponent(SHIPPING_BIN_TOOLTIP_2, ModUtils.getTotalItemPrice(event.getItemStack())));
                }
            }
        }
    }
}
