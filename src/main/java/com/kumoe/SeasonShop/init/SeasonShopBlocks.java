package com.kumoe.SeasonShop.init;

import com.kumoe.SeasonShop.content.block.ShippingBinBlock;
import com.kumoe.SeasonShop.content.block.ShopBlock;
import com.kumoe.SeasonShop.content.block.entity.ShippingBinBlockEntity;
import com.kumoe.SeasonShop.content.block.entity.ShopBlockEntity;
import com.kumoe.SeasonShop.content.block.renderer.ShippingBinRenderer;
import com.kumoe.SeasonShop.content.block.renderer.ShopBlockRenderer;
import com.kumoe.SeasonShop.content.menu.ShippingBinMenu;
import com.kumoe.SeasonShop.content.menu.ShopMenu;
import com.kumoe.SeasonShop.content.screen.ShippingBinScreen;
import com.kumoe.SeasonShop.content.screen.ShopScreen;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import sereneseasons.core.SereneSeasons;

public class SeasonShopBlocks {


    public static BlockEntityEntry<ShopBlockEntity> SHOP_BE;
    public static MenuEntry<ShippingBinMenu> SHIPPING_BIN_BLOCK_MENU;
    public static MenuEntry<ShopMenu> SHOP_BLOCK_MENU;

    public static BlockEntry<ShippingBinBlock> SHIPPING_BIN_BLOCK;
    public static BlockEntry<ShopBlock> SHOP_BLOCK;
    public static BlockEntityEntry<ShippingBinBlockEntity> SHIPPING_BIN_BE;
    public static RegistryEntry<CreativeModeTab> SEASON_SHOP_TAB;

    public static void register() {
        registerCreativeModTab();
        registerBlock();
        SeasonShop.logger().debug("Registering Season blocks...");
        registerMenu();
        SeasonShop.logger().debug("Registering Season menus...");
        registerBlockEntity();
        SeasonShop.logger().debug("Registering Season block entities...");
    }

    private static void registerCreativeModTab() {
        SEASON_SHOP_TAB = SeasonShop.REGISTRATE.object("season_shop").defaultCreativeTab(tab -> tab.withLabelColor(0xFF00AA32).withTabsAfter(SereneSeasons.CREATIVE_TAB_REGISTER.getRegistryName())).register();
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
                .properties((properties) -> BlockBehaviour.Properties.copy(Blocks.CHEST)).blockstate(ShippingBinBlock::buildModel).tag(BlockTags.MINEABLE_WITH_AXE).item().tab(SEASON_SHOP_TAB.getKey(), (ctx, modifier) -> modifier.accept(ctx)).build().register();
        SHOP_BLOCK = SeasonShop.REGISTRATE.block("shop_block", ShopBlock::new)
                .properties((properties) -> BlockBehaviour.Properties.copy(Blocks.CHEST)).blockstate(ShopBlock::buildModel).tag(BlockTags.MINEABLE_WITH_AXE).item().tab(SEASON_SHOP_TAB.getKey(), (ctx, modifier) -> modifier.accept(ctx)).build().register();
    }

}
