package tfar.davespotioneering.platform.services;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import tfar.davespotioneering.inventory.BasicInventoryBridge;

import java.util.Locale;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }

    <T> void superRegister(Class<?> clazz, Registry<T> registry, Class<? extends T> filter);


    //loader specific instantiations

    default Item makeBasicUmbrella(Item.Properties builder, DyeColor name, String style) {
        return makeBasicUmbrella(builder,name.getName().toLowerCase(Locale.ROOT),style);
    }
    Item makeBasicUmbrella(Item.Properties builder, String name, String style);

    Slot makeIngSlot(BasicInventoryBridge handle,int slot,int x, int y);
    Slot makePotSlot(BasicInventoryBridge handle,int slot,int x, int y);
    Slot makeFuelSlot(BasicInventoryBridge handle,int slot,int x, int y);

    BasicInventoryBridge makeBrewingHandler(int slots);
    BlockEntity makeAdvancedBrewingStand(BlockPos pos, BlockState state);

}