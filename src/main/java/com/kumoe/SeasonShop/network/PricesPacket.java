package com.kumoe.SeasonShop.network;

import com.kumoe.SeasonShop.content.shipping.ShippingBinBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class PricesPacket {
    private final UUID playerUuid;
    private final double price;
    private final BlockPos pos;

    public PricesPacket(UUID playerUuid, double price, BlockPos pos) {
        this.playerUuid = playerUuid;
        this.price = price;
        this.pos = pos;
    }

    public static PricesPacket decode(FriendlyByteBuf byteBuf) {
        UUID playerUuid = byteBuf.readUUID();
        double price = byteBuf.readDouble();
        BlockPos pos = byteBuf.readBlockPos();
        return PricesPacket.create(playerUuid, price, pos);
    }

    public static PricesPacket create(UUID playerUuid, double price, BlockPos pos) {
        return new PricesPacket(playerUuid, price, pos);
    }

    public void encode(FriendlyByteBuf byteBuf) {
        byteBuf.writeUUID(this.playerUuid);
        byteBuf.writeDouble(price);
        byteBuf.writeBlockPos(pos);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            // remove sold items
            if (ctx.getDirection().getReceptionSide().isServer()) {
                if (ctx.getSender().level().getBlockEntity(pos) instanceof ShippingBinBlockEntity bin) {
                    bin.getItems().clear();
                    bin.setChanged();
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
