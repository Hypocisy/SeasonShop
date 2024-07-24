package com.kumoe.SeasonShop.content.block.beRenderer;

import com.kumoe.SeasonShop.content.block.ShopBlock;
import com.kumoe.SeasonShop.content.block.entity.ShopBlockEntity;
import com.kumoe.SeasonShop.init.SeasonShop;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.state.BlockState;

public class ShopBlockRenderer extends AbstractShopRenderer<ShopBlockEntity> {

    private static final ResourceLocation SINGLE_CHEST = new ResourceLocation(SeasonShop.MODID, "entity/chest/shop_block");
    private static final ResourceLocation LEFT_CHEST = new ResourceLocation(SeasonShop.MODID, "entity/chest/shop_block_left");
    private static final ResourceLocation RIGHT_CHEST = new ResourceLocation(SeasonShop.MODID, "entity/chest/shop_block_right");

    public ShopBlockRenderer(BlockEntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public Material getSingleMaterial() {
        return new Material(Sheets.CHEST_SHEET, SINGLE_CHEST);
    }

    @Override
    public Material getLeftMaterial() {
        return new Material(Sheets.CHEST_SHEET, LEFT_CHEST);
    }

    @Override
    public Material getRightMaterial() {
        return new Material(Sheets.CHEST_SHEET, RIGHT_CHEST);
    }

    @Override
    public boolean isSupportedBlock(Block pBlock) {
        return pBlock instanceof ShopBlock;
    }

    @Override
    protected DoubleBlockCombiner.NeighborCombineResult<? extends ShopBlockEntity> getCombineResult(BlockState blockState, Level level, ShopBlockEntity blockEntity) {
        return ((ShopBlock) blockState.getBlock()).combine(blockState, level, blockEntity.getBlockPos(), true);
    }

    @Override
    protected DoubleBlockCombiner.Combiner<ShopBlockEntity, Float2FloatFunction> getOpennessCombiner(ShopBlockEntity blockEntity) {
        return ShopBlock.opennesscombiner(blockEntity);
    }
}
