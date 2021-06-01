package tfar.davespotioneering.menu;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.BrewingStandContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import tfar.davespotioneering.init.ModContainerTypes;

public class AdvancedBrewingStandContainer extends Container {
    private final ItemStackHandler tileBrewingStand;
    private final IIntArray data;
    private final Slot slot;

    //client
    public AdvancedBrewingStandContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, new ItemStackHandler(4+4), new IntArray(2));
    }

    //common
    public AdvancedBrewingStandContainer(int id, PlayerInventory playerInventory, ItemStackHandler inventory, IIntArray data) {
        super(ModContainerTypes.ADVANCED_BREWING_STAND, id);
       // assertInventorySize(inventory, 5);
       // assertIntArraySize(data, 2);
        this.tileBrewingStand = inventory;
        this.data = data;

        int potY = 103;

        this.addSlot(new PotionSlot(inventory, 0, 56, potY));
        this.addSlot(new PotionSlot(inventory, 1, 79, potY + 7));
        this.addSlot(new PotionSlot(inventory, 2, 102, potY));

        int ingY = 15;

        this.slot = this.addSlot(new IngredientSlot(inventory, 3, 79, ingY));
        this.addSlot(new IngredientSlot(inventory, 4, 79, ingY + 18 * 1));
        this.addSlot(new IngredientSlot(inventory, 5, 79, ingY + 18 * 2));
        this.addSlot(new IngredientSlot(inventory, 6, 79, ingY + 18 * 3));

        this.addSlot(new FuelSlot(inventory, 7, 17, ingY + 18 * 3));

        this.trackIntArray(data);

        int invX = 8;
        int invY = 136;

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
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;//this.tileBrewingStand.isUsableByPlayer(playerIn);
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            itemstack = slotStack.copy();
            if (index > 2 && index != 3 && index != 4) {
                if (BrewingStandContainer.FuelSlot.isValidBrewingFuel(itemstack)) {
                    if (this.mergeItemStack(slotStack, 4, 5, false) || this.slot.isItemValid(slotStack) && !this.mergeItemStack(slotStack, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.slot.isItemValid(slotStack)) {
                    if (!this.mergeItemStack(slotStack, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (PotionSlot.canHoldPotion(itemstack) && itemstack.getCount() == 1) {
                    if (!this.mergeItemStack(slotStack, 0, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 5 && index < 32) {
                    if (!this.mergeItemStack(slotStack, 32, 41, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 32 && index < 41) {
                    if (!this.mergeItemStack(slotStack, 5, 32, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.mergeItemStack(slotStack, 5, 41, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.mergeItemStack(slotStack, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(slotStack, itemstack);
            }

            if (slotStack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
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

    public static class FuelSlot extends SlotItemHandler {
        public FuelSlot(IItemHandler iInventoryIn, int index, int xPosition, int yPosition) {
            super(iInventoryIn, index, xPosition, yPosition);
        }

        /**
         * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
         */
        public boolean isItemValid(ItemStack stack) {
            return isValidBrewingFuel(stack);
        }

        /**
         * Returns true if the given ItemStack is usable as a fuel in the brewing stand.
         */
        public static boolean isValidBrewingFuel(ItemStack itemStackIn) {
            return itemStackIn.getItem() == Items.BLAZE_POWDER;
        }

        /**
         * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the
         * case of armor slots)
         */
        public int getSlotStackLimit() {
            return 64;
        }
    }

    static class IngredientSlot extends SlotItemHandler {
        public IngredientSlot(IItemHandler iInventoryIn, int index, int xPosition, int yPosition) {
            super(iInventoryIn, index, xPosition, yPosition);
        }

        /**
         * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
         */
        public boolean isItemValid(ItemStack stack) {
            return net.minecraftforge.common.brewing.BrewingRecipeRegistry.isValidIngredient(stack);
        }

        /**
         * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the
         * case of armor slots)
         */
        public int getSlotStackLimit() {
            return 64;
        }
    }

    public static class PotionSlot extends SlotItemHandler {
        public PotionSlot(IItemHandler p_i47598_1_, int p_i47598_2_, int p_i47598_3_, int p_i47598_4_) {
            super(p_i47598_1_, p_i47598_2_, p_i47598_3_, p_i47598_4_);
        }

        /**
         * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
         */
        public boolean isItemValid(ItemStack stack) {
            return canHoldPotion(stack);
        }

        /**
         * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the
         * case of armor slots)
         */
        public int getSlotStackLimit() {
            return 1;
        }

        public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
            Potion potion = PotionUtils.getPotionFromItem(stack);
            if (thePlayer instanceof ServerPlayerEntity) {
                net.minecraftforge.event.ForgeEventFactory.onPlayerBrewedPotion(thePlayer, stack);
                CriteriaTriggers.BREWED_POTION.trigger((ServerPlayerEntity)thePlayer, potion);
            }

            super.onTake(thePlayer, stack);
            return stack;
        }

        /**
         * Returns true if this itemstack can be filled with a potion
         */
        public static boolean canHoldPotion(ItemStack stack) {
            return net.minecraftforge.common.brewing.BrewingRecipeRegistry.isValidInput(stack);
        }
    }
}
