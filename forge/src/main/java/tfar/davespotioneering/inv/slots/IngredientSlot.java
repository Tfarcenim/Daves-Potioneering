package tfar.davespotioneering.inv.slots;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class IngredientSlot extends SlotItemHandler {
    public IngredientSlot(IItemHandler iInventoryIn, int index, int xPosition, int yPosition) {
        super(iInventoryIn, index, xPosition, yPosition);
    }

    /**
     * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
     */
    public boolean mayPlace(ItemStack stack) {
        return BrewingRecipeRegistry.isValidIngredient(stack);
    }
}
