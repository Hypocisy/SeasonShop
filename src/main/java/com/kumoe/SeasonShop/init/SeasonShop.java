package com.kumoe.SeasonShop.init;

import com.kumoe.SeasonShop.content.screen.KanBanGirlOverlay;
import com.kumoe.SeasonShop.data.SSLangData;
import com.kumoe.SeasonShop.data.config.Config;
import com.kumoe.SeasonShop.data.config.SeasonShopConfig;
import com.kumoe.SeasonShop.data.datapack.*;
import com.kumoe.SeasonShop.network.NetworkHandler;
import com.kumoe.SeasonShop.network.S2CBuyBackSettingSyncPacket;
import com.kumoe.SeasonShop.network.S2CPriceSyncPacket;
import com.kumoe.SeasonShop.network.S2CShopSettingSyncPacket;
import com.mojang.logging.LogUtils;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;


@Mod(SeasonShop.MODID)
@Mod.EventBusSubscriber(modid = SeasonShop.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class SeasonShop {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "season_shop";
    public static final Registrate REGISTRATE = Registrate.create(MODID);
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final PriceDataLoader<PriceData> priceLoader = new PriceDataLoader<>();
    private static final ShopSettingLoader<ResourceLocation, ShopSetting> settingLoader = new ShopSettingLoader<>();
    private static final BuyBackSettingLoader<ResourceLocation, BuyBackSetting> buyBackSettingLoader = new BuyBackSettingLoader<>();
    private static SeasonShop instance;
    final Pair<Config, ForgeConfigSpec> configured = (new ForgeConfigSpec.Builder()).configure(Config::new);

    public SeasonShop(FMLJavaModLoadingContext context) {
        instance = this;
        SeasonShopBlocks.register();
        context.registerConfig(ModConfig.Type.SERVER, configured.getRight());
        REGISTRATE.addDataGenerator(ProviderType.LANG, SSLangData::genLang);
        MinecraftForge.EVENT_BUS.addListener(this::onDatapackSync);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static Logger logger() {
        return LOGGER;
    }

    public static SeasonShop getInstance() {
        return instance;
    }

    public static PriceDataLoader<PriceData> getPriceLoader() {
        return priceLoader;
    }

    public static ShopSettingLoader<ResourceLocation, ShopSetting> getSettingLoader() {
        return settingLoader;
    }

    public static BuyBackSettingLoader<ResourceLocation, BuyBackSetting> getBuyBackSettingLoader() {
        return buyBackSettingLoader;
    }

    @SubscribeEvent
    public static void onModConfigLoad(ModConfigEvent event) {
        ModConfig config = event.getConfig();
        if (config.getSpec() == SeasonShop.getInstance().getConfigSpec()) {
            SeasonShop.LOGGER.debug("Loading " + SeasonShop.MODID + " config");
            SeasonShopConfig.bake();
        }
    }

    @SubscribeEvent
    public static void onCommonSetup(final FMLCommonSetupEvent event) {
        NetworkHandler.register();
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerGuiOverlayEvent(final RegisterGuiOverlaysEvent evt) {
        evt.registerAboveAll("kanban_girl", new KanBanGirlOverlay());
    }

    public void onDatapackSync(OnDatapackSyncEvent event) {
        var s2CPriceSyncPacket = new S2CPriceSyncPacket(SeasonShop.getPriceLoader().getLoader());
        var s2CShopSettingSyncPacket = new S2CShopSettingSyncPacket(SeasonShop.getSettingLoader().getLoader());
        var s2CBuyBackSettingSyncPacket = new S2CBuyBackSettingSyncPacket(SeasonShop.getBuyBackSettingLoader().getLoader());
        if (event.getPlayer() != null) {
            NetworkHandler.sendToPlayer(event::getPlayer, s2CPriceSyncPacket);
            NetworkHandler.sendToPlayer(event::getPlayer, s2CShopSettingSyncPacket);
            NetworkHandler.sendToPlayer(event::getPlayer, s2CBuyBackSettingSyncPacket);
        }
    }

    public Config getConfig() {
        return this.configured.getLeft();
    }

    public ForgeConfigSpec getConfigSpec() {
        return configured.getRight();
    }
}
