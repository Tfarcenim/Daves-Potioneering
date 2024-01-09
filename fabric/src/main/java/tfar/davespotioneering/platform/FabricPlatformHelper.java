package tfar.davespotioneering.platform;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.inv.BrewingHandler;
import tfar.davespotioneering.inventory.BasicInventoryBridge;
import tfar.davespotioneering.item.UmbrellaItem;
import tfar.davespotioneering.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;

import java.lang.reflect.Field;
import java.util.Locale;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public <T> void superRegister(Class<?> clazz, Registry<T> registry, Class<? extends T> filter) {
        for (Field field : clazz.getFields()) {
            try {
                Object o = field.get(null);
                if (filter.isInstance(o)) {
                    Registry.register(registry,new ResourceLocation(DavesPotioneering.MODID,field.getName().toLowerCase(Locale.ROOT)),(T)o);
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }

    //loader specific instantiations

    @Override
    public Item makeBasicUmbrella(Item.Properties builder, String name, String style) {
        return new UmbrellaItem(builder,name,style);
    }

    @Override
    public Slot makeIngSlot(BasicInventoryBridge handle, int slot, int x, int y) {
        return new BrewingStandMenu.IngredientSlot((Container) handle,slot,x,y);
    }

    @Override
    public Slot makePotSlot(BasicInventoryBridge handle, int slot, int x, int y) {
        return new BrewingStandMenu.PotionSlot((Container) handle,slot,x,y);
    }

    @Override
    public Slot makeFuelSlot(BasicInventoryBridge handle, int slot, int x, int y) {
        return new BrewingStandMenu.FuelSlot((Container) handle,slot,x,y);
    }

    @Override
    public BasicInventoryBridge makeBrewingHandler(int slots) {
        return new BrewingHandler(slots);
    }
}
