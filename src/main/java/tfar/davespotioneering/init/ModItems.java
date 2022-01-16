package tfar.davespotioneering.init;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.client.model.gecko.GeoItemStackRenderer;
import tfar.davespotioneering.item.GauntletItem;
import tfar.davespotioneering.item.SimpleGauntletItem;
import tfar.davespotioneering.item.UmbrellaItem;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

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
    public static final Item NETHERITE_GAUNTLET = new SimpleGauntletItem(ItemTier.NETHERITE,1,-2.8f,new Item.Properties().group(tab));
    public static final Item RUDIMENTARY_GAUNTLET = new SimpleGauntletItem(ItemTier.IRON,1,-2.8f,new Item.Properties().group(tab));

    public static final Item MAGIC_LECTERN = new BlockItem(ModBlocks.MAGIC_LECTERN,new Item.Properties());
    public static final Item POTION_INJECTOR = new BlockItem(ModBlocks.POTION_INJECTOR,new Item.Properties().group(tab));

    public static final Item UMBRELLA = new UmbrellaItem(new Item.Properties().group(tab).maxDamage(300)
            .setISTER(() -> () -> HideISTERsFromServer.createGeoItemStackRenderer(new ResourceLocation(DavesPotioneering.MODID,"umbrella"),() -> ModItems.GENTLEMAN_UMBRELLA)));
    public static final Item GENTLEMAN_UMBRELLA = new UmbrellaItem(new Item.Properties().group(tab).maxDamage(300)
            .setISTER(() -> () -> HideISTERsFromServer.createGeoItemStackRenderer(new ResourceLocation(DavesPotioneering.MODID,"gentleman_umbrella"),() -> ModItems.GENTLEMAN_UMBRELLA)));

    public static final Item CLEAR_UMBRELLA = new UmbrellaItem(new Item.Properties().group(tab).maxDamage(300)
            .setISTER(() -> () -> HideISTERsFromServer.createGeoItemStackRendererTransparent(new ResourceLocation(DavesPotioneering.MODID,"clear_umbrella"),() -> ModItems.GENTLEMAN_UMBRELLA)));

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

        private static ItemStackTileEntityRenderer createGeoItemStackRenderer(ResourceLocation itemName, Supplier<Item> supplier) {
            return new GeoItemStackRenderer<>(new GeoItemStackRenderer.GeoItemModel<>(itemName),new GeoItemStackRenderer.DummyAnimations(supplier));
        }

        private static ItemStackTileEntityRenderer createGeoItemStackRendererTransparent(ResourceLocation itemName, Supplier<Item> supplier) {
            return new GeoItemStackRenderer<>(new GeoItemStackRenderer.GeoItemModel<>(itemName), RenderType::getEntityTranslucent,new GeoItemStackRenderer.DummyAnimations(supplier));
        }
    }
}
