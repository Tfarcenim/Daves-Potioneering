package tfar.davespotioneering.client.model.gecko;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import tfar.davespotioneering.DavesPotioneering;

import java.util.Locale;

public class GeoItemModel<T extends Item & IAnimatable> extends AnimatedGeoModel<T> {

    protected final ResourceLocation animation;

    protected final ResourceLocation modelLoc;
    protected final ResourceLocation textureLoc;


    private static final ResourceLocation DUMMY = new ResourceLocation(DavesPotioneering.MODID, "animations/animation.dummy.json");

    public static <T extends Item & IAnimatable>GeoItemModel<T> makeClosedUmbrella(DyeColor color) {
        return new GeoItemModel<>(new ResourceLocation("closed_umbrella"),
                new ResourceLocation(DavesPotioneering.MODID, "closed_" + color.name().toLowerCase(Locale.ROOT) + "_umbrella"), DUMMY);
    }

    public static<T extends Item & IAnimatable>  GeoItemModel<T> makeOpenUmbrella(DyeColor color) {
        return new GeoItemModel<>(new ResourceLocation("open_umbrella"),
                new ResourceLocation(DavesPotioneering.MODID, "open_" + color.name().toLowerCase(Locale.ROOT) + "_umbrella"), DUMMY);
    }

    public static<T extends Item & IAnimatable> GeoItemModel<T> makeClosedUmbrella(String color) {
        return new GeoItemModel<>(new ResourceLocation("closed_umbrella"),
                new ResourceLocation(DavesPotioneering.MODID, "closed_" + color + "_umbrella"), DUMMY);
    }

    public static <T extends Item & IAnimatable>GeoItemModel<T> makeOpenUmbrella(String color) {
        return new GeoItemModel<>(new ResourceLocation("open_umbrella"),
                new ResourceLocation(DavesPotioneering.MODID, "open_" + color + "_umbrella"), DUMMY);
    }

    public static <T extends Item & IAnimatable>GeoItemModel<T> makeOpenAgedUmbrella() {
        return new GeoItemModel<>(new ResourceLocation("open_aged_umbrella"),
                new ResourceLocation(DavesPotioneering.MODID, "open_aged_umbrella"), DUMMY);
    }

    public GeoItemModel(ResourceLocation item) {
        this(item, DUMMY);
    }

    public GeoItemModel(ResourceLocation item, ResourceLocation animation) {
        this(item, item, animation);
    }

    public GeoItemModel(ResourceLocation model, ResourceLocation texture, ResourceLocation animation) {
        this.animation = animation;
        modelLoc = new ResourceLocation(DavesPotioneering.MODID, "geo/item/" + model.getPath() + ".geo.json");
        textureLoc = new ResourceLocation(DavesPotioneering.MODID, "textures/item/" + texture.getPath() + ".png");
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
