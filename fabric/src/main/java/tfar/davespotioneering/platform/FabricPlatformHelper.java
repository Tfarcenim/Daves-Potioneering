package tfar.davespotioneering.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.DavesPotioneeringFabric;
import tfar.davespotioneering.blockentity.AdvancedBrewingStandBlockEntity;
import tfar.davespotioneering.blockentity.CAdvancedBrewingStandBlockEntity;
import tfar.davespotioneering.blockentity.CReinforcedCauldronBlockEntity;
import tfar.davespotioneering.blockentity.ReinforcedCauldronBlockEntity;
import tfar.davespotioneering.inv.BrewingHandlerFabric;
import tfar.davespotioneering.inv.PotionInjectorHandlerFabric;
import tfar.davespotioneering.inventory.BasicInventoryBridge;
import tfar.davespotioneering.item.CGauntletItem;
import tfar.davespotioneering.item.GauntletItemFabric;
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
        return new BrewingStandMenu.IngredientsSlot((Container) handle,slot,x,y);
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
        return new BrewingHandlerFabric(slots);
    }

    @Override
    public BasicInventoryBridge makePotionInjector(int slots) {
        return new PotionInjectorHandlerFabric(slots);
    }

    @Override
    public CAdvancedBrewingStandBlockEntity makeAdvancedBrewingStand(BlockPos pos, BlockState state) {
        return new AdvancedBrewingStandBlockEntity(pos,state);
    }

    @Override
    public CReinforcedCauldronBlockEntity makeReinforcedCauldron(BlockPos pos, BlockState state) {
        return new ReinforcedCauldronBlockEntity(pos,state);
    }

    @Override
    public CGauntletItem makeGauntlet(Item.Properties properties) {
        return new GauntletItemFabric(properties);
    }

    //configs
    @Override
    public boolean coatTools() {
        return DavesPotioneeringFabric.CONFIG.coat_tools;
    }

    @Override
    public boolean spikeFood() {
        return DavesPotioneeringFabric.CONFIG.spike_food;
    }

    @Override
    public boolean coatAnything() {
        return DavesPotioneeringFabric.CONFIG.coat_anything;
    }

    @Override
    public int coatingUses() {
        return DavesPotioneeringFabric.CONFIG.coating_uses;
    }
}
