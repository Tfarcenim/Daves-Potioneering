package tfar.davespotioneering.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tfar.davespotioneering.client.ClientEvents;
import tfar.davespotioneering.client.ClientHooks;
import tfar.davespotioneering.init.ModItems;

import java.lang.ref.WeakReference;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Shadow @Final private ItemModels models;

    @ModifyVariable(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V"
    ,at = @At(value = "INVOKE",target = "Lnet/minecraft/client/render/model/BakedModel;getTransformation()Lnet/minecraft/client/render/model/json/ModelTransformation;"), argsOnly = true)
    private BakedModel modified(BakedModel model, ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model1) {
        return ClientHooks.modifyModel(model, stack,renderMode,leftHanded,matrices,vertexConsumers,light,overlay,this.models);
    }

    @Inject(method = "getModel",at = @At("HEAD"))
    private void captureModels(ItemStack itemStack, World world, LivingEntity livingEntity, int i, CallbackInfoReturnable<BakedModel> cir) {
        ClientEvents.itemStack = itemStack;
        ClientEvents.level = new WeakReference<>(world);
        ClientEvents.player = new WeakReference<>(livingEntity);
        ClientEvents.seed = i;
    }

    @ModifyVariable(
            method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
            at = @At(value = "INVOKE",target = "Lnet/minecraft/client/render/item/ItemRenderer;renderGuiQuad(Lnet/minecraft/client/render/BufferBuilder;IIIIIIII)V",ordinal = 0),ordinal = 3)
    private int changeColor(int j, TextRenderer renderer, ItemStack stack, int x, int y, @Nullable String countLabel) {
        if (stack.getItem() == ModItems.POTIONEER_GAUNTLET) {
            return 0xff8000;
        }
        return j;
    }
}
