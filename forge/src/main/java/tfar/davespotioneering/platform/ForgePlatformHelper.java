package tfar.davespotioneering.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.DavesPotioneeringForge;
import tfar.davespotioneering.blockentity.AdvancedBrewingStandBlockEntity;
import tfar.davespotioneering.inv.BrewingHandler;
import tfar.davespotioneering.inv.slots.FuelSlot;
import tfar.davespotioneering.inv.slots.IngredientSlot;
import tfar.davespotioneering.inv.slots.PotionSlot;
import tfar.davespotioneering.inventory.BasicInventoryBridge;
import tfar.davespotioneering.item.UmbrellaItem;
import tfar.davespotioneering.platform.services.IPlatformHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    @Override
    public <T> void superRegister(Class<?> clazz, Registry<T> registry, Class<? extends T> filter) {
            List<Pair<ResourceLocation, Supplier<?>>> list = DavesPotioneeringForge.registerLater.computeIfAbsent(registry, k -> new ArrayList<>());
        for (Field field : clazz.getFields()) {
            try {
                Object o = field.get(null);
                if (filter.isInstance(o)) {
                    list.add(Pair.of(new ResourceLocation(DavesPotioneering.MODID,field.getName().toLowerCase(Locale.ROOT)),() -> o));
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }

    @Override
    public Item makeBasicUmbrella(Item.Properties builder, String name, String style) {
        return new UmbrellaItem(builder,name,style);
    }

    @Override
    public Slot makeIngSlot(BasicInventoryBridge handle, int slot, int x, int y) {
        return new IngredientSlot((IItemHandler) handle,slot,x,y);
    }

    @Override
    public Slot makePotSlot(BasicInventoryBridge handle, int slot, int x, int y) {
        return new PotionSlot((IItemHandler) handle,slot,x,y);
    }

    @Override
    public Slot makeFuelSlot(BasicInventoryBridge handle, int slot, int x, int y) {
        return new FuelSlot((IItemHandler) handle,slot,x,y);
    }

    @Override
    public BasicInventoryBridge makeBrewingHandler(int slots) {
        return new BrewingHandler(slots);
    }

    @Override
    public BlockEntity makeAdvancedBrewingStand(BlockPos pos, BlockState state) {
        return new AdvancedBrewingStandBlockEntity(pos,state);
    }
}