package tfar.davespotioneering.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import tfar.davespotioneering.blockentity.AdvancedBrewingStandBlockEntity;
import tfar.davespotioneering.blockentity.CAdvancedBrewingStandBlockEntity;
import tfar.davespotioneering.init.ModContainerTypes;
import tfar.davespotioneering.inv.BrewingHandler;

public class AdvancedBrewingStandContainer extends AbstractContainerMenu {
    private final ContainerData data;

    public CAdvancedBrewingStandBlockEntity blockEntity;

    //client
    public AdvancedBrewingStandContainer(int id, Inventory playerInventory) {
        this(id, playerInventory, new BrewingHandler(AdvancedBrewingStandBlockEntity.SLOTS), new SimpleContainerData(2),null);
    }

    //common
    public AdvancedBrewingStandContainer(int id, Inventory playerInventory, Container inventory, ContainerData data, AdvancedBrewingStandBlockEntity advancedBrewingStandBlockEntity) {
        super(ModContainerTypes.ADVANCED_BREWING_STAND, id);
       // assertInventorySize(inventory, 5);
       // assertIntArraySize(data, 2);
        this.data = data;


        int potY = 77;

        this.addSlot(new PotionSlot(inventory, 0, 56, potY));
        this.addSlot(new PotionSlot(inventory, 1, 79, potY + 7));
        this.addSlot(new PotionSlot(inventory, 2, 102, potY));


        for (int i = 3; i < 3 + 4;i++) {
            this.addSlot(new IngredientSlot(inventory, i, 22 * i - 20, 17));
        }

        int ing1 = 43;

        this.addSlot(new IngredientSlot(inventory, 7, 79, ing1));

        this.addSlot(new BrewingStandMenu.FuelSlot(inventory, AdvancedBrewingStandBlockEntity.FUEL, 17, ing1));

        this.addDataSlots(data);

        int invX = 8;
        int invY = 110;

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, invX + j * 18, invY + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, invX + k * 18, invY + 58));
        }
    }

    /**
     * Determines whether supplied player can use this container
     */
    public boolean stillValid(Player playerIn) {
        return true;//this.tileBrewingStand.isUsableByPlayer(playerIn);
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();
            if (index < AdvancedBrewingStandBlockEntity.SLOTS) {
                if (!this.moveItemStackTo(slotStack, AdvancedBrewingStandBlockEntity.SLOTS, 41, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(slotStack, 0, AdvancedBrewingStandBlockEntity.SLOTS, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(slotStack, itemstack);
            }

            if (slotStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, slotStack);
        }

        return itemstack;
    }

    public int getFuel() {
        return this.data.get(1);
    }

    public int getBrewTime() {
        return this.data.get(0);
    }

    static class IngredientSlot extends Slot {
        public IngredientSlot(Container iInventoryIn, int index, int xPosition, int yPosition) {
            super(iInventoryIn, index, xPosition, yPosition);
        }

        /**
         * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
         */
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() == Items.MILK_BUCKET || PotionBrewing.isIngredient(stack);
        }
    }

    public static class PotionSlot extends BrewingStandMenu.PotionSlot {
        public PotionSlot(Container iItemHandler, int index, int x, int y) {
            super(iItemHandler, index, x, y);
        }

        /**
         * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the
         * case of armor slots)
         */
        public int getMaxStackSize() {
            return 2;
        }
    }
}
