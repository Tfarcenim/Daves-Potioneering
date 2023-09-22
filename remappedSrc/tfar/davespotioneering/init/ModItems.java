package tfar.davespotioneering.init;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
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

    public static final ResourceLocation NET_ID = new ResourceLocation(DavesPotioneering.MODID,"item/sprite/netherite_gauntlet");
    public static final ResourceLocation RUD_ID = new ResourceLocation(DavesPotioneering.MODID,"item/sprite/rudimentary_gauntlet");

    private static List<Item> MOD_ITEMS;

    public static final CreativeModeTab tab = FabricItemGroupBuilder
            .create(new ResourceLocation(DavesPotioneering.MODID, DavesPotioneering.MODID))
            .icon(() -> new ItemStack(ModItems.POTIONEER_GAUNTLET)).build();
    
    public static final Item COMPOUND_BREWING_STAND = new BlockItem(ModBlocks.COMPOUND_BREWING_STAND,new Item.Properties().group(tab));
    public static final Item REINFORCED_CAULDRON = new BlockItem(ModBlocks.REINFORCED_CAULDRON,new Item.Properties().group(tab));
    public static final Item POTIONEER_GAUNTLET = new GauntletItem(new Item.Properties().group(tab).maxDamage(32));
    public static final Item NETHERITE_GAUNTLET = new SimpleGauntletItem(Tiers.NETHERITE,4,-2.8f,new Item.Properties().group(tab),NET_ID);
    public static final Item RUDIMENTARY_GAUNTLET = new SimpleGauntletItem(Tiers.IRON,3,-2.8f,new Item.Properties().group(tab),RUD_ID);

    public static final Item MAGIC_LECTERN = new BlockItem(ModBlocks.MAGIC_LECTERN,new Item.Properties());
    public static final Item POTION_INJECTOR = new BlockItem(ModBlocks.POTION_INJECTOR,new Item.Properties().group(tab));

    public static final Item WHITE_UMBRELLA = new UmbrellaItem(umbrella(),"classic");
    public static final Item ORANGE_UMBRELLA = new UmbrellaItem(umbrella(),"classic");
    public static final Item MAGENTA_UMBRELLA = new UmbrellaItem(umbrella(),"classic");
    public static final Item LIGHT_BLUE_UMBRELLA = new UmbrellaItem(umbrella(),"classic");
    public static final Item YELLOW_UMBRELLA = new UmbrellaItem(umbrella(),"classic");
    public static final Item LIME_UMBRELLA = new UmbrellaItem(umbrella(),"classic");
    public static final Item PINK_UMBRELLA = new UmbrellaItem(umbrella(),"classic");
    public static final Item GRAY_UMBRELLA = new UmbrellaItem(umbrella(),"classic");
    public static final Item LIGHT_GRAY_UMBRELLA = new UmbrellaItem(umbrella(),"classic");
    public static final Item CYAN_UMBRELLA = new UmbrellaItem(umbrella(),"classic");
    public static final Item PURPLE_UMBRELLA = new UmbrellaItem(umbrella(),"classic");
    public static final Item BLUE_UMBRELLA = new UmbrellaItem(umbrella(),"classic");
    public static final Item BROWN_UMBRELLA = new UmbrellaItem(umbrella(),"classic");
    public static final Item GREEN_UMBRELLA = new UmbrellaItem(umbrella(),"classic");
    public static final Item RED_UMBRELLA = new UmbrellaItem(umbrella(),"classic");
    public static final Item BLACK_UMBRELLA = new UmbrellaItem(umbrella(),"classic");

    public static final Item GILDED_UMBRELLA = new UmbrellaItem(umbrella(),"gilded");
    public static final Item AGED_UMBRELLA = new UmbrellaItem(umbrella(),"aged");


    public static Item.Properties umbrella() {
        return new Item.Properties().group(tab).maxDamage(300);
    }

    public static void register() {
        for (Field field : ModItems.class.getFields()) {
            try {
                Object o = field.get(null);
                if (o instanceof Item) {
                    Registry.register(Registry.ITEM,new ResourceLocation(DavesPotioneering.MODID,field.getName().toLowerCase(Locale.ROOT)),(Item)o);
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
