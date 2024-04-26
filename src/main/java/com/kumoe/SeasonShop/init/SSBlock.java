package com.kumoe.SeasonShop.init;

import com.kumoe.SeasonShop.content.shipping.*;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.MenuEntry;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class SSBlock {


    public static MenuEntry<ShippingBinMenu> SHIPPING_BIN_BLOCK_MENU = SeasonShop.REGISTRATE.menu("shipping_bin_block_menu", ShippingBinMenu::new, () -> ShippingBinScreen::new).register();

    public static void register() {
        SeasonShop.LOGGER.debug("Registering Season blocks...");
    }

    public static BlockEntry<ShippingBinBlock> SHIPPING_BIN_BLOCK = SeasonShop.REGISTRATE.block("shipping_bin", ShippingBinBlock::new).properties((properties) -> BlockBehaviour.Properties.copy(Blocks.CHEST)).blockstate(ShippingBinBlock::buildModel).tag(BlockTags.MINEABLE_WITH_AXE).simpleItem().register();
    public static BlockEntityEntry<ShippingBinBlockEntity> SHIPPING_BIN_BLOCK_BE = SeasonShop.REGISTRATE.blockEntity("shipping_bin_block_entity", ShippingBinBlockEntity::new).validBlock(SHIPPING_BIN_BLOCK).renderer(() -> ShippingBinRenderer::new).register();

}
