package com.kumoe.SeasonShop.network;

import com.kumoe.SeasonShop.api.ModUtils;
import com.kumoe.SeasonShop.api.PluginUtils;
import com.kumoe.SeasonShop.content.block.entity.BuyBackBlockEntity;
import com.kumoe.SeasonShop.data.SSLangData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import sereneseasons.api.season.SeasonHelper;

import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class C2SSellPlayerItem {

    private final UUID playerUuid;
    private final BlockPos pos;
    private final int slotId;
    private final int mouseButton;


    public C2SSellPlayerItem(UUID playerUuid, BlockPos pos, int slotId, int mouseButton) {
        this.playerUuid = playerUuid;
        this.pos = pos;
        this.slotId = slotId;
        this.mouseButton = mouseButton;
    }

    public static C2SSellPlayerItem decode(FriendlyByteBuf byteBuf) {
        UUID playerUuid = byteBuf.readUUID();
        BlockPos pos = byteBuf.readBlockPos();
        int slotId = byteBuf.readVarInt();
        int mouseButton = byteBuf.readVarInt();
        return C2SSellPlayerItem.create(playerUuid, pos, slotId, mouseButton);
    }

    public static C2SSellPlayerItem create(UUID playerUuid, BlockPos pos, int slotId, int mouseButton) {
        return new C2SSellPlayerItem(playerUuid, pos, slotId, mouseButton);
    }

    public void encode(FriendlyByteBuf byteBuf) {
        byteBuf.writeUUID(this.playerUuid);
        byteBuf.writeBlockPos(pos);
        byteBuf.writeVarInt(slotId);
        byteBuf.writeVarInt(mouseButton);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            if (ctx.getDirection().getReceptionSide().isServer()) {
                ServerPlayer player = ctx.getSender();
                if (player != null && player.level() instanceof ServerLevel serverLevel && serverLevel.getBlockEntity(pos) instanceof BuyBackBlockEntity buyBackBlockEntity) {
                    // get player want to sell item
                    var playerWantToSellItem = buyBackBlockEntity.getItem(slotId).copy();
                    var playerInventory = player.getInventory();

                    // Does player have this itemStack
                    var hasItem = playerInventory.hasAnyOf(Set.of(playerWantToSellItem.getItem()));
                    if (hasItem) {
                        var slotIndex = playerInventory.findSlotMatchingItem(playerWantToSellItem);
                        if (slotIndex == -1) {
                            return;
                        }
                        var playerInvItemStack = playerInventory.getItem(slotIndex);
                        var sellCount = mouseButton == 0 ? 1 : playerInvItemStack.getCount();
                        var season = SeasonHelper.getSeasonState(player.level()).getSeason();
                        var totalPrice = ModUtils.getOneItemPrice(season, playerWantToSellItem) * sellCount;
                        playerInventory.removeItem(slotIndex, sellCount);
                        playerInventory.setChanged();
                        PluginUtils.depositPlayer(player, totalPrice);
                        player.sendSystemMessage(Component.translatable(SSLangData.SHIPPING_BIN_TOOLTIP_6.key(), totalPrice));
                    }
                }
            }

        });
        ctx.setPacketHandled(true);
    }

}
