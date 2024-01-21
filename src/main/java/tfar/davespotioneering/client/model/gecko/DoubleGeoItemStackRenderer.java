package tfar.davespotioneering.client.model.gecko;

import net.minecraft.world.item.Item;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import tfar.davespotioneering.client.ClientEvents;

public class DoubleGeoItemStackRenderer<T extends Item & IAnimatable> extends GeoItemStackRenderer<T> {

    private final AnimatedGeoModel<T> modelProvider2;

    public DoubleGeoItemStackRenderer(AnimatedGeoModel<T> modelProvider1, AnimatedGeoModel<T> modelProvider2) {
        super(modelProvider1);
        this.modelProvider2 = modelProvider2;
    }


    @Override
    public AnimatedGeoModel<T> getGeoModelProvider() {
        float override = ClientEvents.computeBlockingOverride();
        return override > 0 ? modelProvider2 : modelProvider;
    }
}
