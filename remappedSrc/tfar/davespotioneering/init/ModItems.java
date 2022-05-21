package tfar.davespotioneering.init;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.item.*;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.item.GauntletItem;
import tfar.davespotioneering.item.SimpleGauntletItem;
import tfar.davespotioneering.item.UmbrellaItem;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ModItems {

    private static List<Item> MOD_ITEMS;

    public static final ItemGroup tab = new ItemGroup(8,DavesPotioneering.MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.COMPOUND_BREWING_STAND);
        }
    };
    
    public static final Item COMPOUND_BREWING_STAND = new BlockItem(ModBlocks.COMPOUND_BREWING_STAND,new Item.Settings().group(tab));
    public static final Item REINFORCED_CAULDRON = new BlockItem(ModBlocks.REINFORCED_CAULDRON,new Item.Settings().group(tab));
    public static final Item POTIONEER_GAUNTLET = new GauntletItem(new Item.Settings().group(tab).maxDamage(32));
    public static final Item NETHERITE_GAUNTLET = new SimpleGauntletItem(ToolMaterials.NETHERITE,4,-2.8f,new Item.Settings().group(tab));
    public static final Item RUDIMENTARY_GAUNTLET = new SimpleGauntletItem(ToolMaterials.IRON,3,-2.8f,new Item.Settings().group(tab));

    public static final Item MAGIC_LECTERN = new BlockItem(ModBlocks.MAGIC_LECTERN,new Item.Settings());
    public static final Item POTION_INJECTOR = new BlockItem(ModBlocks.POTION_INJECTOR,new Item.Settings().group(tab));

    public static final Item WHITE_UMBRELLA = new UmbrellaItem(classicUmbrella(DyeColor.WHITE),"classic");
    public static final Item ORANGE_UMBRELLA = new UmbrellaItem(classicUmbrella(DyeColor.ORANGE),"classic");
    public static final Item MAGENTA_UMBRELLA = new UmbrellaItem(classicUmbrella(DyeColor.MAGENTA),"classic");
    public static final Item LIGHT_BLUE_UMBRELLA = new UmbrellaItem(classicUmbrella(DyeColor.LIGHT_BLUE),"classic");
    public static final Item YELLOW_UMBRELLA = new UmbrellaItem(classicUmbrella(DyeColor.YELLOW),"classic");
    public static final Item LIME_UMBRELLA = new UmbrellaItem(classicUmbrella(DyeColor.LIME),"classic");
    public static final Item PINK_UMBRELLA = new UmbrellaItem(classicUmbrella(DyeColor.PINK),"classic");
    public static final Item GRAY_UMBRELLA = new UmbrellaItem(classicUmbrella(DyeColor.GRAY),"classic");
    public static final Item LIGHT_GRAY_UMBRELLA = new UmbrellaItem(classicUmbrella(DyeColor.LIGHT_GRAY),"classic");
    public static final Item CYAN_UMBRELLA = new UmbrellaItem(classicUmbrella(DyeColor.CYAN),"classic");
    public static final Item PURPLE_UMBRELLA = new UmbrellaItem(classicUmbrella(DyeColor.PURPLE),"classic");
    public static final Item BLUE_UMBRELLA = new UmbrellaItem(classicUmbrella(DyeColor.BLUE),"classic");
    public static final Item BROWN_UMBRELLA = new UmbrellaItem(classicUmbrella(DyeColor.BROWN),"classic");
    public static final Item GREEN_UMBRELLA = new UmbrellaItem(classicUmbrella(DyeColor.GREEN),"classic");
    public static final Item RED_UMBRELLA = new UmbrellaItem(classicUmbrella(DyeColor.RED),"classic");
    public static final Item BLACK_UMBRELLA = new UmbrellaItem(classicUmbrella(DyeColor.BLACK),"classic");

    public static final Item GILDED_UMBRELLA = new UmbrellaItem(umbrella("gilded"),"gilded");
    public static final Item AGED_UMBRELLA = new UmbrellaItem(
            baseUmbrella().setISTER(() -> HideISTERsFromServer::createAgedUmbrellaItemStackRenderer),"aged");



    public static Item.Settings baseUmbrella() {
        return new Item.Settings().group(tab).maxDamage(300);
    }


    public static Item.Settings umbrella(String s) {
        return baseUmbrella()
                .setISTER(() -> () -> HideISTERsFromServer.createGeoClassicUmbrellaItemStackRenderer(s));
    }

    public static Item.Settings classicUmbrella(DyeColor dyeColor) {
       return baseUmbrella()
                .setISTER(() -> () -> HideISTERsFromServer.createGeoClassicUmbrellaItemStackRenderer(dyeColor));
    }

    public static void register() {
        for (Field field : ModItems.class.getFields()) {
            try {
                Object o = field.get(null);
                if (o instanceof Item) {
                    Registry.register(Registry.ITEM,new Identifier(DavesPotioneering.MODID,field.getName().toLowerCase(Locale.ROOT)),(Item)o);
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
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
