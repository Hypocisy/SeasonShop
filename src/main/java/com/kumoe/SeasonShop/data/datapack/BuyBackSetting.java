package com.kumoe.SeasonShop.data.datapack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import sereneseasons.api.season.Season;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public record BuyBackSetting(Season.SubSeason subSeason, Map<Integer, List<ResourceLocation>> setting) {
    public static final Codec<BuyBackSetting> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Season.SubSeason.CODEC.fieldOf("sub_season").forGetter(BuyBackSetting::subSeason),
            Codec.unboundedMap(Codec.STRING.xmap(Integer::parseInt, Objects::toString), Codec.list(ResourceLocation.CODEC)).fieldOf("pageSet").forGetter(BuyBackSetting::setting)
    ).apply(instance, BuyBackSetting::new));
}
