package com.kumoe.SeasonShop.content.block;

import com.kumoe.SeasonShop.content.block.entity.BuyBackBlockEntity;
import com.kumoe.SeasonShop.init.SeasonShopBlocks;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiPredicate;

public class BuyBackBlock extends ChestBlock {
    static final DoubleBlockCombiner.Combiner<BuyBackBlockEntity, Optional<MenuProvider>> MENU_PROVIDER_COMBINER = new DoubleBlockCombiner.Combiner<>() {
        public Optional<MenuProvider> acceptDouble(final BuyBackBlockEntity chestBlockEntity1, final BuyBackBlockEntity chestBlockEntity2) {
            return Optional.of(new MenuProvider() {
                @Nullable
                @Override
                public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
                    return chestBlockEntity1.canOpen(pPlayer) && chestBlockEntity2.canOpen(pPlayer) ? SeasonShopBlocks.SHIPPING_BIN_BLOCK_MENU.create(pContainerId, pPlayerInventory) : null;
                }

                @Override
                public Component getDisplayName() {
                    if (chestBlockEntity1.hasCustomName()) {
                        return chestBlockEntity1.getDisplayName();
                    }
                    return chestBlockEntity2.hasCustomName() ? chestBlockEntity2.getDisplayName() : Component.translatable("container.chestDouble");
                }
            });
        }

        public Optional<MenuProvider> acceptSingle(BuyBackBlockEntity chestBlockEntity) {
            return Optional.of(chestBlockEntity);
        }

        public Optional<MenuProvider> acceptNone() {
            return Optional.empty();
        }
    };


    public BuyBackBlock(Properties pProperties) {
        super(pProperties, SeasonShopBlocks.BUYBACK_BE::get);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TYPE, ChestType.SINGLE).setValue(WATERLOGGED, false));
    }

    public static void buildModel(DataGenContext<Block, BuyBackBlock> ctx, RegistrateBlockstateProvider pvd) {
        var single = pvd.models().getBuilder("block/buyback_block")
                .parent(new ModelFile.UncheckedModelFile(pvd.modLoc("custom/buyback_block")))
                .texture("single", pvd.modLoc("block/buyback_block"))
                .renderType("cutout");

        var left = pvd.models().getBuilder("block/buyback_block_left")
                .parent(new ModelFile.UncheckedModelFile(pvd.modLoc("custom/buyback_block_left")))
                .texture("left", pvd.modLoc("block/buyback_block_left"))
                .renderType("cutout");

        var right = pvd.models().getBuilder("block/buyback_block_right")
                .parent(new ModelFile.UncheckedModelFile(pvd.modLoc("custom/buyback_block_right")))
                .texture("right", pvd.modLoc("block/buyback_block_right"))
                .renderType("cutout");

        pvd.horizontalBlock(ctx.get(), state -> switch (state.getValue(TYPE)) {
            case SINGLE -> single;
            case LEFT -> left;
            case RIGHT -> right;
        });
    }


    public static DoubleBlockCombiner.Combiner<BuyBackBlockEntity, Float2FloatFunction> opennesscombiner(final LidBlockEntity pLid) {
        return new DoubleBlockCombiner.Combiner<>() {
            @Override
            public Float2FloatFunction acceptDouble(BuyBackBlockEntity pFirst, BuyBackBlockEntity pSecond) {
                return (pPartialTicks) -> Math.max(pFirst.getOpenNess(pPartialTicks), pSecond.getOpenNess(pPartialTicks));
            }

            public Float2FloatFunction acceptSingle(BuyBackBlockEntity pSingle) {
                return pSingle::getOpenNess;
            }

            public Float2FloatFunction acceptNone() {
                return pLid::getOpenNess;
            }
        };
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return super.getStateForPlacement(pContext);
    }


    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide() && pPlayer instanceof ServerPlayer serverPlayer) {
            if (pLevel.getBlockEntity(pPos) instanceof BuyBackBlockEntity blockEntity) {
                NetworkHooks.openScreen(serverPlayer, this.getMenuProvider(pState, pLevel, pPos), byteBuf -> {
                    byteBuf.writeBlockPos(pPos);
                    byteBuf.writeInt(blockEntity.getCurrentPage());
                });
                pPlayer.awardStat(this.getOpenChestStat());
                PiglinAi.angerNearbyPiglins(pPlayer, true);
            }
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide ? createTickerHelper(pBlockEntityType, SeasonShopBlocks.BUYBACK_BE.get(), BuyBackBlockEntity::lidAnimateTick) : null;
    }


    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {

    }

    public DoubleBlockCombiner.NeighborCombineResult<BuyBackBlockEntity> combine(BlockState pState, Level pLevel, BlockPos pPos, boolean pOverride) {
        BiPredicate<LevelAccessor, BlockPos> bipredicate = pOverride ? ((levelAccessor, blockPos) -> false) : BuyBackBlock::isChestBlockedAt;
        return DoubleBlockCombiner.combineWithNeigbour(SeasonShopBlocks.BUYBACK_BE.get(), BuyBackBlock::getBlockType, BuyBackBlock::getConnectedDirection, FACING, pState, pLevel, pPos, bipredicate);
    }

    @Nullable
    @Override
    public MenuProvider getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos) {
        return this.combine(pState, pLevel, pPos, false).apply(MENU_PROVIDER_COMBINER).orElse(null);
    }

    @Override
    public BuyBackBlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return SeasonShopBlocks.BUYBACK_BE.get().create(pPos, pState);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.hasBlockEntity() && (!pState.is(pNewState.getBlock()) || !pNewState.hasBlockEntity())) {
            pLevel.removeBlockEntity(pPos);
        }
    }
}
