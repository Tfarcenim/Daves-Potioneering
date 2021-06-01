package tfar.davespotioneering.init;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class ModItems {

    private static List<Item> MOD_ITEMS;

    public static final Item ADVANCED_BREWING_STAND = new BlockItem(ModBlocks.ADVANCED_BREWING_STAND,new Item.Properties().group(ItemGroup.DECORATIONS));

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
