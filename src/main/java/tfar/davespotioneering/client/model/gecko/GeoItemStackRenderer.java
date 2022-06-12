package tfar.davespotioneering.client.model.gecko;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;
import tfar.davespotioneering.DavesPotioneering;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class GeoItemStackRenderer<T extends IAnimatable> extends BlockEntityWithoutLevelRenderer implements IGeoRenderer<T> {

    protected final AnimatedGeoModel<T> modelProvider;
    protected ItemStack currentItemStack;
    protected final Function<ResourceLocation, RenderType> renderTypeGetter;
    private final T ianimatable;

    private static final Map<Item, GeoItemStackRenderer<?>> animatedRenderers = new ConcurrentHashMap<>();

    public GeoItemStackRenderer(AnimatedGeoModel<T> modelProvider, T ianimatable, BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_) {
        this(modelProvider, RenderType::entityCutout, ianimatable,p_172550_,p_172551_);
    }

    public GeoItemStackRenderer(AnimatedGeoModel<T> modelProvider, Function<ResourceLocation, RenderType> renderTypeGetter, T ianimatable, BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_) {
        super(p_172550_,p_172551_);
        this.modelProvider = modelProvider;
        this.renderTypeGetter = renderTypeGetter;
        this.ianimatable = ianimatable;
    }

    public static void registerAnimatedItem(Item item, GeoItemStackRenderer<?> renderer) {
        animatedRenderers.put(item, renderer);
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
        this.currentItemStack = itemStack;
        GeoModel model = getGeoModelProvider().getModel(getGeoModelProvider().getModelLocation(ianimatable));
        Minecraft mc = Minecraft.getInstance();
        AnimationEvent<T> itemEvent = new AnimationEvent<>(ianimatable, 0, 0, mc.getFrameTime(),
                false, Collections.singletonList(itemStack));
        getGeoModelProvider().setLivingAnimations(ianimatable, this.getUniqueID(ianimatable), itemEvent);
        matrices.pushPose();
        matrices.translate(0, 0.01f, 0);
        matrices.translate(0.5, 0.5, 0.5);

        RenderSystem.setShaderTexture(0,getTextureLocation(ianimatable));
        Color renderColor = getRenderColor(ianimatable, 0, matrices, bufferIn, null, packedLightIn);
        RenderType renderType = getRenderType(ianimatable, 0, matrices, bufferIn, null, packedLightIn,
                getTextureLocation(ianimatable));

        // Our models often use single sided planes for fine detail
        RenderSystem.disableCull();

        // Get the Glint buffer if this item is enchanted
        VertexConsumer ivertexbuilder = ItemRenderer.getFoilBufferDirect(bufferIn, renderType, true, currentItemStack.hasFoil());

        render(model, ianimatable, 0, renderType, matrices, null, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY,
                (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
                (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
        matrices.popPose();
    }

    @Override
    public RenderType getRenderType(T animatable, float partialTicks, PoseStack stack, @Nullable MultiBufferSource renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return renderTypeGetter.apply(textureLocation);
    }

    @Override
    public AnimatedGeoModel<T> getGeoModelProvider() {
        return modelProvider;
    }

    @Override
    public ResourceLocation getTextureLocation(T instance) {
        return this.getGeoModelProvider().getTextureLocation(instance);
    }

    public static class GeoItemModel<T extends IAnimatable> extends AnimatedGeoModel<T> {

        protected final ResourceLocation animation;

        protected final ResourceLocation modelLoc;
        protected final ResourceLocation textureLoc;


        private static final ResourceLocation DUMMY = new ResourceLocation(DavesPotioneering.MODID, "animations/animation.dummy.json");

        public static GeoItemModel<IAnimatable> makeClosedUmbrella(DyeColor color) {
            return new GeoItemModel<>(new ResourceLocation("closed_umbrella"),
                    new ResourceLocation(DavesPotioneering.MODID,"closed_"+color.name().toLowerCase(Locale.ROOT)+"_umbrella"),DUMMY);
        }

        public static GeoItemModel<IAnimatable> makeOpenUmbrella(DyeColor color) {
            return new GeoItemModel<>(new ResourceLocation("open_umbrella"),
                    new ResourceLocation(DavesPotioneering.MODID,"open_"+color.name().toLowerCase(Locale.ROOT)+"_umbrella"),DUMMY);
        }

        public static GeoItemModel<IAnimatable> makeClosedUmbrella(String color) {
            return new GeoItemModel<>(new ResourceLocation("closed_umbrella"),
                    new ResourceLocation(DavesPotioneering.MODID,"closed_"+color+"_umbrella"),DUMMY);
        }

        public static GeoItemModel<IAnimatable> makeOpenUmbrella(String color) {
            return new GeoItemModel<>(new ResourceLocation("open_umbrella"),
                    new ResourceLocation(DavesPotioneering.MODID,"open_"+color+"_umbrella"),DUMMY);
        }

        public static GeoItemModel<IAnimatable> makeOpenAgedUmbrella() {
            return new GeoItemModel<>(new ResourceLocation("open_aged_umbrella"),
                    new ResourceLocation(DavesPotioneering.MODID,"open_aged_umbrella"),DUMMY);
        }

        public GeoItemModel(ResourceLocation item) {
            this(item, DUMMY);
        }

        public GeoItemModel(ResourceLocation item, ResourceLocation animation) {
            this(item,item,animation);
        }

        public GeoItemModel(ResourceLocation model, ResourceLocation texture,ResourceLocation animation) {
            this.animation = animation;
            modelLoc = new ResourceLocation(DavesPotioneering.MODID, "geo/item/" + model.getPath() + ".geo.json");
            textureLoc = new ResourceLocation(DavesPotioneering.MODID, "textures/item/" + texture.getPath() + ".png");
        }

        @Override
        public ResourceLocation getModelLocation(T object) {
            return modelLoc;
        }

        @Override
        public ResourceLocation getTextureLocation(T object) {
            return textureLoc;
        }

        @Override
        public ResourceLocation getAnimationFileLocation(T animatable) {
            return animation;
        }
    }

    public static final IAnimatable NOTHING = new DummyAnimations();

    public static class DummyAnimations implements IAnimatable {

        AnimationFactory factory = new AnimationFactory(this);

        private DummyAnimations() {
        }

        @Override
        public void registerControllers(AnimationData data) {
            data.addAnimationController(
                    new AnimationController<>(this, "null", 0, this::predicate)
            );
        }

        protected <P extends IAnimatable> PlayState predicate(AnimationEvent<P> event) {
            return PlayState.STOP;
        }

        @Override
        public AnimationFactory getFactory() {
            return factory;
        }
    }
}
