package com.kumoe.SeasonShop.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class PricesPacket {
    private final UUID playerUuid;
    private final double price;

    public PricesPacket(UUID playerUuid, double price) {
        this.playerUuid = playerUuid;
        this.price = price;
    }

    public static PricesPacket decode(FriendlyByteBuf byteBuf) {
        UUID playerUuid = byteBuf.readUUID();
        double price = byteBuf.readDouble();
        return PricesPacket.create(playerUuid, price);
    }

    public static PricesPacket create(UUID playerUuid, double price) {
        return new PricesPacket(playerUuid, price);
    }

    public void encode(FriendlyByteBuf byteBuf) {
        byteBuf.writeUUID(this.playerUuid);
        byteBuf.writeDouble(price);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(()->{});
        ctx.setPacketHandled(true);
    }
}
