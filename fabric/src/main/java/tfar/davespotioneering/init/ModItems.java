package tfar.davespotioneering.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.DavesPotioneeringFabric;
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

    public static final CreativeModeTab tab = CreativeModeTab.builder(CreativeModeTab.Row.TOP,0)
            .title(Component.translatable("itemGroup."+ DavesPotioneering.MODID+"."+ DavesPotioneering.MODID))
            .icon(() -> new ItemStack(ModItems.POTIONEER_GAUNTLET))
            .displayItems((itemDisplayParameters, output) -> getAllItems().forEach(output::accept))
            .build();
    
    public static final Item COMPOUND_BREWING_STAND = new BlockItem(ModBlocks.COMPOUND_BREWING_STAND,new Item.Properties());
    public static final Item REINFORCED_CAULDRON = new BlockItem(ModBlocks.REINFORCED_CAULDRON,new Item.Properties());
    public static final Item POTIONEER_GAUNTLET = new GauntletItem(new Item.Properties().durability(32));
    public static final Item NETHERITE_GAUNTLET = new SimpleGauntletItem(Tiers.NETHERITE,4,-2.8f,new Item.Properties(),NET_ID);
    public static final Item RUDIMENTARY_GAUNTLET = new SimpleGauntletItem(Tiers.IRON,3,-2.8f,new Item.Properties(),RUD_ID);

    public static final Item MAGIC_LECTERN = new BlockItem(ModBlocks.MAGIC_LECTERN,new Item.Properties());
    public static final Item POTION_INJECTOR = new BlockItem(ModBlocks.POTION_INJECTOR,new Item.Properties());

    public static final Item WHITE_UMBRELLA = new UmbrellaItem(umbrella(), DyeColor.WHITE, "classic");
    public static final Item ORANGE_UMBRELLA = new UmbrellaItem(umbrella(), DyeColor.ORANGE, "classic");
    public static final Item MAGENTA_UMBRELLA = new UmbrellaItem(umbrella(), DyeColor.MAGENTA, "classic");
    public static final Item LIGHT_BLUE_UMBRELLA = new UmbrellaItem(umbrella(), DyeColor.LIGHT_BLUE, "classic");
    public static final Item YELLOW_UMBRELLA = new UmbrellaItem(umbrella(), DyeColor.YELLOW, "classic");
    public static final Item LIME_UMBRELLA = new UmbrellaItem(umbrella(),DyeColor.LIME, "classic");
    public static final Item PINK_UMBRELLA = new UmbrellaItem(umbrella(), DyeColor.PINK, "classic");
    public static final Item GRAY_UMBRELLA = new UmbrellaItem(umbrella(), DyeColor.GRAY, "classic");
    public static final Item LIGHT_GRAY_UMBRELLA = new UmbrellaItem(umbrella(), DyeColor.LIGHT_GRAY, "classic");
    public static final Item CYAN_UMBRELLA = new UmbrellaItem(umbrella(), DyeColor.CYAN, "classic");
    public static final Item PURPLE_UMBRELLA = new UmbrellaItem(umbrella(), DyeColor.PURPLE, "classic");
    public static final Item BLUE_UMBRELLA = new UmbrellaItem(umbrella(), DyeColor.BLUE, "classic");
    public static final Item BROWN_UMBRELLA = new UmbrellaItem(umbrella(), DyeColor.BROWN, "classic");
    public static final Item GREEN_UMBRELLA = new UmbrellaItem(umbrella(), DyeColor.GREEN, "classic");
    public static final Item RED_UMBRELLA = new UmbrellaItem(umbrella(), DyeColor.RED, "classic");
    public static final Item BLACK_UMBRELLA = new UmbrellaItem(umbrella(), DyeColor.BLACK, "classic");

    public static final Item GILDED_UMBRELLA = new UmbrellaItem(umbrella(), "gilded", "gilded");
    public static final Item AGED_UMBRELLA = new UmbrellaItem(umbrella(), "aged", "aged");


    public static Item.Properties umbrella() {
        return new Item.Properties().durability(300);
    }

    public static void register() {
        for (Field field : ModItems.class.getFields()) {
            try {
                Object o = field.get(null);
                if (o instanceof Item) {
                    Registry.register(BuiltInRegistries.ITEM,new ResourceLocation(DavesPotioneering.MODID,field.getName().toLowerCase(Locale.ROOT)),(Item)o);
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
