package com.kumoe.SeasonShop.api;

import com.kumoe.SeasonShop.data.config.SeasonShopConfig;
import com.kumoe.SeasonShop.data.datapack.Price;
import com.kumoe.SeasonShop.data.datapack.PriceData;
import com.kumoe.SeasonShop.init.SeasonShop;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
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
import java.util.Map;
import java.util.UUID;

public class ModUtils {

    private static final String AVATAR_CACHE_DIR = "avatarCache" + File.separator;
    // todo 将箱子绑定到玩家，控制玩家可放置箱子的数量
    // todo gui 显示玩家的头像
    protected static Map<ResourceLocation, PriceData> priceDataMap = SeasonShop.getPriceLoader().getLoader();
    // size - overlay - default
    protected static String api = "https://crafatar.com/avatars/";
    protected static String params = "?size=16";

    /**
     * @param stack The item of slot
     * @return Item price
     */
    public static double getTotalItemPrice(ItemStack stack) {
        // 如果设置价格则使用基础价格与季节价格和相乘，如果没有返回默认价格。
        if (getItemPriceObject(stack) == null)
            return getOneItemPrice(stack) * stack.getCount();
        return BigDecimal.valueOf(stack.getCount() * getCurrentSeasonPrice(getItemPriceObject(stack))).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public static double getOneItemPrice(ItemStack stack) {
        // 如果设置价格则使用基础价格与季节价格相乘，如果没有返回默认价格。
        return getItemPriceObject(stack) == null ? SeasonShopConfig.defaultPrice : getCurrentSeasonPrice(getItemPriceObject(stack));
    }

    public static Price getItemPriceObject(ItemStack stack) {
        var priceData = priceDataMap.get(new ResourceLocation("season_shop", "prices/price_setting.json"));

        if (priceData != null) {
            return priceData.prices().get(stackToResourceLocation(stack));
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
            case SPRING -> itemPrice.springPrice().orElse(SeasonShopConfig.defaultPrice);
            case SUMMER -> itemPrice.summerPrice().orElse(SeasonShopConfig.defaultPrice);
            case AUTUMN -> itemPrice.autumnPrice().orElse(SeasonShopConfig.defaultPrice);
            case WINTER -> itemPrice.winterPrice().orElse(SeasonShopConfig.defaultPrice);
        };
    }

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
                SeasonShop.getLogger().debug(e.toString());
            }
        }
    }

    public static ResourceLocation loadPlayerAvatar(UUID uuid) {
        File avatarFile = getAvatarFile(uuid);
        ResourceLocation avatarLocation = new ResourceLocation(SeasonShop.MODID, "textures/avatars/player_avatar_" + uuid);
        try (NativeImage nativeImage = NativeImage.read(new FileInputStream(avatarFile))) {
            Minecraft.getInstance().execute(() -> {
                DynamicTexture dynamicTexture = new DynamicTexture(nativeImage);
                Minecraft.getInstance().getTextureManager().register(avatarLocation, dynamicTexture);
            });
        } catch (IOException e) {
            SeasonShop.getLogger().debug(e.toString());
            return null;
        }
        return avatarLocation;
    }

    protected static File getAvatarFile(UUID uuid) {
        return new File(FMLPaths.GAMEDIR.get() + File.separator + AVATAR_CACHE_DIR + uuid + ".png");
    }
}
