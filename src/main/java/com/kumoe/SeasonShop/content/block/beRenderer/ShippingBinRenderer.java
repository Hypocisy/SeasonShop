package com.kumoe.SeasonShop.content.block.beRenderer;

import com.kumoe.SeasonShop.content.block.ShippingBinBlock;
import com.kumoe.SeasonShop.content.block.entity.ShippingBinBlockEntity;
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

public class ShippingBinRenderer extends AbstractShopRenderer<ShippingBinBlockEntity> {
    private static final ResourceLocation SINGLE_CHEST = new ResourceLocation(SeasonShop.MODID, "entity/chest/shipping_bin");
    private static final ResourceLocation LEFT_CHEST = new ResourceLocation(SeasonShop.MODID, "entity/chest/shipping_bin_left");
    private static final ResourceLocation RIGHT_CHEST = new ResourceLocation(SeasonShop.MODID, "entity/chest/shipping_bin_right");

    public ShippingBinRenderer(BlockEntityRendererProvider.Context pContext) {
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
        return pBlock instanceof ShippingBinBlock;
    }

    @Override
    protected DoubleBlockCombiner.NeighborCombineResult<? extends ShippingBinBlockEntity> getCombineResult(BlockState blockState, Level level, ShippingBinBlockEntity blockEntity) {
        return ((ShippingBinBlock) blockState.getBlock()).combine(blockState, level, blockEntity.getBlockPos(), true);
    }

    @Override
    protected DoubleBlockCombiner.Combiner<ShippingBinBlockEntity, Float2FloatFunction> getOpennessCombiner(ShippingBinBlockEntity blockEntity) {
        return ShippingBinBlock.opennesscombiner(blockEntity);
    }

}
