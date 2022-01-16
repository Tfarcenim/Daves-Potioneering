package tfar.davespotioneering.menu;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import tfar.davespotioneering.init.ModContainerTypes;
import tfar.davespotioneering.inv.PotionInjectorHandler;

public class PotionInjectorMenu extends Container {

    private final ItemStackHandler inventory;

    //client
    public PotionInjectorMenu(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, new PotionInjectorHandler(8));
    }

    //common
    public PotionInjectorMenu(int id, PlayerInventory playerInventory, ItemStackHandler inventory) {
        super(ModContainerTypes.ALCHEMICAL_GAUNTLET, id);
        this.inventory = inventory;
        // assertInventorySize(inventory, 5);
        // assertIntArraySize(data, 2);

        int potY = 77;

        for (int i = 0; i < 6;i++) {
            this.addSlot(new SlotItemHandler(inventory, i, 26 + 108 * (i/3), 18 + 18 * (i % 3)));
        }

        addSlot(new SlotItemHandler(inventory,6,80, 32));
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

    public static final int BLAZE_CAP = 32;

    private void storePotionsAndBlaze() {
        ItemStack gauntlet = inventory.getStackInSlot(PotionInjectorHandler.GAUNTLET);
        if (!gauntlet.isEmpty()) {
            CompoundNBT newNbt = new CompoundNBT();
            ListNBT nbt1 = new ListNBT();
            CompoundNBT oldNBT = gauntlet.getOrCreateTag();

            CompoundNBT info = oldNBT.getCompound("info");
            ListNBT oldList = info.getList("potions", Constants.NBT.TAG_STRING);

            for (int i = 0; i < PotionInjectorHandler.GAUNTLET; i++) {

                Potion oldPotion = oldList.isEmpty() ? Potions.EMPTY : Registry.POTION.getOrDefault(new ResourceLocation(oldList.get(i).getString()));

                if (oldPotion == Potions.EMPTY) {
                    ItemStack potionStack = inventory.getStackInSlot(i);
                    nbt1.add(StringNBT.valueOf(PotionUtils.getPotionFromItem(potionStack).getRegistryName().toString()));
                    inventory.extractItem(i, 1, false);
                    //copy old potion over
                } else {
                    nbt1.add(oldList.get(i));
                }
            }

            newNbt.putInt("activePotionIndex", 0);
            newNbt.put("potions", nbt1);

            int presentBlaze = info.getInt("blaze");

            int blazeInsert = Math.min(BLAZE_CAP - presentBlaze,Math.min(BLAZE_CAP,inventory.getStackInSlot(PotionInjectorHandler.BLAZE).getCount()));

            newNbt.putInt("blaze",blazeInsert + presentBlaze);
            inventory.extractItem(PotionInjectorHandler.BLAZE,blazeInsert,false);
            gauntlet.getTag().put("info",newNbt);
        }
    }

    private void removePotionsAndBlaze() {
        ItemStack gauntlet = inventory.getStackInSlot(PotionInjectorHandler.GAUNTLET);
        if (!gauntlet.isEmpty()) {
            CompoundNBT nbt = gauntlet.getTag().getCompound("info");
            ListNBT listNBT = nbt.getList("potions", Constants.NBT.TAG_STRING);

            boolean allRemoved = true;
            for (int i = 0; i < listNBT.size(); i++) {
                INBT inbt = listNBT.get(i);

                Potion potion = Registry.POTION.getOrDefault(new ResourceLocation(inbt.getString()));
                if (potion != Potions.EMPTY) {
                    ItemStack present = inventory.getStackInSlot(i);
                    if (present.getCount() < inventory.getSlotLimit(i)) {
                        ItemStack stack = new ItemStack(Items.LINGERING_POTION);
                        PotionUtils.addPotionToItemStack(stack, potion);
                        inventory.insertItem(i, stack, false);
                        listNBT.set(i,StringNBT.valueOf(Potions.EMPTY.getRegistryName().toString()));
                    } else {
                        allRemoved = false;
                    }
                }
            }
            if (allRemoved) {
                nbt.remove("potions");
            }

            int presentBlaze = inventory.getStackInSlot(PotionInjectorHandler.BLAZE).getCount();

            int maxBlazeRemove = inventory.getSlotLimit(PotionInjectorHandler.BLAZE) - presentBlaze;

            int blaze = nbt.getInt("blaze");

            int blazeRemove = Math.min(maxBlazeRemove,blaze);

            inventory.insertItem(PotionInjectorHandler.BLAZE,new ItemStack(Items.BLAZE_POWDER,blazeRemove),false);

            if (blaze > blazeRemove) {
                nbt.putInt("blaze",blaze - blazeRemove);
            } else {
                nbt.remove("blaze");
            }
        }
    }

    public boolean blazeOnly(boolean inject){
        if (inject) {
            for (int i = 0; i < 6; i++) {
                if (!inventory.getStackInSlot(i).isEmpty()) {
                    return false;
                }
            }
        } else {
            ItemStack stack = inventory.getStackInSlot(PotionInjectorHandler.GAUNTLET);
            CompoundNBT nbt = stack.getTag();
            if (nbt != null) {
                CompoundNBT info = nbt.getCompound("info");
                ListNBT listNBT = info.getList("potions", Constants.NBT.TAG_STRING);
                return listNBT.isEmpty();
            }
        }
        return true;
    }
}
