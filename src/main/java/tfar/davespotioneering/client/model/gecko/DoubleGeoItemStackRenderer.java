package tfar.davespotioneering.client.model.gecko;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import java.util.function.Function;

public class DoubleGeoItemStackRenderer<T extends IAnimatable> extends GeoItemStackRenderer<T> {
    private final AnimatedGeoModel<T> modelProvider2;

    public DoubleGeoItemStackRenderer(AnimatedGeoModel<T> modelProvider1,AnimatedGeoModel<T> modelProvider2, T ianimatable) {
        this(modelProvider1,modelProvider2, RenderType::getEntityCutout, ianimatable);
    }

    public DoubleGeoItemStackRenderer(AnimatedGeoModel<T> modelProvider1,AnimatedGeoModel<T> modelProvider2, Function<ResourceLocation, RenderType> renderTypeGetter, T ianimatable) {
        super(modelProvider1,renderTypeGetter,ianimatable);
        this.modelProvider2 = modelProvider2;
    }

    public static final ThreadLocal<Integer> override = ThreadLocal.withInitial(() -> 0);

    @Override
    public AnimatedGeoModel<T> getGeoModelProvider() {
        return override.get() == 1 ? modelProvider : modelProvider2;
    }

}
