package com.kumoe.SeasonShop.network;

import com.kumoe.SeasonShop.init.SeasonShop;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

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
//        SimpleChannel net = NetworkRegistry.newSimpleChannel(SeasonShop.id("main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
        INSTANCE = net;

        net.registerMessage(getId(), PricesPacket.class, PricesPacket::encode, PricesPacket::decode, PricesPacket::handle);

        net.messageBuilder(S2CPriceSyncPacket.class, getId(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(S2CPriceSyncPacket::decode)
                .encoder(S2CPriceSyncPacket::encode)
                .consumerNetworkThread(S2CPriceSyncPacket::handle).add();
        net.messageBuilder(S2CClearAvatarPacket.class, getId(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(S2CClearAvatarPacket::decode)
                .encoder(S2CClearAvatarPacket::encode)
                .consumerNetworkThread(S2CClearAvatarPacket::handle).add();
    }

    public static SimpleChannel getInstance() {
        return INSTANCE;
    }

    public static void sendToServer(PricesPacket pricesPacket) {
        INSTANCE.sendToServer(pricesPacket);
    }

    public static void sendToPlayer(PacketDistributor.PacketTarget with, S2CPriceSyncPacket packet) {
        INSTANCE.send(with, packet);
    }
}
