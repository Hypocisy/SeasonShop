package com.kumoe.SeasonShop.init;

import com.kumoe.SeasonShop.content.shipping.*;
import com.kumoe.SeasonShop.content.shop.ShippingShopScreen;
import com.kumoe.SeasonShop.content.shop.ShopBlock;
import com.kumoe.SeasonShop.content.shop.ShopBlockEntity;
import com.kumoe.SeasonShop.content.shop.ShopMenu;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.MenuEntry;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class SSBlock {


    public static MenuEntry<ShippingBinMenu> SHIPPING_BIN_BLOCK_MENU = SeasonShop.REGISTRATE.menu("shipping_bin_block_menu",
            ShippingBinMenu::new, () -> ShippingBinScreen::new).register();
    public static MenuEntry<ShopMenu> SHOP_BLOCK_MENU = SeasonShop.REGISTRATE.menu("shop_block_menu",
            ShopMenu::new, () -> ShippingShopScreen::new).register();

    public static void register() {
        SeasonShop.LOGGER.debug("Registering Season blocks...");
    }

    public static BlockEntry<ShopBlock> SHOP_BLOCK = SeasonShop.REGISTRATE.block("shop_block", ShopBlock::new).properties((properties) -> BlockBehaviour.Properties.copy(Blocks.CHEST)).tag(BlockTags.MINEABLE_WITH_AXE).simpleItem().register();

    public static BlockEntry<ShippingBinBlock> SHIPPING_BIN_BLOCK = SeasonShop.REGISTRATE.block("shipping_bin", ShippingBinBlock::new).properties((properties) -> BlockBehaviour.Properties.copy(Blocks.CHEST)).blockstate(ShippingBinBlock::buildModel).tag(BlockTags.MINEABLE_WITH_AXE).simpleItem().register();

    public static BlockEntityEntry<ShippingBinBlockEntity> SHIPPING_BIN_BLOCK_BE = SeasonShop.REGISTRATE.blockEntity("shipping_bin_block_entity", ShippingBinBlockEntity::new).validBlock(SHIPPING_BIN_BLOCK).renderer(() -> ShippingBinRenderer::new).register();
    public static BlockEntityEntry<ShopBlockEntity> SHOP_BE = SeasonShop.REGISTRATE.blockEntity("shop_block_entity", ShopBlockEntity::new).validBlock(SHOP_BLOCK).register();
}
