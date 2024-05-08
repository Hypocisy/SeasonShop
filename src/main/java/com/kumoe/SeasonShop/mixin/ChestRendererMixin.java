package com.kumoe.SeasonShop.mixin;

import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChestRenderer.class)
public class ChestRendererMixin<T extends BlockEntity & LidBlockEntity> {


//    @Inject(method = "render(Lnet/minecraft/world/level/block/entity/BlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V", at = @At(value = "TAIL"))
//    public void renderMixin(T pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay, CallbackInfo ci) {
//        SeasonShop.LOGGER.debug("pPartialTick: " + pPartialTick);
//        SeasonShop.LOGGER.debug("i: " + pBlockEntity.getOpenNess(pPartialTick));
//    }
}
