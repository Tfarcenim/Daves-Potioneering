package tfar.davespotioneering.menu;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import tfar.davespotioneering.init.ModContainerTypes;
import tfar.davespotioneering.inv.PotionInjectorHandler;
import tfar.davespotioneering.inv.SingleItemHandler;

public class GauntletMenu extends Container {

    private final ItemStackHandler inventory;

    //client
    public GauntletMenu(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, new PotionInjectorHandler(8));
    }

    //common
    public GauntletMenu(int id, PlayerInventory playerInventory, ItemStackHandler inventory) {
        super(ModContainerTypes.ALCHEMICAL_GAUNTLET, id);
        this.inventory = inventory;
        // assertInventorySize(inventory, 5);
        // assertIntArraySize(data, 2);

        int potY = 77;

        for (int i = 0; i < 6;i++) {
            this.addSlot(new SlotItemHandler(inventory, i, 26 + 108 * (i/3), 18 + 18 * (i % 3)));
        }

        addSlot(new SlotItemHandler(inventory,6,80,32));
        addSlot(new SlotItemHandler(inventory,7,80,55));

        int invX = 8;
        int invY = 109;

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, invX + j * 18, invY + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, invX + k * 18, invY + 58));
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index < this.inventory.getSlots()) {
                if (!this.mergeItemStack(itemstack1, this.inventory.getSlots(), this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, this.inventory.getSlots(), false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    public void handleButton(int button) {
        switch (button) {
            case 0:
                 storePotionsAndBlaze();
                 break;
            case 1:
                removePotionsAndBlaze();
                break;
        }
    }

    private void storePotionsAndBlaze() {
        ItemStack gauntlet = inventory.getStackInSlot(PotionInjectorHandler.GAUNTLET);
        if (!gauntlet.isEmpty()) {
            CompoundNBT nbt = new CompoundNBT();
            ListNBT nbt1 = new ListNBT();
            for (int i = 0; i < PotionInjectorHandler.GAUNTLET;i++) {
                ItemStack potionStack = inventory.getStackInSlot(i);
                nbt1.add(StringNBT.valueOf(PotionUtils.getPotionFromItem(potionStack).getRegistryName().toString()));
                inventory.extractItem(i,64,false);
            }
            nbt.put("potions",nbt1);
            nbt.putInt("blaze",inventory.getStackInSlot(PotionInjectorHandler.BLAZE).getCount());
            inventory.extractItem(PotionInjectorHandler.BLAZE,64,false);
            gauntlet.getOrCreateTag().put("info",nbt);
        }
    }

    private void removePotionsAndBlaze() {
        ItemStack gauntlet = inventory.getStackInSlot(PotionInjectorHandler.GAUNTLET);
        if (!gauntlet.isEmpty()) {
            CompoundNBT nbt = gauntlet.getTag().getCompound("info");
            nbt.remove("potions");
            int blaze = nbt.getInt("blaze");
            inventory.insertItem(PotionInjectorHandler.BLAZE,new ItemStack(Items.BLAZE_POWDER,blaze),false);
            nbt.remove("blaze");
        }
    }
}
