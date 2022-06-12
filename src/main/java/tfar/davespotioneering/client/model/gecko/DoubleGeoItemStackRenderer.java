package tfar.davespotioneering.client.model.gecko;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import java.util.function.Function;

public class DoubleGeoItemStackRenderer<T extends IAnimatable> extends GeoItemStackRenderer<T> {

    private final AnimatedGeoModel<T> modelProvider2;

    protected final Function<ResourceLocation, RenderType> renderTypeGetter;

    public DoubleGeoItemStackRenderer(AnimatedGeoModel<T> modelProvider1,AnimatedGeoModel<T> modelProvider2, T ianimatable,BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_) {
        this(modelProvider1, RenderType::entityCutout, ianimatable,p_172550_,p_172551_,modelProvider2);
    }

    public DoubleGeoItemStackRenderer(AnimatedGeoModel<T> modelProvider1,  Function<ResourceLocation, RenderType> renderTypeGetter, T ianimatable, BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_,AnimatedGeoModel<T> modelProvider2) {
        super(modelProvider1,renderTypeGetter,ianimatable,p_172550_,p_172551_);
        this.modelProvider2 = modelProvider2;
        this.renderTypeGetter = renderTypeGetter;
    }

    public static final ThreadLocal<Float> override = ThreadLocal.withInitial(() -> 0f);

    @Override
    public AnimatedGeoModel<T> getGeoModelProvider() {
        return override.get() == 1 ? modelProvider2 : modelProvider;
    }
}
