package tfar.davespotioneering.client.model.gecko;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import java.util.function.Function;

public class DoubleGeoItemStackRenderer<T extends Item & IAnimatable> extends GeoItemStackRenderer<T> {

    private final AnimatedGeoModel<T> modelProvider2;

    public DoubleGeoItemStackRenderer(AnimatedGeoModel<T> modelProvider1, AnimatedGeoModel<T> modelProvider2) {
        this(modelProvider1, RenderType::entityCutout, modelProvider2);
    }

    public DoubleGeoItemStackRenderer(AnimatedGeoModel<T> modelProvider1, Function<ResourceLocation, RenderType> renderTypeGetter, AnimatedGeoModel<T> modelProvider2) {
        super(modelProvider1,renderTypeGetter);
        this.modelProvider2 = modelProvider2;
    }

    public static final ThreadLocal<Float> override = ThreadLocal.withInitial(() -> 1f);

    @Override
    public AnimatedGeoModel<T> getGeoModelProvider() {
        return override.get() == 1 ? modelProvider2 : modelProvider;
    }
}
