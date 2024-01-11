package tfar.davespotioneering.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import tfar.davespotioneering.client.ClientHooks;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Shadow @Final private ItemModelShaper itemModelShaper;

    @ModifyVariable(method = "render"
    ,at = @At(value = "INVOKE",target = "Lnet/minecraft/client/renderer/block/model/ItemTransforms;getTransform(Lnet/minecraft/world/item/ItemDisplayContext;)Lnet/minecraft/client/renderer/block/model/ItemTransform;"), argsOnly = true)
    private BakedModel modified(BakedModel model, ItemStack stack, ItemDisplayContext renderMode, boolean leftHanded, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, BakedModel model1) {
        return ClientHooks.modifyModel(model, stack,renderMode,leftHanded,matrices,vertexConsumers,light,overlay,this.itemModelShaper);
    }

   /* @ModifyVariable(
            method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
            at = @At(value = "INVOKE",target = "Lnet/minecraft/client/render/item/ItemRenderer;renderGuiQuad(Lnet/minecraft/client/render/BufferBuilder;IIIIIIII)V",ordinal = 0),ordinal = 3)
    private int changeColor(int j, Font renderer, ItemStack stack, int x, int y, @Nullable String countLabel) {
        if (stack.getItem() == ModItems.POTIONEER_GAUNTLET) {
            return 0xff8000;
        }
        return j;
    }*/
}
