package tfar.davespotioneering.init;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;

import java.lang.reflect.Field;
import java.util.List;

public class ModItems {


    private static List<Item> MOD_ITEMS;

    public static void register(RegistryEvent.Register<Item> e) {
        for (Field field : ModItems.class.getFields()) {
            try {
                if (field.get(null) instanceof Item) {
                 //   e.getRegistry().register((Item) field.get(null), field.getName().toLowerCase(Locale.ROOT));
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }

}
