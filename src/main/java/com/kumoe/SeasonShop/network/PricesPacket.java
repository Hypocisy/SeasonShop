package com.kumoe.SeasonShop.network;

import com.kumoe.SeasonShop.init.SeasonShop;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public class PricesPacket {
    private final String itemId;
    private final double price;

    public PricesPacket(String itemId, double price) {
        this.itemId = itemId;
        this.price = price;
    }

    public static PricesPacket decode(FriendlyByteBuf byteBuf) {
        int itemIdLength = byteBuf.readInt(); // 获取 item id length
        byte[] itemIdBytes = new byte[itemIdLength]; // 新建一个 item bytes length 长度的数组用于存item id的数据
        byteBuf.readBytes(itemIdBytes); // 将转换后的item写入byte数组
        return new PricesPacket(new String(itemIdBytes, StandardCharsets.UTF_8), byteBuf.readDouble());
    }

    public void encode(FriendlyByteBuf byteBuf) {
        byte[] bytes = this.itemId.getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
        byteBuf.writeDouble(price);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            if (ctx.getDirection().getReceptionSide().isClient()) {
                SeasonShop.getLogger().info(this.itemId);
                if (Minecraft.getInstance().player != null) {
                    Minecraft.getInstance().player.displayClientMessage(Component.literal("Item Id: " + this.itemId + " price " + this.price), true);
                }
                NetworkHandler.getNetwork().sendToServer(new PricesPacket(this.itemId, this.price));
                SeasonShop.getLogger().info("sent");
                // NetworkHandler.getNetwork().reply(new PricesPacket("minecraft:apple", this.price), ctx);
            }
        });
        ctx.setPacketHandled(true);
    }
}
