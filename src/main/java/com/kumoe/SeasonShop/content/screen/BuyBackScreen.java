package com.kumoe.SeasonShop.content.screen;

import com.kumoe.SeasonShop.api.ModUtils;
import com.kumoe.SeasonShop.content.block.entity.BuyBackBlockEntity;
import com.kumoe.SeasonShop.content.menu.BuybackMenu;
import com.kumoe.SeasonShop.content.menu.CustomSlot;
import com.kumoe.SeasonShop.init.SeasonShop;
import com.kumoe.SeasonShop.network.C2SSellPlayerItem;
import com.kumoe.SeasonShop.network.NetworkHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

public class BuyBackScreen extends AbstractContainerScreen<BuybackMenu> {
    static final ResourceLocation SELL_SHOP_LOCATION = new ResourceLocation(SeasonShop.MODID, "textures/gui/sell_shop.png");
    protected final BuybackMenu menu;
    protected final Inventory playerInventory;
    protected final BuyBackBlockEntity container;
    protected final Season season;
    private final Player player;

    public BuyBackScreen(BuybackMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 189;
        this.imageHeight = 104;
        this.menu = pMenu;
        this.playerInventory = pPlayerInventory;
        this.player = pPlayerInventory.player;
        this.container = pMenu.getContainer();
        this.season = SeasonHelper.getSeasonState(player.level()).getSeason();
    }

    @Override
    protected void init() {
        super.init();
        int x = (this.width - this.imageWidth - 10) / 2;
        int y = (this.height - this.imageHeight - 2) / 2 - 20;
        ImageButton leftPageButton = new ImageButton(x + 28, y + 80, 20, 12, 0, this.imageHeight, 0, SELL_SHOP_LOCATION, pButton -> {
            // 如果当前是第0页就不让继续往前翻
            if (this.container.getCurrentPage() - 1 < 0) return;
            this.container.updateServerPage(this.container.getCurrentPage() - 1);
        });

        ImageButton rightPageButton = new ImageButton(x + this.imageWidth - 48, y + 80, 20, 12, 0, this.imageHeight + 12, 0, SELL_SHOP_LOCATION, pButton -> {
            // 如果当前是允许的最大页面，就不让继续翻
            if (this.container.getCurrentPage() > ModUtils.getSettingMap().size()) return;
            this.container.updateServerPage(this.container.getCurrentPage() + 1);
        });

        this.addRenderableWidget(leftPageButton);
        this.addRenderableWidget(rightPageButton);
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        this.titleLabelY = this.titleLabelY - 20;
        this.inventoryLabelY = this.imageHeight - 20;

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        pGuiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = (this.width - this.imageWidth - 10) / 2;
        int y = (this.height - this.imageHeight - 2) / 2 - 20;
        // sell shop background
        guiGraphics.blit(SELL_SHOP_LOCATION, x, y, 0, 0, 189, 104);
        // icon base on now's season

        guiGraphics.blit(SELL_SHOP_LOCATION, x + 87, y + 77, this.imageWidth, ModUtils.getSeasonIconVOffset(season), 13, 13);
        // player inventory background
        guiGraphics.blit(ShippingBinScreen.SHIPPING_BIN_GUI, x, y + 77 + 20, 0, 87, imageWidth, imageHeight);
    }

    @Override
    @NotNull
    public BuybackMenu getMenu() {
        return this.menu;
    }

    @Override
    protected void slotClicked(Slot pSlot, int pSlotId, int pMouseButton, ClickType pType) {
        if (pSlot instanceof CustomSlot && pType.equals(ClickType.PICKUP)) {

            int playerToSellItemId = playerInventory.findSlotMatchingItem(pSlot.getItem());
            // slot id == -1 means can't find item
            if (playerToSellItemId == -1) {
                return;
            }
            // todo check clicked slot, and sell items

            ItemStack playerToSellItem = playerInventory.getItem(playerToSellItemId);
            if (!playerToSellItem.isEmpty()) {
                NetworkHandler.getInstance().sendToServer(new C2SSellPlayerItem(player.getUUID(), container.getBlockPos(), pSlotId, pMouseButton));
            }
        }
        super.slotClicked(pSlot, pSlotId, pMouseButton, pType);
    }

}