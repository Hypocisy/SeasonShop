package com.kumoe.SeasonShop.content.shipping;

import com.kumoe.SeasonShop.init.SSBlock;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiPredicate;

public class ShippingBinBlock extends ChestBlock {
    public static final EnumProperty<ChestType> TYPE = BlockStateProperties.CHEST_TYPE;

    private static final DoubleBlockCombiner.Combiner<ShippingBinBlockEntity, Optional<MenuProvider>> MENU_PROVIDER_COMBINER = new DoubleBlockCombiner.Combiner<>() {
        public Optional<MenuProvider> acceptDouble(final ShippingBinBlockEntity chestBlockEntity1, final ShippingBinBlockEntity chestBlockEntity2) {
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

        public Optional<MenuProvider> acceptSingle(ShippingBinBlockEntity chestBlockEntity) {
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
            if (entity instanceof AbstractShippingBinBlockEntity blockEntity) {
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
        return pLevel.isClientSide ? createTickerHelper(pBlockEntityType, SSBlock.SHIPPING_BIN_BLOCK_BE.get(), AbstractShippingBinBlockEntity::lidAnimateTick) : null;
    }

    public static DoubleBlockCombiner.Combiner<ShippingBinBlockEntity, Float2FloatFunction> opennesscombiner(final LidBlockEntity pLid) {
        return new DoubleBlockCombiner.Combiner<>() {
            @Override
            public Float2FloatFunction acceptDouble(ShippingBinBlockEntity pFirst, ShippingBinBlockEntity pSecond) {
                return (pPartialTicks) -> Math.max(pFirst.getOpenNess(pPartialTicks), pSecond.getOpenNess(pPartialTicks));
            }

            public Float2FloatFunction acceptSingle(ShippingBinBlockEntity pSingle) {
                return pSingle::getOpenNess;
            }

            public Float2FloatFunction acceptNone() {
                return pLid::getOpenNess;
            }
        };
    }

    public DoubleBlockCombiner.NeighborCombineResult<? extends ShippingBinBlockEntity> combine(BlockState pState, Level pLevel, BlockPos pPos, boolean pOverride) {
        BiPredicate<LevelAccessor, BlockPos> bipredicate = pOverride ? ((levelAccessor, blockPos) -> false) : ShippingBinBlock::isChestBlockedAt;

        return DoubleBlockCombiner.combineWithNeigbour(SSBlock.SHIPPING_BIN_BLOCK_BE.get(), ShippingBinBlock::getBlockType, ShippingBinBlock::getConnectedDirection, FACING, pState, pLevel, pPos, bipredicate);
    }
    public static DoubleBlockCombiner.BlockType getBlockType(BlockState blockState) {
        ChestType chesttype = blockState.getValue(TYPE);
        if (chesttype == ChestType.SINGLE) {
            return DoubleBlockCombiner.BlockType.SINGLE;
        } else {
            return chesttype == ChestType.RIGHT ? DoubleBlockCombiner.BlockType.FIRST : DoubleBlockCombiner.BlockType.SECOND;
        }
    }

    @Nullable
    @Override
    public MenuProvider getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos) {
        return this.combine(pState, pLevel, pPos, false).apply(MENU_PROVIDER_COMBINER).orElse(null);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        if (blockentity instanceof AbstractShippingBinBlockEntity blockEntity) {
            blockEntity.recheckOpen();
        }

    }


    @Nullable
    @Override
    public AbstractShippingBinBlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return SSBlock.SHIPPING_BIN_BLOCK_BE.get().create(blockPos, blockState);
    }

}
