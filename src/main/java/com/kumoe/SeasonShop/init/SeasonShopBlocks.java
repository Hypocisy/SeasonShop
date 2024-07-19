package com.kumoe.SeasonShop.init;

import com.kumoe.SeasonShop.content.shipping.*;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.MenuEntry;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class SeasonShopBlocks {


    public static BlockEntityEntry<ShopBlockEntity> SHOP_BE;
    public static MenuEntry<ShippingBinMenu> SHIPPING_BIN_BLOCK_MENU;
    public static MenuEntry<ShopMenu> SHOP_BLOCK_MENU;

    public static BlockEntry<ShippingBinBlock> SHIPPING_BIN_BLOCK;
    public static BlockEntry<ShopBlock> SHOP_BLOCK;
    public static BlockEntityEntry<ShippingBinBlockEntity> SHIPPING_BIN_BE;

    public static void register() {
        registerBlock();
        SeasonShop.getLogger().debug("Registering Season blocks...");
        registerMenu();
        SeasonShop.getLogger().debug("Registering Season menus...");
        registerBlockEntity();
        SeasonShop.getLogger().debug("Registering Season block entities...");
    }

    private static void registerBlockEntity() {
        SHIPPING_BIN_BE = SeasonShop.REGISTRATE.blockEntity("shipping_bin_be", ShippingBinBlockEntity::new).validBlock(SHIPPING_BIN_BLOCK).renderer(() -> ShippingBinRenderer::new).register();
        SHOP_BE = SeasonShop.REGISTRATE.blockEntity("shop_be", ShopBlockEntity::new).validBlock(SHOP_BLOCK).renderer(() -> ShopBlockRenderer::new).register();
    }

    private static void registerMenu() {
        SHIPPING_BIN_BLOCK_MENU = SeasonShop.REGISTRATE.menu("shipping_bin_block_menu", ShippingBinMenu::factory, () -> ShippingBinScreen::new).register();
        SHOP_BLOCK_MENU = SeasonShop.REGISTRATE.menu("shop_block_menu", ShopMenu::new, () -> ShopScreen::new).register();
    }

    private static void registerBlock() {
        SHIPPING_BIN_BLOCK = SeasonShop.REGISTRATE.block("shipping_bin", ShippingBinBlock::new)
                .properties((properties) -> BlockBehaviour.Properties.copy(Blocks.CHEST)).blockstate(ShippingBinBlock::buildModel).tag(BlockTags.MINEABLE_WITH_AXE).simpleItem().register();
        SHOP_BLOCK = SeasonShop.REGISTRATE.block("shop_block", ShopBlock::new)
                .properties((properties) -> BlockBehaviour.Properties.copy(Blocks.CHEST)).blockstate(ShopBlock::buildModel).tag(BlockTags.MINEABLE_WITH_AXE).simpleItem().register();
    }

}
