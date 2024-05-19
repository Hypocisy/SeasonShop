package com.kumoe.SeasonShop.api.Configs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Map;
import java.util.Optional;

public record ItemPriceBySeason(Optional<Map<String, Integer>> itemPrice) {
    public static final Codec<ItemPriceBySeason> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Codec.STRING, Codec.INT).optionalFieldOf("itemPrice").forGetter(ItemPriceBySeason::itemPrice)
    ).apply(instance, ItemPriceBySeason::new));

}
