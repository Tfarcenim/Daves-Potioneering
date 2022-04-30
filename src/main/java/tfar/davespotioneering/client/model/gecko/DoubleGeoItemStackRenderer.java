package tfar.davespotioneering.client.model.gecko;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Collections;
import java.util.function.Function;

public class DoubleGeoItemStackRenderer<T extends IAnimatable> extends BlockEntityWithoutLevelRenderer implements IGeoRenderer<T> {

    private final AnimatedGeoModel<T> modelProvider1;
    private final AnimatedGeoModel<T> modelProvider2;

    protected final Function<ResourceLocation, RenderType> renderTypeGetter;
    private final T ianimatable;

    public DoubleGeoItemStackRenderer(AnimatedGeoModel<T> modelProvider1,AnimatedGeoModel<T> modelProvider2, T ianimatable) {
        this(modelProvider1,modelProvider2, RenderType::entityCutout, ianimatable);
    }

    public DoubleGeoItemStackRenderer(AnimatedGeoModel<T> modelProvider1,AnimatedGeoModel<T> modelProvider2, Function<ResourceLocation, RenderType> renderTypeGetter, T ianimatable) {
        this.modelProvider1 = modelProvider1;
        this.modelProvider2 = modelProvider2;
        this.renderTypeGetter = renderTypeGetter;
        this.ianimatable = ianimatable;
    }

    //render
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrices, MultiBufferSource bufferIn,
            int combinedLightIn,
            int p_239207_6_
    ) {
        if (transformType == ItemTransforms.TransformType.GUI) {
            matrices.pushPose();
            Minecraft mc = Minecraft.getInstance();
            MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
            Lighting.setupForFlatItems();
            this.render(matrices, bufferIn, combinedLightIn, stack);
            buffer.endBatch();
            RenderSystem.enableDepthTest();
            Lighting.setupFor3DItems();
            matrices.popPose();
        } else {
            this.render(matrices, bufferIn, combinedLightIn, stack);
        }
    }

    public void render(PoseStack matrices, MultiBufferSource bufferIn, int packedLightIn, ItemStack itemStack) {
        GeoModel model = getGeoModelProvider().getModel(getGeoModelProvider().getModelLocation(ianimatable));
        Minecraft mc = Minecraft.getInstance();
        AnimationEvent<T> itemEvent = new AnimationEvent<>(ianimatable, 0, 0, mc.getFrameTime(),
                false, Collections.singletonList(itemStack));

        getGeoModelProvider().setLivingAnimations(ianimatable, this.getUniqueID(ianimatable), itemEvent);
        matrices.pushPose();
        matrices.translate(0, 0.01f, 0);
        matrices.translate(0.5, 0.5, 0.5);

        mc.textureManager.bind(getTextureLocation(ianimatable));
        Color renderColor = getRenderColor(ianimatable, 0, matrices, bufferIn, null, packedLightIn);
        RenderType renderType = getRenderType(ianimatable, 0, matrices, bufferIn, null, packedLightIn,
                getTextureLocation(ianimatable));

        // Our models often use single sided planes for fine detail
        RenderSystem.disableCull();

        // Get the Glint buffer if this item is enchanted
        VertexConsumer ivertexbuilder = ItemRenderer.getFoilBufferDirect(bufferIn, renderType, true, itemStack.hasFoil());

        render(model, ianimatable, 0, renderType, matrices, null, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY,
                (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
                (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
        matrices.popPose();
    }

    public static final ThreadLocal<Integer> override = ThreadLocal.withInitial(() -> 0);

    @Override
    public RenderType getRenderType(T animatable, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return renderTypeGetter.apply(textureLocation);
    }

    @Override
    public AnimatedGeoModel<T> getGeoModelProvider() {
        return override.get() == 1 ? modelProvider1 : modelProvider2;
    }

    @Override
    public ResourceLocation getTextureLocation(T instance) {
        return this.getGeoModelProvider().getTextureLocation(instance);
    }

}
