package tfar.davespotioneering.client.model.gecko;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.function.Function;

public class DoubleGeoItemStackRenderer<T extends IAnimatable> extends BuiltinModelItemRenderer implements IGeoRenderer<T>, BuiltinItemRendererRegistry.DynamicItemRenderer {

    private final AnimatedGeoModel<T> modelProvider1;
    private final AnimatedGeoModel<T> modelProvider2;

    protected final Function<Identifier, RenderLayer> renderTypeGetter;
    private final T ianimatable;

    public DoubleGeoItemStackRenderer(AnimatedGeoModel<T> modelProvider1,AnimatedGeoModel<T> modelProvider2, T ianimatable) {
        this(modelProvider1,modelProvider2, RenderLayer::getEntityCutout, ianimatable);
    }

    public DoubleGeoItemStackRenderer(AnimatedGeoModel<T> modelProvider1,AnimatedGeoModel<T> modelProvider2, Function<Identifier, RenderLayer> renderTypeGetter, T ianimatable) {
        this.modelProvider1 = modelProvider1;
        this.modelProvider2 = modelProvider2;
        this.renderTypeGetter = renderTypeGetter;
        this.ianimatable = ianimatable;
    }

    //render
    public void render(ItemStack stack, ModelTransformation.Mode transformType, MatrixStack matrices, VertexConsumerProvider bufferIn,
            int combinedLightIn,
            int p_239207_6_
    ) {
        if (transformType == ModelTransformation.Mode.GUI) {
            matrices.push();
            MinecraftClient mc = MinecraftClient.getInstance();
            VertexConsumerProvider.Immediate buffer = mc.getBufferBuilders().getEntityVertexConsumers();
            DiffuseLighting.disableGuiDepthLighting();
            this.render(matrices, bufferIn, combinedLightIn, stack);
            buffer.draw();
            RenderSystem.enableDepthTest();
            DiffuseLighting.enableGuiDepthLighting();
            matrices.pop();
        } else {
            this.render(matrices, bufferIn, combinedLightIn, stack);
        }
    }

    public void render(MatrixStack matrices, VertexConsumerProvider bufferIn, int packedLightIn, ItemStack itemStack) {
        GeoModel model = getGeoModelProvider().getModel(getGeoModelProvider().getModelLocation(ianimatable));
        MinecraftClient mc = MinecraftClient.getInstance();
        AnimationEvent<T> itemEvent = new AnimationEvent<>(ianimatable, 0, 0, mc.getTickDelta(),
                false, Collections.singletonList(itemStack));

        getGeoModelProvider().setLivingAnimations(ianimatable, this.getUniqueID(ianimatable), itemEvent);
        matrices.push();
        matrices.translate(0, 0.01f, 0);
        matrices.translate(0.5, 0.5, 0.5);

        mc.getTextureManager().bindTexture(getTextureLocation(ianimatable));
        Color renderColor = getRenderColor(ianimatable, 0, matrices, bufferIn, null, packedLightIn);
        RenderLayer renderType = getRenderType(ianimatable, 0, matrices, bufferIn, null, packedLightIn,
                getTextureLocation(ianimatable));

        // Our models often use single sided planes for fine detail
        RenderSystem.disableCull();

        // Get the Glint buffer if this item is enchanted
        VertexConsumer ivertexbuilder = ItemRenderer.getDirectItemGlintConsumer(bufferIn, renderType, true, itemStack.hasGlint());

        render(model, ianimatable, 0, renderType, matrices, null, ivertexbuilder, packedLightIn, OverlayTexture.DEFAULT_UV,
                (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
                (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
        matrices.pop();
    }

    public static final ThreadLocal<Integer> override = ThreadLocal.withInitial(() -> 0);

    @Override
    public RenderLayer getRenderType(T animatable, float partialTicks, MatrixStack stack, @Nullable VertexConsumerProvider renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        return renderTypeGetter.apply(textureLocation);
    }

    @Override
    public AnimatedGeoModel<T> getGeoModelProvider() {
        return override.get() == 1 ? modelProvider1 : modelProvider2;
    }

    @Override
    public Identifier getTextureLocation(T instance) {
        return this.getGeoModelProvider().getTextureLocation(instance);
    }

}
