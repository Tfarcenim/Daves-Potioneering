package tfar.davespotioneering.client.model.gecko;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import tfar.davespotioneering.DavesPotioneeringFabric;

import java.util.Locale;

public class GeoItemModel<T extends Item & GeoAnimatable> extends GeoModel<T> {

    protected final ResourceLocation animation;

    protected final ResourceLocation modelLoc;
    protected final ResourceLocation textureLoc;


    private static final ResourceLocation DUMMY = new ResourceLocation(DavesPotioneeringFabric.MODID, "animations/animation.dummy.json");

    public static <T extends Item & GeoAnimatable>GeoItemModel<T> makeClosedUmbrella(DyeColor color) {
        return new GeoItemModel<>(new ResourceLocation("closed_umbrella"),
                new ResourceLocation(DavesPotioneeringFabric.MODID, "closed_" + color.name().toLowerCase(Locale.ROOT) + "_umbrella"), DUMMY);
    }

    public static<T extends Item & GeoAnimatable>  GeoItemModel<T> makeOpenUmbrella(DyeColor color) {
        return new GeoItemModel<>(new ResourceLocation("open_umbrella"),
                new ResourceLocation(DavesPotioneeringFabric.MODID, "open_" + color.name().toLowerCase(Locale.ROOT) + "_umbrella"), DUMMY);
    }

    public static<T extends Item & GeoAnimatable> GeoItemModel<T> makeClosedUmbrella(String color) {
        return new GeoItemModel<>(new ResourceLocation("closed_umbrella"),
                new ResourceLocation(DavesPotioneeringFabric.MODID, "closed_" + color + "_umbrella"), DUMMY);
    }

    public static <T extends Item & GeoAnimatable>GeoItemModel<T> makeOpenUmbrella(String color) {
        return new GeoItemModel<>(new ResourceLocation("open_umbrella"),
                new ResourceLocation(DavesPotioneeringFabric.MODID, "open_" + color + "_umbrella"), DUMMY);
    }

    public static <T extends Item & GeoAnimatable>GeoItemModel<T> makeOpenAgedUmbrella() {
        return new GeoItemModel<>(new ResourceLocation("open_aged_umbrella"),
                new ResourceLocation(DavesPotioneeringFabric.MODID, "open_aged_umbrella"), DUMMY);
    }

    public GeoItemModel(ResourceLocation item) {
        this(item, DUMMY);
    }

    public GeoItemModel(ResourceLocation item, ResourceLocation animation) {
        this(item, item, animation);
    }

    public GeoItemModel(ResourceLocation model, ResourceLocation texture, ResourceLocation animation) {
        this.animation = animation;
        modelLoc = new ResourceLocation(DavesPotioneeringFabric.MODID, "geo/item/" + model.getPath() + ".geo.json");
        textureLoc = new ResourceLocation(DavesPotioneeringFabric.MODID, "textures/item/" + texture.getPath() + ".png");
    }

    @Override
    public ResourceLocation getModelResource(T object) {
        return modelLoc;
    }

    @Override
    public ResourceLocation getTextureResource(T object) {
        return textureLoc;
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return animation;
    }
}
