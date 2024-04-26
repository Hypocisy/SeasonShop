package com.kumoe.SeasonShop.content.shipping;

import com.kumoe.SeasonShop.init.SSBlock;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiPredicate;

public class ShippingBinBlock extends ChestBlock {

    private static final DoubleBlockCombiner.Combiner<ChestBlockEntity, Optional<MenuProvider>> MENU_PROVIDER_COMBINER = new DoubleBlockCombiner.Combiner<>() {
        public Optional<MenuProvider> acceptDouble(final ChestBlockEntity chestBlockEntity1, final ChestBlockEntity chestBlockEntity2) {
            final Container container = new CompoundContainer(chestBlockEntity1, chestBlockEntity2);
            return Optional.of(new MenuProvider() {
                @Nullable
                public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {

                    if (chestBlockEntity1.canOpen(pPlayer) && chestBlockEntity2.canOpen(pPlayer)) {
                        return SSBlock.SHIPPING_BIN_BLOCK_MENU.create(pContainerId, pPlayerInventory);
                    }
                    return null;
                }

                public Component getDisplayName() {
                    if (chestBlockEntity1.hasCustomName()) {
                        return chestBlockEntity1.getDisplayName();
                    }
                    return chestBlockEntity2.hasCustomName() ? chestBlockEntity2.getDisplayName() : Component.translatable("container.chestDouble");
                }
            });
        }

        public Optional<MenuProvider> acceptSingle(ChestBlockEntity chestBlockEntity) {
            return Optional.of(chestBlockEntity);
        }

        public Optional<MenuProvider> acceptNone() {
            return Optional.empty();
        }
    };

    public ShippingBinBlock(Properties pProperties) {
        super(pProperties, SSBlock.SHIPPING_BIN_BLOCK_BE::get);
    }

    public static void buildModel(DataGenContext<Block, ShippingBinBlock> ctx, RegistrateBlockstateProvider pvd) {
        var single = pvd.models().getBuilder("block/shipping_bin")
                .parent(new ModelFile.UncheckedModelFile(pvd.modLoc("custom/block/shipping_bin")))
                .texture("single", "block/shipping_bin")
                .renderType("cutout");
        var left = pvd.models().getBuilder("block/shipping_bin_left")
                .parent(new ModelFile.UncheckedModelFile(pvd.modLoc("custom/block/shipping_bin_left")))
                .texture("left", "block/shipping_bin_large")
                .renderType("cutout");
        var right = pvd.models().getBuilder("block/shipping_bin_right")
                .parent(new ModelFile.UncheckedModelFile(pvd.modLoc("custom/block/shipping_bin_right")))
                .texture("right", "block/shipping_bin_large")
                .renderType("cutout");

        pvd.horizontalBlock(ctx.get(), state -> switch (state.getValue(TYPE)) {
            case SINGLE -> single;
            case LEFT -> left;
            case RIGHT -> right;
        });
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if (entity instanceof ShippingBinBlockEntity blockEntity) {
                NetworkHooks.openScreen((ServerPlayer) pPlayer, blockEntity, pPos);
                pPlayer.awardStat(this.getOpenChestStat());
                PiglinAi.angerNearbyPiglins(pPlayer, true);
            }
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide ? createTickerHelper(pBlockEntityType, SSBlock.SHIPPING_BIN_BLOCK_BE.get(), ShippingBinBlockEntity::lidAnimateTick) : null;
    }

    @Nullable
    @Override
    public MenuProvider getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos) {
        return this.combine(pState, pLevel, pPos, false).apply(MENU_PROVIDER_COMBINER).orElse(null);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        if (blockentity instanceof ShippingBinBlockEntity blockEntity) {
            blockEntity.recheckOpen();
        }

    }


    @Nullable
    @Override
    public ShippingBinBlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return SSBlock.SHIPPING_BIN_BLOCK_BE.get().create(blockPos, blockState);
    }

}
