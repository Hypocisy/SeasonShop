package com.kumoe.SeasonShop.init;

import com.kumoe.SeasonShop.data.SSLangData;
import com.kumoe.SeasonShop.data.config.Config;
import com.kumoe.SeasonShop.data.config.SeasonShopConfig;
import com.kumoe.SeasonShop.data.datapack.PriceData;
import com.kumoe.SeasonShop.data.datapack.PriceDataLoader;
import com.kumoe.SeasonShop.network.NetworkHandler;
import com.kumoe.SeasonShop.network.S2CPriceSyncPacket;
import com.mojang.logging.LogUtils;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.PacketDistributor;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SeasonShop.MODID)
@Mod.EventBusSubscriber(modid = SeasonShop.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class SeasonShop {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "season_shop";
    public static final Registrate REGISTRATE = Registrate.create(MODID);
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
        MinecraftForge.EVENT_BUS.addListener(this::onDatapackSync);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static SeasonShop getInstance() {
        return instance;
    }

    public static PriceDataLoader<PriceData> getPriceLoader() {
        return priceLoader;
    }

    @SubscribeEvent
    public static void onModConfigLoad(ModConfigEvent event) {
        ModConfig config = event.getConfig();
        if (config.getSpec() == SeasonShop.getInstance().getConfigSpec()) {
            SeasonShop.LOGGER.debug("Loading " + SeasonShop.MODID + " config");
            SeasonShopConfig.bake(config);
        }
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MODID, path);
    }

    @SubscribeEvent
    public static void onCommonSetup(final FMLCommonSetupEvent event) {
        NetworkHandler.register();
    }

    public void onDatapackSync(OnDatapackSyncEvent event) {
        var packet = new S2CPriceSyncPacket(SeasonShop.getPriceLoader().getLoader());
        if (event.getPlayer() !=null) {
            NetworkHandler.sendToPlayer(PacketDistributor.PLAYER.with(event::getPlayer), packet);
        }
    }

    public Config getConfig() {
        return this.configured.getLeft();
    }

    public ForgeConfigSpec getConfigSpec() {
        return configured.getRight();
    }
}
