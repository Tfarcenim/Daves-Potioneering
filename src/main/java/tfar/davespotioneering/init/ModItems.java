package tfar.davespotioneering.init;

import net.minecraft.item.*;
import net.minecraftforge.event.RegistryEvent;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.item.GauntletItem;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class ModItems {

    private static List<Item> MOD_ITEMS;

    public static final ItemGroup tab = new ItemGroup(DavesPotioneering.MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.ADVANCED_BREWING_STAND);
        }
    };
    
    public static final Item ADVANCED_BREWING_STAND = new BlockItem(ModBlocks.ADVANCED_BREWING_STAND,new Item.Properties().group(tab));
    public static final Item REINFORCED_CAULDRON = new BlockItem(ModBlocks.REINFORCED_CAULDRON,new Item.Properties().group(tab));
    public static final Item ALCHEMICAL_GAUNTLET = new GauntletItem(new Item.Properties().group(tab).maxDamage(32));
    public static final Item POTIONEER_SHIELD = new ShieldItem(new Item.Properties().group(tab));
    public static final Item MAGIC_LECTERN = new BlockItem(ModBlocks.MAGIC_LECTERN,new Item.Properties());
    public static final Item POTION_INJECTOR = new BlockItem(ModBlocks.POTION_INJECTOR,new Item.Properties().group(tab));

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
