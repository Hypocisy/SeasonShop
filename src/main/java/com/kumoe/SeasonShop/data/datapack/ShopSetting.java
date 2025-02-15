package com.kumoe.SeasonShop.data.datapack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public record ShopSetting(Map<Integer, List<ResourceLocation>> setting) {
    public static final Codec<ShopSetting> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Codec.STRING.xmap(Integer::parseInt, Objects::toString), Codec.list(ResourceLocation.CODEC)).fieldOf("pageSet").forGetter(ShopSetting::setting)).apply(instance, ShopSetting::new));
}
