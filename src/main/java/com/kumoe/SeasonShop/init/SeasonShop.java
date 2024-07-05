package com.kumoe.SeasonShop.init;

import com.kumoe.SeasonShop.data.*;
import com.kumoe.SeasonShop.data.config.Config;
import com.kumoe.SeasonShop.data.config.SeasonShopConfig;
import com.kumoe.SeasonShop.data.datapack.*;
import com.kumoe.SeasonShop.network.NetworkHandler;
import com.kumoe.SeasonShop.network.PricesPacket;
import com.mojang.logging.LogUtils;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SeasonShop.MODID)
@Mod.EventBusSubscriber(modid = SeasonShop.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class SeasonShop {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "season_shop";
    public static final Registrate REGISTRATE = Registrate.create(MODID);
    public static final ResourceKey<Registry<Price>> ITEM_PRICES = ResourceKey.createRegistryKey(new ResourceLocation(MODID, "item_prices"));
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final PriceDataLoader<PriceData> priceLoader = new PriceDataLoader<>();
    private static SeasonShop instance;
    final Pair<Config, ForgeConfigSpec> configured = (new ForgeConfigSpec.Builder()).configure(Config::new);

    public SeasonShop() {
        instance = this;
        SeasonShopBlocks.register();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, configured.getRight());
        REGISTRATE.addDataGenerator(ProviderType.LANG, SSLangData::genLang);
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static SeasonShop getInstance() {
        return instance;
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        PackOutput output = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        RegistrySetBuilder builder = new RegistrySetBuilder();
        builder.add(ITEM_PRICES,
                bootstrap -> bootstrap.register(ResourceKey.create(ITEM_PRICES, id("example")),
                        new Price()
                ));
        LOGGER.debug("gathering data: {}", builder);
        event.getGenerator().addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(output, lookupProvider, builder, Set.of(MODID)));
    }

    public static PriceDataLoader<PriceData> getPriceLoader() {
        return priceLoader;
    }


    @SubscribeEvent
    public static void onModConfigLoad(ModConfigEvent event) {
        ModConfig config = event.getConfig();
        if (config.getSpec() == SeasonShop.getInstance().getConfigSpec()) {
            SeasonShop.LOGGER.debug("Loading EpicFightIntegration config");
            SeasonShopConfig.bake(config);
        }
    }
    // 必须注册这三个Channel 否则服务端无法识别
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        NetworkHandler.getNetwork().registerMessage(1, PricesPacket.class, PricesPacket::encode, PricesPacket::decode, PricesPacket::handle);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        NetworkHandler.getNetwork().registerMessage(1, PricesPacket.class, PricesPacket::encode, PricesPacket::decode, PricesPacket::handle);
    }

    @SubscribeEvent
    public static void onServerSetup(FMLDedicatedServerSetupEvent event) {
        NetworkHandler.getNetwork().registerMessage(1, PricesPacket.class, PricesPacket::encode, PricesPacket::decode, PricesPacket::handle);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MODID, path);
    }

    public Config getConfig() {
        return this.configured.getLeft();
    }

    public ForgeConfigSpec getConfigSpec() {
        return configured.getRight();
    }
}
