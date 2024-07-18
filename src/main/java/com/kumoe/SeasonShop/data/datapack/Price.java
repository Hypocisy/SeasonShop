package com.kumoe.SeasonShop.data.datapack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record Price(Optional<Double> basePrice, Optional<Double> springPrice, Optional<Double> summerPrice,
                    Optional<Double> autumnPrice, Optional<Double> winterPrice) {
    public static final Codec<Price> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("basePrice").forGetter(Price::basePrice),
            Codec.DOUBLE.optionalFieldOf("springPrice").forGetter(Price::springPrice),
            Codec.DOUBLE.optionalFieldOf("summerPrice").forGetter(Price::summerPrice),
            Codec.DOUBLE.optionalFieldOf("autumnPrice").forGetter(Price::autumnPrice),
            Codec.DOUBLE.optionalFieldOf("winterPrice").forGetter(Price::winterPrice)
    ).apply(instance, Price::new));
}
