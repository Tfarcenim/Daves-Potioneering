package tfar.davespotioneering.init;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ShieldItem;
import net.minecraftforge.event.RegistryEvent;
import tfar.davespotioneering.item.GauntletItem;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class ModItems {

    private static List<Item> MOD_ITEMS;

    public static final Item ADVANCED_BREWING_STAND = new BlockItem(ModBlocks.ADVANCED_BREWING_STAND,new Item.Properties().group(ItemGroup.DECORATIONS));
    public static final Item REINFORCED_CAULDRON = new BlockItem(ModBlocks.REINFORCED_CAULDRON,new Item.Properties().group(ItemGroup.DECORATIONS));
    public static final Item ALCHEMICAL_GAUNTLET = new GauntletItem(new Item.Properties().group(ItemGroup.COMBAT));
    public static final Item POTIONEER_SHIELD = new ShieldItem(new Item.Properties().group(ItemGroup.COMBAT));
    public static final Item MAGIC_LECTERN = new BlockItem(ModBlocks.MAGIC_LECTERN,new Item.Properties().group(ItemGroup.DECORATIONS));
    public static final Item GAUNTLET_WORKSTATION = new BlockItem(ModBlocks.GAUNTLET_WORKSTATION,new Item.Properties().group(ItemGroup.DECORATIONS));

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
}
