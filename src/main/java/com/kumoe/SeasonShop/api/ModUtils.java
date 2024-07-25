package com.kumoe.SeasonShop.api;

import com.kumoe.SeasonShop.data.SSLangData;
import com.kumoe.SeasonShop.data.config.SeasonShopConfig;
import com.kumoe.SeasonShop.data.datapack.Price;
import com.kumoe.SeasonShop.data.datapack.PriceData;
import com.kumoe.SeasonShop.init.SeasonShop;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import sereneseasons.handler.season.SeasonHandler;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ModUtils {

    public static final String AVATAR_CACHE_DIR = "avatarCache" + File.separator;
    private static final long TRANSACTION_WINDOW = SeasonShopConfig.transactionWindow; // 1小时
    private static final int EXPECTED_TRANSACTIONS = SeasonShopConfig.expectedTransactions;// 每小时预期的"正常"交易量
    private static final double PRICE_CHANGE_FACTOR = SeasonShopConfig.priceChangeFactor;  // 每个物品对价格的影响因子
    private static final Map<ResourceLocation, List<Transaction>> recentTransactions = new HashMap<>();
    protected static Map<ResourceLocation, PriceData> priceDataMap = SeasonShop.getPriceLoader().getLoader();
    // size - overlay - default
    protected static String api = SeasonShopConfig.apiUrl;
    protected static String params = SeasonShopConfig.apiParams;

    public static void recordTransaction(ItemStack stack, int quantity) {
        ResourceLocation itemId = stackToResourceLocation(stack);
        if (!recentTransactions.containsKey(itemId)) {
            recentTransactions.put(itemId, new ArrayList<>());
        }
        recentTransactions.get(itemId).add(new Transaction(System.currentTimeMillis(), quantity));
    }

    private static double calculateDemandMultiplier(ResourceLocation itemId) {
        List<Transaction> transactions = recentTransactions.get(itemId);
        if (transactions == null || transactions.isEmpty()) {
            return 1.0;
        }
        long currentTime = System.currentTimeMillis();
        int totalQuantity = 0;
        transactions.removeIf(transaction -> currentTime - transaction.timestamp > TRANSACTION_WINDOW);
        for (Transaction transaction : transactions) {
            totalQuantity += transaction.quantity;
        }
        // 计算价格变化
        double multiplier = 1.0 + (EXPECTED_TRANSACTIONS - totalQuantity) * PRICE_CHANGE_FACTOR;
        // 限制价格变化范围
        return Mth.absMax(SeasonShopConfig.minPriceLimit, Math.min(SeasonShopConfig.maxPriceLimit, multiplier));
    }

    public static double getOneItemPrice(ItemStack stack) {
        // 如果设置价格则使用基础价格与季节价格相乘，如果没有返回默认价格。
        Price price = getItemPriceObject(stack);
        double basePrice = (price != null) ? getCurrentSeasonPrice(price) : SeasonShopConfig.defaultPrice;
        ResourceLocation itemId = stackToResourceLocation(stack);
        double demandMultiplier = itemId == null ? 1.0 : calculateDemandMultiplier(itemId);

        return BigDecimal.valueOf(basePrice * demandMultiplier).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    @Nullable
    public static Price getItemPriceObject(ItemStack stack) {
        PriceData priceData = priceDataMap.get(new ResourceLocation("season_shop", "prices/price_setting.json"));

        if (priceData != null) {
            return priceData.prices().get(stackToResourceLocation(stack));
        }
        return null;
    }

    @Nullable
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
            case SPRING -> itemPrice.springPrice().orElse(SeasonShopConfig.defaultPrice);
            case SUMMER -> itemPrice.summerPrice().orElse(SeasonShopConfig.defaultPrice);
            case AUTUMN -> itemPrice.autumnPrice().orElse(SeasonShopConfig.defaultPrice);
            case WINTER -> itemPrice.winterPrice().orElse(SeasonShopConfig.defaultPrice);
        };
    }

    /**
     * cache image to GameDir/avatarCache
     *
     * @param uuid file name
     */
    public static void cachePlayerAvatar(UUID uuid) {
        File avatarFile = getAvatarFile(uuid);
        if (!avatarFile.getParentFile().exists()) {
            avatarFile.getParentFile().mkdirs();
        }
        if (!avatarFile.exists()) {
            try {
                URL url = new URL(api + uuid + params);
                InputStream inputStream = url.openStream();
                BufferedImage bufferedImage = ImageIO.read(inputStream);
                ImageIO.write(bufferedImage, "png", avatarFile);
            } catch (IOException e) {
                SeasonShop.logger().debug(e.toString());
            }
        }
    }

    /**
     * use DynamicTexture
     *
     * @param avatarFile the file object for loading native images.
     * @param uuid       native uuid of image name
     * @return ResourceLocation that registered at client
     */
    @Nullable
    @OnlyIn(Dist.CLIENT)
    public static ResourceLocation loadPlayerAvatar(File avatarFile, UUID uuid) {
        if (avatarFile.exists()) {
            ResourceLocation avatarLocation = getAvatarLocation(uuid);
            try (NativeImage nativeImage = NativeImage.read(new FileInputStream(avatarFile))) {
                Minecraft.getInstance().execute(() -> {
                    DynamicTexture dynamicTexture = new DynamicTexture(nativeImage);
                    Minecraft.getInstance().getTextureManager().register(avatarLocation, dynamicTexture);
                });
                return avatarLocation;
            } catch (IOException e) {
                SeasonShop.logger().debug(e.toString());
            }
        } else {
            SeasonShop.logger().debug("downloading {} to {}", uuid, avatarFile.getPath());
        }
        return null;
    }

    /**
     * get avatar file object by uuid
     *
     * @param uuid local file uuid + .png
     * @return The File object of avatar
     */
    public static File getAvatarFile(UUID uuid) {
        return new File(FMLPaths.GAMEDIR.get() + File.separator + AVATAR_CACHE_DIR + uuid + ".png");
    }

    /**
     * get need register's avatar location
     *
     * @param uuid format is season_shop:textures/avatars/player_avatar_ + uuid
     * @return player avatar location
     */
    protected static ResourceLocation getAvatarLocation(UUID uuid) {
        return new ResourceLocation(SeasonShop.MODID, "textures/avatars/player_avatar_" + uuid);
    }

    public static MutableComponent getLangComponent(SSLangData langData, Object... pArgs) {
        return Component.translatable(langData.key(), pArgs).withStyle(langData.format());
    }

    private static class Transaction {
        long timestamp;
        int quantity;

        Transaction(long timestamp, int quantity) {
            this.timestamp = timestamp;
            this.quantity = quantity;
        }
    }
}
