package com.kumoe.SeasonShop.network;

import com.kumoe.SeasonShop.api.ModUtils;
import com.kumoe.SeasonShop.api.PluginUtils;
import com.kumoe.SeasonShop.content.block.entity.ShippingBinBlockEntity;
import com.kumoe.SeasonShop.init.SeasonShop;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

import java.util.UUID;
import java.util.function.Supplier;

public class C2SBinPricesPacket {
    private final UUID playerUuid;
    private final BlockPos pos;
    private double totalPrice;

    public C2SBinPricesPacket(UUID playerUuid, BlockPos pos) {
        this.playerUuid = playerUuid;
        this.pos = pos;
    }

    public static C2SBinPricesPacket decode(FriendlyByteBuf byteBuf) {
        UUID playerUuid = byteBuf.readUUID();
        BlockPos pos = byteBuf.readBlockPos();
        return C2SBinPricesPacket.create(playerUuid, pos);
    }

    public static C2SBinPricesPacket create(UUID playerUuid, BlockPos pos) {
        return new C2SBinPricesPacket(playerUuid, pos);
    }

    public void encode(FriendlyByteBuf byteBuf) {
        byteBuf.writeUUID(this.playerUuid);
        byteBuf.writeBlockPos(pos);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            // remove sold items
            Player player = ctx.getSender();
            if (player == null) return;
            if (ctx.getDirection().getReceptionSide().isServer()) {
                var level = player.level();
                if (level.getBlockEntity(pos) instanceof ShippingBinBlockEntity bin) {
                    if (PluginUtils.checkBukkitInstalled()) {
                        Season season = SeasonHelper.getSeasonState(level).getSeason();
                        for (ItemStack itemStack : bin.getItems()) {
                            totalPrice += ModUtils.getOneItemPrice(season, itemStack) * itemStack.getCount();
                            ModUtils.recordTransaction(itemStack, itemStack.getCount());
                        }
                        bin.getItems().clear();
                        bin.setChanged();
                        PluginUtils.depositPlayer(player, totalPrice);
                        ctx.setPacketHandled(true);
                        NetworkHandler.sendToPlayer(ctx::getSender, new S2CTotalValue(totalPrice));
                        SeasonShop.logger().debug("Player {} has been deposited to {}, now have {} balance", playerUuid, totalPrice, PluginUtils.getBalance(ctx.getSender()));
                    }
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
