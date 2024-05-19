package com.kumoe.SeasonShop.network;

import com.kumoe.SeasonShop.init.SeasonShop;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    static int id = 0;
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SeasonShop.MODID, "main"),
            () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);
    public static void registerMessages() {
//        NETWORK.registerMessage(id++, OpenGui2Packet.class,OpenGui2Packet::encode, OpenGui2Packet::decode,OpenGui2Packet::handle);
    }

    public static SimpleChannel getNetwork() {
        return NETWORK;
    }
}
