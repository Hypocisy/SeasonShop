package com.kumoe.SeasonShop.api;

import com.kumoe.SeasonShop.api.Configs.SeasonShopConfig;
import net.minecraft.world.item.ItemStack;
import sereneseasons.handler.season.SeasonHandler;
import sereneseasons.season.SeasonTime;

import java.util.HashMap;
import java.util.Map;

public class ModUtils {

    // todo: Calculate slot item to value
    protected static Map<ItemStack, Integer> itemPrices = new HashMap<>();

    /**
     * @param stack The item of slot
     * @return Item price
     */
    public static double getItemCost(ItemStack stack) {
        return itemPrices.get(stack) == null ? stack.getCount() * 0.1 : (itemPrices.get(stack) * stack.getCount() * getCurrentSeasonPrice());
    }


    /**
     * @return Get Season Icon V Offset
     */
    public static int getSeasonIconVOffset() {
        SeasonTime time = SeasonHandler.getClientSeasonTime();
        return switch (time.getSeason()) {
            case SUMMER -> 13;
            case AUTUMN -> 26;
            case WINTER -> 39;
            default -> 0;
        };
    }

    /**
     * @return Get current season's item price
     */
    public static double getCurrentSeasonPrice() {
        SeasonTime time = SeasonHandler.getClientSeasonTime();
        return switch (time.getSeason()) {
            case SPRING -> SeasonShopConfig.springPrice;
            case SUMMER -> SeasonShopConfig.summerPrice;
            case AUTUMN -> SeasonShopConfig.autumnPrice;
            case WINTER -> SeasonShopConfig.winterPrice;
        };
    }
}
