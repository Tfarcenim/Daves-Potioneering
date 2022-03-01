package tfar.davespotioneering.init;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.client.model.gecko.DoubleGeoItemStackRenderer;
import tfar.davespotioneering.client.model.gecko.GeoItemStackRenderer;
import tfar.davespotioneering.item.GauntletItem;
import tfar.davespotioneering.item.SimpleGauntletItem;
import tfar.davespotioneering.item.UmbrellaItem;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class ModItems {

    private static List<Item> MOD_ITEMS;

    public static final ItemGroup tab = new ItemGroup(DavesPotioneering.MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.COMPOUND_BREWING_STAND);
        }
    };
    
    public static final Item COMPOUND_BREWING_STAND = new BlockItem(ModBlocks.COMPOUND_BREWING_STAND,new Item.Properties().group(tab));
    public static final Item REINFORCED_CAULDRON = new BlockItem(ModBlocks.REINFORCED_CAULDRON,new Item.Properties().group(tab));
    public static final Item POTIONEER_GAUNTLET = new GauntletItem(new Item.Properties().group(tab).maxDamage(32));
    public static final Item NETHERITE_GAUNTLET = new SimpleGauntletItem(ItemTier.NETHERITE,3,-2.8f,new Item.Properties().group(tab));
    public static final Item RUDIMENTARY_GAUNTLET = new SimpleGauntletItem(ItemTier.IRON,3,-2.8f,new Item.Properties().group(tab));

    public static final Item MAGIC_LECTERN = new BlockItem(ModBlocks.MAGIC_LECTERN,new Item.Properties());
    public static final Item POTION_INJECTOR = new BlockItem(ModBlocks.POTION_INJECTOR,new Item.Properties().group(tab));

    public static final Item WHITE_UMBRELLA = new UmbrellaItem(classicUmbrella(DyeColor.WHITE));
    public static final Item ORANGE_UMBRELLA = new UmbrellaItem(classicUmbrella(DyeColor.ORANGE));
    public static final Item MAGENTA_UMBRELLA = new UmbrellaItem(classicUmbrella(DyeColor.MAGENTA));
    public static final Item LIGHT_BLUE_UMBRELLA = new UmbrellaItem(classicUmbrella(DyeColor.LIGHT_BLUE));
    public static final Item YELLOW_UMBRELLA = new UmbrellaItem(classicUmbrella(DyeColor.YELLOW));
    public static final Item LIME_UMBRELLA = new UmbrellaItem(classicUmbrella(DyeColor.LIME));
    public static final Item PINK_UMBRELLA = new UmbrellaItem(classicUmbrella(DyeColor.PINK));
    public static final Item GRAY_UMBRELLA = new UmbrellaItem(classicUmbrella(DyeColor.GRAY));
    public static final Item LIGHT_GRAY_UMBRELLA = new UmbrellaItem(classicUmbrella(DyeColor.LIGHT_GRAY));
    public static final Item CYAN_UMBRELLA = new UmbrellaItem(classicUmbrella(DyeColor.CYAN));
    public static final Item PURPLE_UMBRELLA = new UmbrellaItem(classicUmbrella(DyeColor.PURPLE));
    public static final Item BLUE_UMBRELLA = new UmbrellaItem(classicUmbrella(DyeColor.BLUE));
    public static final Item BROWN_UMBRELLA = new UmbrellaItem(classicUmbrella(DyeColor.BROWN));
    public static final Item GREEN_UMBRELLA = new UmbrellaItem(classicUmbrella(DyeColor.GREEN));
    public static final Item RED_UMBRELLA = new UmbrellaItem(classicUmbrella(DyeColor.RED));
    public static final Item BLACK_UMBRELLA = new UmbrellaItem(classicUmbrella(DyeColor.BLACK));



    public static Item.Properties classicUmbrella(DyeColor dyeColor) {
       return new Item.Properties().group(tab).maxDamage(300)
                .setISTER(() -> () -> HideISTERsFromServer.createGeoUmbrellaItemStackRenderer(
                        new ResourceLocation(DavesPotioneering.MODID,dyeColor.name().toLowerCase(Locale.ROOT)+"_umbrella")));
    }

    public static void register(RegistryEvent.Register<Item> e) {
        for (Field field : ModItems.class.getFields()) {
            try {
                Object o = field.get(null);
                if (o instanceof Item) {
                    e.getRegistry().register(((Item) o).setRegistryName(field.getName().toLowerCase(Locale.ROOT)));
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }

    private static class HideISTERsFromServer {

        private static ItemStackTileEntityRenderer createGeoUmbrellaItemStackRenderer(ResourceLocation itemName) {
            return new DoubleGeoItemStackRenderer<>(new GeoItemStackRenderer.GeoItemModel<>(new ResourceLocation(itemName.getNamespace(),"closed_"+ itemName.getPath())),
                    new GeoItemStackRenderer.GeoItemModel<>(new ResourceLocation(itemName.getNamespace(),"open_"+ itemName.getPath())),new GeoItemStackRenderer.DummyAnimations());
        }

        private static ItemStackTileEntityRenderer createGeoItemStackRendererTransparent(ResourceLocation itemName) {
            return new GeoItemStackRenderer<>(new GeoItemStackRenderer.GeoItemModel<>(itemName), RenderType::getEntityTranslucent,new GeoItemStackRenderer.DummyAnimations());
        }
    }
}
