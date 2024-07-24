package com.kumoe.SeasonShop.api;

import com.kumoe.SeasonShop.data.config.SeasonShopConfig;
import com.kumoe.SeasonShop.data.datapack.Price;
import com.kumoe.SeasonShop.data.datapack.PriceData;
import com.kumoe.SeasonShop.init.SeasonShop;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
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
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.UUID;

import static com.kumoe.SeasonShop.data.SSLangData.COMMAND_CLEAN_AVATAR_FAILED;
import static com.kumoe.SeasonShop.data.SSLangData.COMMAND_CLEAN_AVATAR_SUCCESS;

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
        Price price = getItemPriceObject(stack);
        double oneItemPrice = getOneItemPrice(stack);
        if (price == null)
            return oneItemPrice * stack.getCount();
        return BigDecimal.valueOf(stack.getCount() * getCurrentSeasonPrice(price)).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public static double getOneItemPrice(ItemStack stack) {
        // 如果设置价格则使用基础价格与季节价格相乘，如果没有返回默认价格。
        Price price = getItemPriceObject(stack);
        if (price != null) {
            return getCurrentSeasonPrice(price);
        }
        return SeasonShopConfig.defaultPrice;
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

    @Nullable
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

    public static int clearPlayerAvatarCache(CommandContext<CommandSourceStack> context) {
        Path path = FMLPaths.GAMEDIR.get().resolve(AVATAR_CACHE_DIR);

        try {
            if (Files.exists(path)) {
                // Use walkFileTree to delete the directory and its contents recursively
                Files.walkFileTree(path, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
                context.getSource().sendSystemMessage(Component.translatable(COMMAND_CLEAN_AVATAR_SUCCESS.key()).withStyle(COMMAND_CLEAN_AVATAR_SUCCESS.format()));
                return 1;
            } else {
                context.getSource().sendSystemMessage(Component.translatable(COMMAND_CLEAN_AVATAR_FAILED.key()).withStyle(COMMAND_CLEAN_AVATAR_FAILED.format()));
            }
        } catch (IOException e) {
            SeasonShop.logger().debug(e.toString());
        }
        return 0;
    }

    public static File getAvatarFile(UUID uuid) {
        return new File(FMLPaths.GAMEDIR.get() + File.separator + AVATAR_CACHE_DIR + uuid + ".png");
    }

    protected static ResourceLocation getAvatarLocation(UUID uuid) {
        return new ResourceLocation(SeasonShop.MODID, "textures/avatars/player_avatar_" + uuid);
    }
}
