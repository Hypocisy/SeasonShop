package com.kumoe.SeasonShop.api;

import com.kumoe.SeasonShop.data.datapack.Price;
import com.kumoe.SeasonShop.data.datapack.PriceData;
import com.kumoe.SeasonShop.init.SeasonShop;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import sereneseasons.handler.season.SeasonHandler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class ModUtils {

    // todo 将箱子绑定到玩家，控制玩家可放置箱子的数量
    // todo gui 显示玩家的头像
    protected static Map<ResourceLocation, PriceData> priceDataMap = SeasonShop.getPriceLoader().getLoader();

    /**
     * @param stack The item of slot
     * @return Item price
     */
    public static double getTotalItemPrice(ItemStack stack) {
        // 如果设置价格则使用基础价格与季节价格和相乘，如果没有返回0的默认价格。
        return getItemPriceObject(stack) == null ? 0f : BigDecimal.valueOf(stack.getCount() * getCurrentSeasonPrice(getItemPriceObject(stack))).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public static double getOneItemPrice(ItemStack stack) {
        // 如果设置价格则使用基础价格与季节价格相乘，如果没有返回0.1的默认价格。
        return getItemPriceObject(stack) == null ? 0f : getCurrentSeasonPrice(getItemPriceObject(stack));
    }


    public static Price getItemPriceObject(ItemStack stack) {
        var priceData = priceDataMap.get(new ResourceLocation("season_shop", "prices/price_setting.json")).prices();

        if (priceData != null) {
            return priceData.get(stackToResourceLocation(stack));
        }
        return null;
    }

    public static ResourceLocation stackToResourceLocation(ItemStack stack) {
        return ForgeRegistries.ITEMS.getKey(stack.getItem());
    }

    /**
     * @return Get Season Icon V Offset
     */
    public static int getSeasonIconVOffset() {
        return switch (SeasonHandler.getClientSeasonTime().getSeason()) {
            case SUMMER -> 13;
            case AUTUMN -> 26;
            case WINTER -> 39;
            default -> 0;
        };
    }

    /**
     * @return Get current season's item price
     */
    public static double getCurrentSeasonPrice(Price itemPrice) {
        return switch (SeasonHandler.getClientSeasonTime().getSeason()) {
            case SPRING -> itemPrice.springPrice().orElse(0.1);
            case SUMMER -> itemPrice.summerPrice().orElse(0.1);
            case AUTUMN -> itemPrice.autumnPrice().orElse(0.1);
            case WINTER -> itemPrice.winterPrice().orElse(0.1);
        };
    }
}
