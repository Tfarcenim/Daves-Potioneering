package tfar.davespotioneering.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.*;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.client.model.gecko.DoubleGeoItemStackRenderer;
import tfar.davespotioneering.client.model.gecko.GeoItemStackRenderer;
import tfar.davespotioneering.item.AgedUmbrellaItem;
import tfar.davespotioneering.item.GauntletItem;
import tfar.davespotioneering.item.SimpleGauntletItem;
import tfar.davespotioneering.item.UmbrellaItem;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ModItems {

    private static List<Item> MOD_ITEMS;

    public static final CreativeModeTab tab = new CreativeModeTab(DavesPotioneering.MODID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.COMPOUND_BREWING_STAND);
        }
    };
    
    public static final Item COMPOUND_BREWING_STAND = new BlockItem(ModBlocks.COMPOUND_BREWING_STAND,new Item.Properties().tab(tab));
    public static final Item REINFORCED_CAULDRON = new BlockItem(ModBlocks.REINFORCED_CAULDRON,new Item.Properties().tab(tab));
    public static final Item POTIONEER_GAUNTLET = new GauntletItem(new Item.Properties().tab(tab).durability(32));
    public static final Item NETHERITE_GAUNTLET = new SimpleGauntletItem(Tiers.NETHERITE,4,-2.8f,new Item.Properties().tab(tab));
    public static final Item RUDIMENTARY_GAUNTLET = new SimpleGauntletItem(Tiers.IRON,3,-2.8f,new Item.Properties().tab(tab));

    public static final Item MAGIC_LECTERN = new BlockItem(ModBlocks.MAGIC_LECTERN,new Item.Properties());
    public static final Item POTION_INJECTOR = new BlockItem(ModBlocks.POTION_INJECTOR,new Item.Properties().tab(tab));

    public static final Item WHITE_UMBRELLA = new UmbrellaItem(baseUmbrella(),DyeColor.WHITE,"classic");
    public static final Item ORANGE_UMBRELLA = new UmbrellaItem(baseUmbrella(),DyeColor.ORANGE,"classic");
    public static final Item MAGENTA_UMBRELLA = new UmbrellaItem(baseUmbrella(),DyeColor.MAGENTA,"classic");
    public static final Item LIGHT_BLUE_UMBRELLA = new UmbrellaItem(baseUmbrella(),DyeColor.LIGHT_BLUE,"classic");
    public static final Item YELLOW_UMBRELLA = new UmbrellaItem(baseUmbrella(),DyeColor.YELLOW,"classic");
    public static final Item LIME_UMBRELLA = new UmbrellaItem(baseUmbrella(),DyeColor.LIME,"classic");
    public static final Item PINK_UMBRELLA = new UmbrellaItem(baseUmbrella(),DyeColor.PINK,"classic");
    public static final Item GRAY_UMBRELLA = new UmbrellaItem(baseUmbrella(),DyeColor.GRAY,"classic");
    public static final Item LIGHT_GRAY_UMBRELLA = new UmbrellaItem(baseUmbrella(),DyeColor.LIGHT_GRAY,"classic");
    public static final Item CYAN_UMBRELLA = new UmbrellaItem(baseUmbrella(),DyeColor.CYAN,"classic");
    public static final Item PURPLE_UMBRELLA = new UmbrellaItem(baseUmbrella(),DyeColor.PURPLE,"classic");
    public static final Item BLUE_UMBRELLA = new UmbrellaItem(baseUmbrella(),DyeColor.BLUE,"classic");
    public static final Item BROWN_UMBRELLA = new UmbrellaItem(baseUmbrella(),DyeColor.BROWN,"classic");
    public static final Item GREEN_UMBRELLA = new UmbrellaItem(baseUmbrella(),DyeColor.GREEN,"classic");
    public static final Item RED_UMBRELLA = new UmbrellaItem(baseUmbrella(),DyeColor.RED,"classic");
    public static final Item BLACK_UMBRELLA = new UmbrellaItem(baseUmbrella(),DyeColor.BLACK,"classic");

    public static final Item GILDED_UMBRELLA = new UmbrellaItem(baseUmbrella(),"gilded","gilded");
    public static final Item AGED_UMBRELLA = new AgedUmbrellaItem(baseUmbrella(),"aged");



    public static Item.Properties baseUmbrella() {
        return new Item.Properties().tab(tab).durability(300);
    }

    public static class HideISTERsFromServer {

        public static BlockEntityWithoutLevelRenderer createGeoClassicUmbrellaItemStackRenderer(DyeColor color) {
            return createGeoClassicUmbrellaItemStackRenderer(color.name().toLowerCase(Locale.ROOT));
        }

        public static BlockEntityWithoutLevelRenderer createGeoClassicUmbrellaItemStackRenderer(String itemName) {
            return new DoubleGeoItemStackRenderer<>(
                    GeoItemStackRenderer.GeoItemModel.makeClosedUmbrella(itemName),
                    GeoItemStackRenderer.GeoItemModel.makeOpenUmbrella(itemName)
                    ,GeoItemStackRenderer.NOTHING,Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        }

        @Nonnull
        public static BlockEntityWithoutLevelRenderer createAgedUmbrellaItemStackRenderer() {
            return new DoubleGeoItemStackRenderer<>(
                    GeoItemStackRenderer.GeoItemModel.makeClosedUmbrella("aged"),
                    GeoItemStackRenderer.GeoItemModel.makeOpenAgedUmbrella()
                    ,GeoItemStackRenderer.NOTHING, Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        }

    }

    public static List<Item> getAllItems() {
        if (MOD_ITEMS == null) {
            MOD_ITEMS = Arrays.stream(ModItems.class.getFields()).map(field -> {
                try {
                    return field.get(null);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }).filter(Item.class::isInstance).map(Item.class::cast).collect(Collectors.toList());
        }
        return MOD_ITEMS;
    }

}
