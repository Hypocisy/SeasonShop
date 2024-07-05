package com.kumoe.SeasonShop.content.shipping;


import com.kumoe.SeasonShop.api.ModUtils;
import com.kumoe.SeasonShop.init.SeasonShop;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShippingBinScreen extends AbstractContainerScreen<ShippingBinMenu> {
    final ResourceLocation SHIPPING_BIN_GUI = new ResourceLocation(SeasonShop.MODID, "textures/gui/shipping_bin.png");
    final Inventory playerInventory;
    protected ShippingBinMenu menu;
    protected ShippingBinBlockEntity container;
    protected Player player;

    public ShippingBinScreen(ShippingBinMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 187;
        this.imageHeight = 188;
        this.playerInventory = pPlayerInventory;
        this.player = pPlayerInventory.player;
        this.menu = pMenu;
        this.container = pMenu.container;
    }

    @Override
    protected void init() {
        super.init();
        ImageButton button = new ImageButton((this.width - this.imageWidth - 10) / 2 + 143, (this.height - this.imageHeight - 2) / 2 + 50, 16, 16, this.imageWidth, 0, 16, SHIPPING_BIN_GUI,
                (pOnPress) -> player.closeContainer());
        this.addRenderableWidget(button);
    }

    protected void sellAllItems(){
        double itemValue = 0f;
        for (Slot slot: this.menu.slots){
            if (slot instanceof SeasonSlot){
                itemValue += ModUtils.getTotalItemPrice(slot.getItem());
                slot.getItem().setCount(0);
                slot.setChanged();
            }
        }
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
        pGuiGraphics.drawString(this.font, this.title, this.titleLabelX + this.imageWidth - 60, this.titleLabelY, 0x404040, true);
        pGuiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, true);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int x = (this.width - this.imageWidth - 10) / 2;
        int y = (this.height - this.imageHeight - 2) / 2;
        guiGraphics.blit(SHIPPING_BIN_GUI, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }
}
