package com.kumoe.SeasonShop.content.shipping;


import com.kumoe.SeasonShop.init.SeasonShop;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShippingBinScreen extends AbstractContainerScreen<ShippingBinMenu> {
    static final ResourceLocation SELL_SHOP_LOCATION = new ResourceLocation(SeasonShop.MODID, "textures/gui/shipping_bin.png");
    final Inventory playerInventory;
    protected ShippingBinMenu menu;
    protected AbstractShippingBinBlockEntity container;
    protected Player player;

    public ShippingBinScreen(ShippingBinMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 187;
        this.imageHeight = 188;
        this.playerInventory = pPlayerInventory;
        this.player = pPlayerInventory.player;
        this.menu = pMenu;
        this.container = pMenu.getContainer();
    }

    @Override
    protected void init() {
        super.init();
//        ImageButton button = new ImageButton(
//                 (this.width - this.imageWidth - 10) / 2 + 143, (this.height - this.imageHeight - 2) / 2 + 50, 16, 16, this.imageWidth, 0, 16,
//                 SELL_SHOP_LOCATION, (pOnPress) -> this.getMinecraft().setScreen(new ShippingShopScreen(shopMenu, this.playerInventory, this.title)));
//
//        this.addRenderableWidget(button);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        this.inventoryLabelY = this.imageHeight - 98;
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        pGuiGraphics.drawString(this.font, this.title, this.titleLabelX + this.imageWidth - 60, this.titleLabelY, 4210752, true);
        pGuiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, true);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = (this.width - this.imageWidth - 10) / 2;
        int y = (this.height - this.imageHeight - 2) / 2;
        guiGraphics.blit(SELL_SHOP_LOCATION, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public ShippingBinMenu getMenu() {


        return this.menu;
    }
}
