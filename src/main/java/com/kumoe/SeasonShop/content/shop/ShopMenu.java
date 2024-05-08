package com.kumoe.SeasonShop.content.shop;

import com.kumoe.SeasonShop.content.shipping.SeasonSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ShopMenu extends AbstractContainerMenu {
    protected final int containerRows = 3;
    protected final Inventory playerInventory;
    private final ShopBlockEntity container;

    public ShopMenu(MenuType<ShopMenu> pType, int pContainerId, Inventory pPlayerInventory, ShopBlockEntity pContainer) {
        super(pType, pContainerId);
        this.container = pContainer;
        this.playerInventory = pPlayerInventory;

//        int e = (this.containerRows - 4) * 18;
        int row;
        int slot;
        for (row = 0; row < this.containerRows; ++row) {
            for (slot = 0; slot < 9; ++slot) {
                this.addSlot(new Slot(pContainer, slot + row * 6, 8 + slot * 18, -2+row * 18));
            }
        }

        // add inv
//        for (row = 0; row < 3; ++row) {
//            for (slot = 0; slot < 9; ++slot) {
//                this.addSlot(new Slot(pPlayerInventory, slot + row * 9 + 9, 8 + slot * 18, 123 + row * 18 + e));
//            }
//        }
//
//        for (row = 0; row < 9; ++row) {
//            this.addSlot(new Slot(pPlayerInventory, row, 8 + row * 18, 179 + e));
//        }

    }

    public ShopMenu(MenuType<ShopMenu> menu, int windowId, Inventory playerInventory, @Nullable FriendlyByteBuf data) {
        this(menu, windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    static ShopBlockEntity getTileEntity(Inventory playerInventory, @Nullable FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");
        BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof ShopBlockEntity bin) {
            return bin;
        } else {
            throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
        }
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     *
     * @param pPlayer
     * @param pIndex
     */
    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    /**
     * Determines whether supplied player can use this container
     *
     * @param pPlayer
     */
    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return this.container.stillValid(pPlayer);
    }


}
