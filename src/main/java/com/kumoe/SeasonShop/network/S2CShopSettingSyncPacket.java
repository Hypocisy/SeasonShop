package com.kumoe.SeasonShop.network;

import com.kumoe.SeasonShop.data.datapack.ShopSetting;
import com.kumoe.SeasonShop.init.SeasonShop;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public record S2CShopSettingSyncPacket(Map<ResourceLocation, ShopSetting> map) {
    private static final Codec<Map<ResourceLocation, ShopSetting>> MAPPER =
            Codec.unboundedMap(ResourceLocation.CODEC, ShopSetting.CODEC);
    public static Map<ResourceLocation, ShopSetting> SYNCED_DATA = new HashMap<>();

    public static S2CShopSettingSyncPacket decode(FriendlyByteBuf buf) {
        return new S2CShopSettingSyncPacket(MAPPER.parse(NbtOps.INSTANCE, buf.readNbt()).result().orElse(new HashMap<>()));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt((CompoundTag) MAPPER.encodeStart(NbtOps.INSTANCE, this.map).result().orElse(new CompoundTag()));
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide().isClient()) {
            ctx.get().enqueueWork(() -> SeasonShop.getSettingLoader().getLoader().putAll(this.map));
        }
        ctx.get().enqueueWork(this::handlePacketOnMainThread);
        ctx.get().setPacketHandled(true);
    }

    private void handlePacketOnMainThread() {
        SYNCED_DATA = this.map;
    }
}
