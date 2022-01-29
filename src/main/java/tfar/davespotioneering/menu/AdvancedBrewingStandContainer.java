package tfar.davespotioneering.menu;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import tfar.davespotioneering.Util;
import tfar.davespotioneering.blockentity.AdvancedBrewingStandBlockEntity;
import tfar.davespotioneering.init.ModContainerTypes;
import tfar.davespotioneering.inv.BrewingHandler;

public class AdvancedBrewingStandContainer extends Container {
    private final ItemStackHandler tileBrewingStand;
    private final IIntArray data;

    public AdvancedBrewingStandBlockEntity blockEntity;

    //client
    public AdvancedBrewingStandContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, new BrewingHandler(AdvancedBrewingStandBlockEntity.SLOTS), new IntArray(2),null);
    }

    //common
    public AdvancedBrewingStandContainer(int id, PlayerInventory playerInventory, ItemStackHandler inventory, IIntArray data, AdvancedBrewingStandBlockEntity advancedBrewingStandBlockEntity) {
        super(ModContainerTypes.ADVANCED_BREWING_STAND, id);
       // assertInventorySize(inventory, 5);
       // assertIntArraySize(data, 2);
        this.tileBrewingStand = inventory;
        this.data = data;

        this.blockEntity = advancedBrewingStandBlockEntity;

        int potY = 77;

        this.addSlot(new PotionSlot(inventory, 0, 56, potY));
        this.addSlot(new PotionSlot(inventory, 1, 79, potY + 7));
        this.addSlot(new PotionSlot(inventory, 2, 102, potY));


        for (int i = 3; i < 3 + 4;i++) {
            this.addSlot(new IngredientSlot(inventory, i, 22 * i - 20, 17));
        }

        int ing1 = 43;

        this.addSlot(new IngredientSlot(inventory, 7, 79, ing1));

        this.addSlot(new FuelSlot(inventory, AdvancedBrewingStandBlockEntity.FUEL, 17, ing1));

        this.trackIntArray(data);

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
            if (index < AdvancedBrewingStandBlockEntity.SLOTS) {
                if (!this.mergeItemStack(slotStack, AdvancedBrewingStandBlockEntity.SLOTS, 41, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.mergeItemStack(slotStack, 0, AdvancedBrewingStandBlockEntity.SLOTS, true)) {
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
            return stack.getItem() == Items.MILK_BUCKET || BrewingRecipeRegistry.isValidIngredient(stack);
        }
    }

    public static class PotionSlot extends SlotItemHandler {
        public PotionSlot(IItemHandler iItemHandler, int index, int x, int y) {
            super(iItemHandler, index, x, y);
        }

        /**
         * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
         */
        public boolean isItemValid(ItemStack stack) {
            return Util.isValidInputCountInsensitive(stack);
        }

        /**
         * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the
         * case of armor slots)
         */
        public int getSlotStackLimit() {
            return 2;
        }

        public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
            Potion potion = PotionUtils.getPotionFromItem(stack);
            if (thePlayer instanceof ServerPlayerEntity) {
                ForgeEventFactory.onPlayerBrewedPotion(thePlayer, stack);
                CriteriaTriggers.BREWED_POTION.trigger((ServerPlayerEntity)thePlayer, potion);
            }

            super.onTake(thePlayer, stack);
            return stack;
        }
    }
}
