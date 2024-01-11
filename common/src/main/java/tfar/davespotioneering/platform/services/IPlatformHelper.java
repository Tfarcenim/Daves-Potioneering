package tfar.davespotioneering.platform.services;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import tfar.davespotioneering.blockentity.CAdvancedBrewingStandBlockEntity;
import tfar.davespotioneering.blockentity.CPotionInjectorBlockEntity;
import tfar.davespotioneering.blockentity.CReinforcedCauldronBlockEntity;
import tfar.davespotioneering.inventory.BasicInventoryBridge;
import tfar.davespotioneering.item.CGauntletItem;

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

    <T extends Registry<? extends F>,F> void superRegister(Class<?> clazz, T registry, Class<? extends F> filter);


    //loader specific instantiations

    default Item makeBasicUmbrella(Item.Properties builder, DyeColor name, String style) {
        return makeBasicUmbrella(builder,name.getName().toLowerCase(Locale.ROOT),style);
    }
    Item makeBasicUmbrella(Item.Properties builder, String name, String style);
    Item makeAgedUmbrella(Item.Properties builder,String style);

    default Slot makeBasic(BasicInventoryBridge handle,int slot,int x, int y) {
        return new Slot((Container) handle,slot,x,y);
    }
    Slot makeIngSlot(BasicInventoryBridge handle,int slot,int x, int y);
    Slot makePotSlot(BasicInventoryBridge handle,int slot,int x, int y);
    Slot makeFuelSlot(BasicInventoryBridge handle,int slot,int x, int y);

    BasicInventoryBridge makeBrewingHandler(int slots);
    BasicInventoryBridge makePotionInjector(int slots);

    CAdvancedBrewingStandBlockEntity makeAdvancedBrewingStand(BlockPos pos, BlockState state);
    CReinforcedCauldronBlockEntity makeReinforcedCauldron(BlockPos pos, BlockState state);
    CPotionInjectorBlockEntity makePotionInjector(BlockPos pos, BlockState state);


    int[] getGauntletCooldowns(Player player);

    void setGauntletCooldowns(Player player,int[] cooldowns);

    void syncGauntletCooldowns(Player player,int[] cooldowns);
    CGauntletItem makeGauntlet(Item.Properties properties);

    //configs
    boolean coatTools();
    boolean spikeFood();
    boolean coatAnything();
    int coatingUses();
    int gauntletCooldown();
    int potionSwitchCooldown();
}