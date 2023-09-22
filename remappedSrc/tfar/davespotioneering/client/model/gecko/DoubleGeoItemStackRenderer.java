package tfar.davespotioneering.client.model.gecko;

import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import java.util.function.Function;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;

public class DoubleGeoItemStackRenderer<T extends IAnimatable> extends GeoItemStackRenderer<T> {

    private final AnimatedGeoModel<T> modelProvider2;

    public DoubleGeoItemStackRenderer(AnimatedGeoModel<T> modelProvider1,AnimatedGeoModel<T> modelProvider2, T ianimatable, BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelSet entityModelLoader) {
        this(modelProvider1, RenderType::entityCutout, ianimatable,blockEntityRenderDispatcher,entityModelLoader,modelProvider2);
    }

    public DoubleGeoItemStackRenderer(AnimatedGeoModel<T> modelProvider1, Function<ResourceLocation, RenderType> renderTypeGetter, T ianimatable, BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelSet entityModelLoader, AnimatedGeoModel<T> modelProvider2) {
        super(modelProvider1,renderTypeGetter,ianimatable,blockEntityRenderDispatcher,entityModelLoader);
        this.modelProvider2 = modelProvider2;
    }

    public static final ThreadLocal<Float> override = ThreadLocal.withInitial(() -> 0f);

    @Override
    public AnimatedGeoModel<T> getGeoModelProvider() {
        return override.get() == 1 ? modelProvider2 : modelProvider;
    }
}
