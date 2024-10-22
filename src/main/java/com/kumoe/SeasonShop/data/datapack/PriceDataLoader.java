package com.kumoe.SeasonShop.data.datapack;


import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.kumoe.SeasonShop.data.config.SeasonShopConfig;
import com.kumoe.SeasonShop.init.SeasonShop;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class PriceDataLoader<T extends PriceData> extends SimplePreparableReloadListener<Map<ResourceLocation, T>> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String FOLDER = "prices";
    private final Map<ResourceLocation, T> loader = new HashMap<>();

    @Override
    public Map<ResourceLocation, T> prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        pProfiler.startTick();
        pProfiler.push("loading item price data...");
        // season_shop/data/season_shop
        var loader = listResources(pResourceManager, pProfiler);

        pProfiler.pop();
        if (SeasonShopConfig.enableDebug) {
            loader.forEach((resourceLocation, t) -> {
                SeasonShop.logger().debug("loading item price data: {}", resourceLocation);
                t.prices().forEach((itemId, itemPrice) -> SeasonShop.logger().debug("loading item price data:{} base {}\n spring {}\n summer {}\n autumn {}\n winter {}",
                        itemId, itemPrice.basePrice().get(), itemPrice.springPrice().get(), itemPrice.summerPrice().get(), itemPrice.autumnPrice().get(), itemPrice.winterPrice().get()));
            });
        }
        return loader;
    }


    @Override
    protected void apply(Map<ResourceLocation, T> prices, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        prices.putAll(loader);
    }

    public T getData(ResourceLocation pItemId) {
        return loader.get(pItemId);
    }

    @SuppressWarnings("unchecked")
    public Map<ResourceLocation, T> listResources(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        for (Map.Entry<ResourceLocation, Resource> resource : pResourceManager.listResources(FOLDER, p -> p.getPath().endsWith(".json")).entrySet()) {
            try (Reader reader = resource.getValue().openAsReader()) {
                JsonElement element = JsonParser.parseReader(reader);
                PriceData.CODEC.parse(JsonOps.INSTANCE, element)
                        .resultOrPartial(error -> {
                            SeasonShop.logger().debug("Failed to parse price data {}", error);
                        })
                        .ifPresent(itemValues -> loader.put(resource.getKey(), (T) itemValues));
            } catch (Exception e) {
                LOGGER.error("Failed to load custom data pack: {}", resource.getKey(), e);
            }
        }
        return loader;
    }

    public Map<ResourceLocation, T> getLoader() {

        return loader;
    }
}
