package tfar.davespotioneering.client.model.gecko;

import net.minecraft.world.item.Item;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import tfar.davespotioneering.client.DavesPotioneeringClient;


public class DoubleGeoItemStackRenderer<T extends Item & GeoAnimatable> extends GeoItemRenderer<T> {

    private final GeoModel<T> modelProvider2;

    public DoubleGeoItemStackRenderer(GeoModel<T> modelProvider1, GeoModel<T> modelProvider2) {
        super(modelProvider1);
        this.modelProvider2 = modelProvider2;
    }

    public GeoModel<T> getGeoModel() {
        float override = DavesPotioneeringClient.computeBlockingOverride();
        return override > 0 ? modelProvider2 : model;
    }
}
