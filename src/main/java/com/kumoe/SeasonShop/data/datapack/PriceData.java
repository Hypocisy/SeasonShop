package com.kumoe.SeasonShop.data.datapack;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record PriceData(Map<ResourceLocation, Price> prices) {
    public static final Codec<PriceData> CODEC = Codec.unboundedMap(ResourceLocation.CODEC, Price.CODEC)
            .xmap(PriceData::new, PriceData::prices);
}
