package com.kumoe.SeasonShop.data.datapack;


import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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

public class BuyBackSettingLoader<R extends ResourceLocation, T extends BuyBackSetting> extends SimplePreparableReloadListener<Map<R, T>> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String FOLDER = "buyback_settings";
    private final Map<R, T> loader = new HashMap<>();

    @Override
    public Map<R, T> prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        pProfiler.startTick();
        pProfiler.push("loading shop setting data...");
        // season_shop/data/season_shop
        var loader = listResources(pResourceManager, pProfiler);
        loader.forEach((resourceLocation, setting) -> SeasonShop.logger().debug(setting.setting().toString()));
        pProfiler.pop();
        return loader;
    }


    @Override
    protected void apply(Map<R, T> shopSetting, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        shopSetting.putAll(loader);
    }

    public T getData(R pItemId) {
        return loader.get(pItemId);
    }

    @SuppressWarnings("unchecked")
    public Map<R, T> listResources(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        pProfiler.startTick();
        for (Map.Entry<ResourceLocation, Resource> resource : pResourceManager.listResources(FOLDER, p -> p.getPath().endsWith(".json")).entrySet()) {
            try (Reader reader = resource.getValue().openAsReader()) {
                JsonElement element = JsonParser.parseReader(reader);
                BuyBackSetting.CODEC.parse(JsonOps.INSTANCE, element)
                        .resultOrPartial(error -> SeasonShop.logger().debug("Failed to parse BuyBackSetting data {}", error))
                        .ifPresent(itemValues -> loader.put((R) resource.getKey(), (T) itemValues));
            } catch (Exception e) {
                LOGGER.error("Failed to load custom data pack: {}", resource.getKey(), e);
            }
        }
        pProfiler.endTick();
        return loader;
    }

    public Map<R, T> getLoader() {
        return loader;
    }
}
