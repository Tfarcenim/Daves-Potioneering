package tfar.davespotioneering.client.model.gecko;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
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

public class GeoItemStackRenderer<T extends IAnimatable> extends BuiltinModelItemRenderer implements IGeoRenderer<T>, BuiltinItemRendererRegistry.DynamicItemRenderer  {

    private final AnimatedGeoModel<T> modelProvider;
    protected ItemStack currentItemStack;
    protected final Function<Identifier, RenderLayer> renderTypeGetter;
    private final T ianimatable;

    private static final Map<Item, GeoItemStackRenderer<?>> animatedRenderers = new ConcurrentHashMap<>();

    public GeoItemStackRenderer(AnimatedGeoModel<T> modelProvider, T ianimatable,BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelLoader entityModelLoader) {
        this(modelProvider, RenderLayer::getEntityCutout, ianimatable,blockEntityRenderDispatcher,entityModelLoader);
    }

    public GeoItemStackRenderer(AnimatedGeoModel<T> modelProvider, Function<Identifier, RenderLayer> renderTypeGetter, T ianimatable, BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelLoader entityModelLoader) {
        super(blockEntityRenderDispatcher,entityModelLoader);
        this.modelProvider = modelProvider;
        this.renderTypeGetter = renderTypeGetter;
        this.ianimatable = ianimatable;
    }

    public static void registerAnimatedItem(Item item, GeoItemStackRenderer<?> renderer) {
        animatedRenderers.put(item, renderer);
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
        this.currentItemStack = itemStack;
        GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(ianimatable));
        MinecraftClient mc = MinecraftClient.getInstance();
        AnimationEvent<T> itemEvent = new AnimationEvent<>(ianimatable, 0, 0, mc.getTickDelta(),
                false, Collections.singletonList(itemStack));
        modelProvider.setLivingAnimations(ianimatable, this.getUniqueID(ianimatable), itemEvent);
        matrices.push();
        matrices.translate(0, 0.01f, 0);
        matrices.translate(0.5, 0.5, 0.5);

      //  mc.textureManager.bind(getTextureLocation(ianimatable));
        Color renderColor = getRenderColor(ianimatable, 0, matrices, bufferIn, null, packedLightIn);
        RenderLayer renderType = getRenderType(ianimatable, 0, matrices, bufferIn, null, packedLightIn,
                getTextureLocation(ianimatable));

        // Our models often use single sided planes for fine detail
        RenderSystem.disableCull();

        // Get the Glint buffer if this item is enchanted
        VertexConsumer ivertexbuilder = ItemRenderer.getDirectItemGlintConsumer(bufferIn, renderType, true, currentItemStack.hasGlint());

        render(model, ianimatable, 0, renderType, matrices, null, ivertexbuilder, packedLightIn, OverlayTexture.DEFAULT_UV,
                (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
                (float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
        matrices.pop();
    }

    @Override
    public RenderLayer getRenderType(T animatable, float partialTicks, MatrixStack stack, @Nullable VertexConsumerProvider renderTypeBuffer, @Nullable VertexConsumer vertexBuilder, int packedLightIn, Identifier textureLocation) {
        return renderTypeGetter.apply(textureLocation);
    }

    @Override
    public AnimatedGeoModel<T> getGeoModelProvider() {
        return modelProvider;
    }

    @Override
    public Identifier getTextureLocation(T instance) {
        return this.modelProvider.getTextureLocation(instance);
    }

    public static class GeoItemModel<T extends IAnimatable> extends AnimatedGeoModel<T> {

        protected final Identifier animation;

        protected final Identifier modelLoc;
        protected final Identifier textureLoc;


        private static final Identifier DUMMY = new Identifier(DavesPotioneering.MODID, "animations/animation.dummy.json");

        public static GeoItemModel<IAnimatable> makeClosedUmbrella(DyeColor color) {
            return new GeoItemModel<>(new Identifier("closed_umbrella"),
                    new Identifier(DavesPotioneering.MODID,"closed_"+color.name().toLowerCase(Locale.ROOT)+"_umbrella"),DUMMY);
        }

        public static GeoItemModel<IAnimatable> makeOpenUmbrella(DyeColor color) {
            return new GeoItemModel<>(new Identifier("open_umbrella"),
                    new Identifier(DavesPotioneering.MODID,"open_"+color.name().toLowerCase(Locale.ROOT)+"_umbrella"),DUMMY);
        }

        public static GeoItemModel<IAnimatable> makeClosedUmbrella(String color) {
            return new GeoItemModel<>(new Identifier("closed_umbrella"),
                    new Identifier(DavesPotioneering.MODID,"closed_"+color+"_umbrella"),DUMMY);
        }

        public static GeoItemModel<IAnimatable> makeOpenUmbrella(String color) {
            return new GeoItemModel<>(new Identifier("open_umbrella"),
                    new Identifier(DavesPotioneering.MODID,"open_"+color+"_umbrella"),DUMMY);
        }

        public static GeoItemModel<IAnimatable> makeOpenAgedUmbrella() {
            return new GeoItemModel<>(new Identifier("open_aged_umbrella"),
                    new Identifier(DavesPotioneering.MODID,"open_aged_umbrella"),DUMMY);
        }

        public GeoItemModel(Identifier item) {
            this(item, DUMMY);
        }

        public GeoItemModel(Identifier item, Identifier animation) {
            this(item,item,animation);
        }

        public GeoItemModel(Identifier model, Identifier texture,Identifier animation) {
            this.animation = animation;
            modelLoc = new Identifier(DavesPotioneering.MODID, "geo/item/" + model.getPath() + ".geo.json");
            textureLoc = new Identifier(DavesPotioneering.MODID, "textures/item/" + texture.getPath() + ".png");
        }

        @Override
        public Identifier getModelLocation(T object) {
            return modelLoc;
        }

        @Override
        public Identifier getTextureLocation(T object) {
            return textureLoc;
        }

        @Override
        public Identifier getAnimationFileLocation(T animatable) {
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
