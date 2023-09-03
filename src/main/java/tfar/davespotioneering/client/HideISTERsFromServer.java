package tfar.davespotioneering.client;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.DyeColor;
import tfar.davespotioneering.client.model.gecko.DoubleGeoItemStackRenderer;
import tfar.davespotioneering.client.model.gecko.GeoItemModel;

import javax.annotation.Nonnull;
import java.util.Locale;

public class HideISTERsFromServer {

    public static BlockEntityWithoutLevelRenderer createGeoClassicUmbrellaItemStackRenderer(String itemName) {
        return new DoubleGeoItemStackRenderer<>(
                GeoItemModel.makeClosedUmbrella(itemName),
                GeoItemModel.makeOpenUmbrella(itemName));
    }

    @Nonnull
    public static BlockEntityWithoutLevelRenderer createAgedUmbrellaItemStackRenderer() {
        return new DoubleGeoItemStackRenderer<>(
                GeoItemModel.makeClosedUmbrella("aged"),
                GeoItemModel.makeOpenAgedUmbrella());
    }

}
