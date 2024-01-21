package tfar.davespotioneering.client.model.gecko;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import tfar.davespotioneering.client.ClientEvents;

import java.util.function.Function;

public class DoubleGeoItemStackRenderer<T extends IAnimatable> extends GeoItemStackRenderer<T> {

    private final AnimatedGeoModel<T> modelProvider2;

    public DoubleGeoItemStackRenderer(AnimatedGeoModel<T> modelProvider1,AnimatedGeoModel<T> modelProvider2, T ianimatable, BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelLoader entityModelLoader) {
        this(modelProvider1, RenderLayer::getEntityCutout, ianimatable,blockEntityRenderDispatcher,entityModelLoader,modelProvider2);
    }

    public DoubleGeoItemStackRenderer(AnimatedGeoModel<T> modelProvider1, Function<Identifier, RenderLayer> renderTypeGetter, T ianimatable, BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelLoader entityModelLoader, AnimatedGeoModel<T> modelProvider2) {
        super(modelProvider1,renderTypeGetter,ianimatable,blockEntityRenderDispatcher,entityModelLoader);
        this.modelProvider2 = modelProvider2;
    }

    @Override
    public AnimatedGeoModel<T> getGeoModelProvider() {
        float override = ClientEvents.computeBlockingOverride();
        return override > 0 ? modelProvider2 : modelProvider;
    }
}
