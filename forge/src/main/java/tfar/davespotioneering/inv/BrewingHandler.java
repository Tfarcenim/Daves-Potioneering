package tfar.davespotioneering.inv;

import com.google.common.collect.Sets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.ArrayUtils;
import tfar.davespotioneering.ForgeUtil;
import tfar.davespotioneering.blockentity.AdvancedBrewingStandBlockEntity;
import tfar.davespotioneering.inventory.BasicInventoryBridge;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class BrewingHandler extends BridgedItemStackHandler {

    public BrewingHandler(int size) {
        super(size);
    }

    public NonNullList<ItemStack> $getStacks() {
        return stacks;
    }

    @Override
    public int getSlotLimit(int slot) {
        return slot < AdvancedBrewingStandBlockEntity.POTIONS.length ? 2 : super.getSlotLimit(slot);
    }

    public int[] getSlotsForFace(Direction side) {
        switch (side) {
            case UP://ingredients are inserted from the top
                return AdvancedBrewingStandBlockEntity.INGREDIENTS;
            case DOWN://ingredients and potions can be extracted from bottom
                return INGREDIENTS_AND_POTIONS;
            default:
                return FUEL_AND_POTIONS;//fuel and potions can be added from any cardinal direction
        }
    }

    public boolean isItemValid(int index, ItemStack stack) {
        if (ArrayUtils.contains(AdvancedBrewingStandBlockEntity.INGREDIENTS,index)) {
            return BrewingRecipeRegistry.isValidIngredient(stack);
        } else {
            Item item = stack.getItem();
            if (index == AdvancedBrewingStandBlockEntity.FUEL) {
                return item == Items.BLAZE_POWDER;
            } else {
                //potion slots
                return ForgeUtil.isValidInputCountInsensitive(stack);
            }
        }
    }

    private static final int[] INGREDIENTS_AND_POTIONS;

    private static final int[] FUEL_AND_POTIONS;

    static {
       Set<Integer> ingSet = Arrays.stream(AdvancedBrewingStandBlockEntity.INGREDIENTS).boxed().collect(Collectors.toSet());
        Set<Integer> potSet = Arrays.stream(AdvancedBrewingStandBlockEntity.POTIONS).boxed().collect(Collectors.toSet());
        Set<Integer> union = Sets.union(ingSet,potSet);
        INGREDIENTS_AND_POTIONS = union.stream().mapToInt(i -> i).toArray();

        Set<Integer> potion_fuel = new HashSet<>(potSet);
        potion_fuel.add(AdvancedBrewingStandBlockEntity.FUEL);
        FUEL_AND_POTIONS = potion_fuel.stream().mapToInt(i -> i).toArray();
    }


}
