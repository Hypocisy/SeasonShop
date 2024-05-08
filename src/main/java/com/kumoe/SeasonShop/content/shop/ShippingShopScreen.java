package com.kumoe.SeasonShop.content.shop;

import com.kumoe.SeasonShop.init.SeasonShop;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import sereneseasons.handler.season.SeasonHandler;
import sereneseasons.season.SeasonTime;

public class ShippingShopScreen extends AbstractContainerScreen<ShopMenu> {
    static final ResourceLocation SELL_SHOP_LOCATION = new ResourceLocation(SeasonShop.MODID, "textures/gui/sell_shop.png");
    protected final ShopMenu menu;
    protected final Inventory playerInventory;

    public ShippingShopScreen(ShopMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 189;
        this.imageHeight = 104;
        this.menu = pMenu;
        this.playerInventory = pPlayerInventory;
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        this.titleLabelY = this.titleLabelY - 20;
        this.inventoryLabelY = this.imageHeight - 20;

        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    /**
     * only use to get synced client season data
     */
    public int getSeasonIconVOffset() {
        SeasonTime time = SeasonHandler.getClientSeasonTime();
        return switch (time.getSeason()) {
            case SUMMER -> 13;
            case AUTUMN -> 26;
            case WINTER -> 39;
            default -> 0;
        };
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        pGuiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = (this.width - this.imageWidth - 10) / 2;
        int y = (this.height - this.imageHeight - 2) / 2 - 20;
        guiGraphics.pose().pushPose();
        guiGraphics.blit(SELL_SHOP_LOCATION, x, y, 0, 0, 189, 104);
        guiGraphics.blit(SELL_SHOP_LOCATION, x + 87, y + 77, this.imageWidth, getSeasonIconVOffset(), 13, 13);
        guiGraphics.pose().popPose();
    }

    @Override
    public ShopMenu getMenu() {
        return this.menu;
    }
}