package tfar.davespotioneering.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.item.AgedUmbrellaItem;
import tfar.davespotioneering.item.GauntletItem;
import tfar.davespotioneering.item.SimpleGauntletItem;
import tfar.davespotioneering.platform.Services;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ModItems {

    private static List<Item> MOD_ITEMS;


    public static final Item COMPOUND_BREWING_STAND = new BlockItem(ModBlocks.COMPOUND_BREWING_STAND,new Item.Properties());
    public static final Item REINFORCED_CAULDRON = new BlockItem(ModBlocks.REINFORCED_CAULDRON,new Item.Properties());
    public static final Item POTIONEER_GAUNTLET = new GauntletItem(new Item.Properties().durability(32));
    public static final Item NETHERITE_GAUNTLET = new SimpleGauntletItem(Tiers.NETHERITE,4,-2.8f,new Item.Properties());
    public static final Item RUDIMENTARY_GAUNTLET = new SimpleGauntletItem(Tiers.IRON,3,-2.8f,new Item.Properties());

    public static final Item MAGIC_LECTERN = new BlockItem(ModBlocks.MAGIC_LECTERN,new Item.Properties());
    public static final Item POTION_INJECTOR = new BlockItem(ModBlocks.POTION_INJECTOR,new Item.Properties());

    public static final Item WHITE_UMBRELLA = Services.PLATFORM.makeBasicUmbrella(baseUmbrella(),DyeColor.WHITE,"classic");
    public static final Item ORANGE_UMBRELLA = Services.PLATFORM.makeBasicUmbrella(baseUmbrella(),DyeColor.ORANGE,"classic");
    public static final Item MAGENTA_UMBRELLA = Services.PLATFORM.makeBasicUmbrella(baseUmbrella(),DyeColor.MAGENTA,"classic");
    public static final Item LIGHT_BLUE_UMBRELLA = Services.PLATFORM.makeBasicUmbrella(baseUmbrella(),DyeColor.LIGHT_BLUE,"classic");
    public static final Item YELLOW_UMBRELLA = Services.PLATFORM.makeBasicUmbrella(baseUmbrella(),DyeColor.YELLOW,"classic");
    public static final Item LIME_UMBRELLA = Services.PLATFORM.makeBasicUmbrella(baseUmbrella(),DyeColor.LIME,"classic");
    public static final Item PINK_UMBRELLA = Services.PLATFORM.makeBasicUmbrella(baseUmbrella(),DyeColor.PINK,"classic");
    public static final Item GRAY_UMBRELLA = Services.PLATFORM.makeBasicUmbrella(baseUmbrella(),DyeColor.GRAY,"classic");
    public static final Item LIGHT_GRAY_UMBRELLA = Services.PLATFORM.makeBasicUmbrella(baseUmbrella(),DyeColor.LIGHT_GRAY,"classic");
    public static final Item CYAN_UMBRELLA = Services.PLATFORM.makeBasicUmbrella(baseUmbrella(),DyeColor.CYAN,"classic");
    public static final Item PURPLE_UMBRELLA = Services.PLATFORM.makeBasicUmbrella(baseUmbrella(),DyeColor.PURPLE,"classic");
    public static final Item BLUE_UMBRELLA = Services.PLATFORM.makeBasicUmbrella(baseUmbrella(),DyeColor.BLUE,"classic");
    public static final Item BROWN_UMBRELLA = Services.PLATFORM.makeBasicUmbrella(baseUmbrella(),DyeColor.BROWN,"classic");
    public static final Item GREEN_UMBRELLA = Services.PLATFORM.makeBasicUmbrella(baseUmbrella(),DyeColor.GREEN,"classic");
    public static final Item RED_UMBRELLA = Services.PLATFORM.makeBasicUmbrella(baseUmbrella(),DyeColor.RED,"classic");
    public static final Item BLACK_UMBRELLA = Services.PLATFORM.makeBasicUmbrella(baseUmbrella(),DyeColor.BLACK,"classic");

    public static final Item GILDED_UMBRELLA = Services.PLATFORM.makeBasicUmbrella(baseUmbrella(),"gilded","gilded");
    public static final Item AGED_UMBRELLA = new AgedUmbrellaItem(baseUmbrella(),"aged");


    public static final TagKey<Item> BLACKLISTED = ItemTags.create(new ResourceLocation(DavesPotioneering.MODID,"blacklisted"));
    public static final TagKey<Item> WHITELISTED = ItemTags.create(new ResourceLocation(DavesPotioneering.MODID,"whitelisted"));


    public static Item.Properties baseUmbrella() {
        return new Item.Properties().durability(300);
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
