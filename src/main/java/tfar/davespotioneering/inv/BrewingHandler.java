package tfar.davespotioneering.inv;

import com.google.common.collect.Sets;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.ArrayUtils;
import tfar.davespotioneering.Util;
import tfar.davespotioneering.blockentity.AdvancedBrewingStandBlockEntity;
import tfar.davespotioneering.mixin.SimpleContainerAccess;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class BrewingHandler extends SimpleInventory {

    public BrewingHandler(int size) {
        super(size);
    }

    public DefaultedList<ItemStack> getItems() {
        return ((SimpleContainerAccess)this).getStacks();
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
                return Util.isValidInputCountInsensitive(stack);
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

    public void readTags(ListTag tags) {
        for (int i = 0; i < tags.size(); i++)
        {
            CompoundTag itemTags = tags.getCompound(i);
            int slot = itemTags.getInt("Slot");

            if (slot >= 0 && slot < getItems().size())
            {
                getItems().set(i,ItemStack.fromTag(itemTags));
            }
        }
    }

    public ListTag getTags() {
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < this.size(); i++)
        {
            if (!getItems().get(i).isEmpty())
            {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                getItems().get(i).toTag(itemTag);
                nbtTagList.add(itemTag);
            }
        }
        return nbtTagList;
    }

}
