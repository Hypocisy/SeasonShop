package com.kumoe.SeasonShop.content.screen;

import com.kumoe.SeasonShop.api.ModUtils;
import com.kumoe.SeasonShop.content.menu.ShopMenu;
import com.kumoe.SeasonShop.init.SeasonShop;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class ShopScreen extends AbstractContainerScreen<ShopMenu> {
    static final ResourceLocation SELL_SHOP_LOCATION = new ResourceLocation(SeasonShop.MODID, "textures/gui/sell_shop.png");
    protected final ShopMenu menu;
    protected final Inventory playerInventory;

    public ShopScreen(ShopMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
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

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        pGuiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = (this.width - this.imageWidth - 10) / 2;
        int y = (this.height - this.imageHeight - 2) / 2 - 20;
        // render a sell shop background
        guiGraphics.blit(SELL_SHOP_LOCATION, x, y, 0, 0, 189, 104);
        // render an icon base on now's season
        guiGraphics.blit(SELL_SHOP_LOCATION, x + 87, y + 77, this.imageWidth, ModUtils.getSeasonIconVOffset(), 13, 13);
    }

    @Override
    @NotNull
    public ShopMenu getMenu() {
        return this.menu;
    }
}