package com.kumoe.SeasonShop.network;

import com.kumoe.SeasonShop.init.SeasonShop;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    private static SimpleChannel INSTANCE;
    private static int id = 0;

    public static int getId() {
        return id++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder.named(
                        new ResourceLocation(SeasonShop.MODID, "main")).networkProtocolVersion(() -> PROTOCOL_VERSION)
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();
        INSTANCE = net;

        net.registerMessage(getId(), C2SBinPricesPacket.class, C2SBinPricesPacket::encode, C2SBinPricesPacket::decode, C2SBinPricesPacket::handle);

        net.messageBuilder(S2CPriceSyncPacket.class, getId(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(S2CPriceSyncPacket::decode)
                .encoder(S2CPriceSyncPacket::encode)
                .consumerNetworkThread(S2CPriceSyncPacket::handle).add();

        net.messageBuilder(S2CShopSettingSyncPacket.class, getId(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(S2CShopSettingSyncPacket::decode)
                .encoder(S2CShopSettingSyncPacket::encode)
                .consumerNetworkThread(S2CShopSettingSyncPacket::handle).add();

        net.messageBuilder(S2CBuyBackSettingSyncPacket.class, getId(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(S2CBuyBackSettingSyncPacket::decode)
                .encoder(S2CBuyBackSettingSyncPacket::encode)
                .consumerNetworkThread(S2CBuyBackSettingSyncPacket::handle).add();

        net.registerMessage(getId(), UpdatePageMessage.class, UpdatePageMessage::encode, UpdatePageMessage::decode, UpdatePageMessage::handle);
        net.registerMessage(getId(), UpdatePageSubSeasonMessage.class, UpdatePageSubSeasonMessage::encode, UpdatePageSubSeasonMessage::decode, UpdatePageSubSeasonMessage::handle);
        net.messageBuilder(S2CClearAvatarPacket.class, getId(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(S2CClearAvatarPacket::decode)
                .encoder(S2CClearAvatarPacket::encode)
                .consumerNetworkThread(S2CClearAvatarPacket::handle).add();

        net.registerMessage(getId(), C2SSellPlayerItem.class, C2SSellPlayerItem::encode, C2SSellPlayerItem::decode, C2SSellPlayerItem::handle);
        net.registerMessage(getId(), S2CTotalValue.class, S2CTotalValue::encode, S2CTotalValue::decode, S2CTotalValue::handle);
    }

    public static SimpleChannel getInstance() {
        return INSTANCE;
    }

    public static void sendToServer(C2SBinPricesPacket c2SBinPricesPacket) {
        INSTANCE.sendToServer(c2SBinPricesPacket);
    }

    public static <MSG> void sendToPlayer(Supplier<ServerPlayer> player, MSG packet) {
        INSTANCE.send(PacketDistributor.PLAYER.with(player), packet);
    }
}
