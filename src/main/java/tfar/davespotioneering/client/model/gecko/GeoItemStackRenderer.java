package tfar.davespotioneering.client.model.gecko;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;
import software.bernie.geckolib3.util.EModelRenderCycle;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.function.Function;

public class GeoItemStackRenderer<T extends Item & IAnimatable> extends GeoItemRenderer<T> {
    protected final Function<ResourceLocation, RenderType> renderTypeGetter;

    public GeoItemStackRenderer(AnimatedGeoModel<T> modelProvider) {
        this(modelProvider, RenderType::entityCutout);
    }

    public GeoItemStackRenderer(AnimatedGeoModel<T> modelProvider, Function<ResourceLocation, RenderType> renderTypeGetter) {
        super(modelProvider);
        this.renderTypeGetter = renderTypeGetter;
    }

    //this needs to be overridden to delegate model provider
    public void render(T animatable, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                       ItemStack stack) {
        this.currentItemStack = stack;
        GeoModel model = this.getGeoModelProvider().getModel(this.getGeoModelProvider().getModelResource(animatable));
        AnimationEvent<T> animationEvent = new AnimationEvent<>(animatable, 0, 0, Minecraft.getInstance().getFrameTime(), false, Collections.singletonList(stack));
        this.dispatchedMat = poseStack.last().pose().copy();

        setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
        this.getGeoModelProvider().setCustomAnimations(animatable, getInstanceId(animatable), animationEvent);
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.51f, 0.5f);

        RenderSystem.setShaderTexture(0, getTextureLocation(animatable));
        Color renderColor = getRenderColor(animatable, 0, poseStack, bufferSource, null, packedLight);
        RenderType renderType = getRenderType(animatable, 0, poseStack, bufferSource, null, packedLight,
                getTextureLocation(animatable));
        render(model, animatable, 0, renderType, poseStack, bufferSource, null, packedLight, OverlayTexture.NO_OVERLAY,
                renderColor.getRed() / 255f, renderColor.getGreen() / 255f,
                renderColor.getBlue() / 255f, renderColor.getAlpha() / 255f);
        poseStack.popPose();
    }

    @Override
    public RenderType getRenderType(T animatable, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return renderTypeGetter.apply(textureLocation);
    }

    @Override
    public ResourceLocation getTextureLocation(T instance) {
        return this.getGeoModelProvider().getTextureResource(instance);
    }
}
