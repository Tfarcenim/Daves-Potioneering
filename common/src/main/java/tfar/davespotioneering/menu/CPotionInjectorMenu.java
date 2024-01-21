package tfar.davespotioneering.menu;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.PotionUtils2;
import tfar.davespotioneering.blockentity.CPotionInjectorBlockEntity;
import tfar.davespotioneering.init.ModMenuTypes;
import tfar.davespotioneering.inventory.BasicInventoryBridge;
import tfar.davespotioneering.item.CGauntletItem;
import tfar.davespotioneering.platform.Services;

import java.util.List;

public class CPotionInjectorMenu extends AbstractContainerMenu {

    private final BasicInventoryBridge inventory;

    //client
    public CPotionInjectorMenu(int id, Inventory playerInventory) {
        this(id, playerInventory, Services.PLATFORM.makePotionInjector(8));
    }

    //common
    public CPotionInjectorMenu(int id, Inventory playerInventory, BasicInventoryBridge inventory) {
        super(ModMenuTypes.ALCHEMICAL_GAUNTLET, id);
        this.inventory = inventory;
        // assertInventorySize(inventory, 5);
        // assertIntArraySize(data, 2);

        int potY = 77;

        for (int i = 0; i < 6;i++) {
            this.addSlot(Services.PLATFORM.makeBasic(inventory, i, 26 + 108 * (i/3), 18 + 18 * (i % 3)));
        }

        addSlot(Services.PLATFORM.makeBasic(inventory,6,80, 32));
        addSlot(Services.PLATFORM.makeBasic(inventory,7,80,55));

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
    public boolean stillValid(Player playerIn) {
        return true;
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < this.inventory.$getSlots()) {
                if (!this.moveItemStackTo(itemstack1, this.inventory.$getSlots(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, this.inventory.$getSlots(), false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
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
        ItemStack gauntlet = inventory.$getStackInSlot(CPotionInjectorBlockEntity.GAUNTLET);
        if (!gauntlet.isEmpty()) {
            CompoundTag newNbt = new CompoundTag();
            ListTag potionList = new ListTag();
            CompoundTag oldNBT = gauntlet.getOrCreateTag();

            CompoundTag info = oldNBT.getCompound(CGauntletItem.INFO);
            ListTag oldPotionList = info.getList(CGauntletItem.POTIONS, Tag.TAG_COMPOUND);

            for (int i = 0; i < CPotionInjectorBlockEntity.GAUNTLET; i++) {
                CompoundTag oldTag = oldPotionList.isEmpty() ? new CompoundTag() : oldPotionList.getCompound(i);
                Potion oldPotion = oldPotionList.isEmpty() ? Potions.EMPTY : PotionUtils.getPotion(oldTag);
                List<MobEffectInstance> oldCustomEffects = oldPotionList.isEmpty() ? List.of() : PotionUtils.getCustomEffects(oldTag);
                if (oldPotion == Potions.EMPTY && oldCustomEffects.isEmpty()) {
                    ItemStack potionStack = inventory.$getStackInSlot(i);
                    potionList.add(PotionUtils2.saveAllEffects(new CompoundTag(),PotionUtils.getPotion(potionStack),PotionUtils.getCustomEffects(potionStack)));
                    inventory.$extractItem(i, 1, false);
                    //copy old potion over
                } else {
                    potionList.add(oldPotionList.get(i));
                }
            }

            newNbt.putInt(CGauntletItem.ACTIVE_POTION, 0);
            newNbt.put(CGauntletItem.POTIONS, potionList);

            int presentBlaze = info.getInt(CGauntletItem.BLAZE);

            int blazeInsert = Math.min(BLAZE_CAP - presentBlaze,Math.min(BLAZE_CAP,inventory.$getStackInSlot(CPotionInjectorBlockEntity.BLAZE).getCount()));

            newNbt.putInt(CGauntletItem.BLAZE,blazeInsert + presentBlaze);
            inventory.$extractItem(CPotionInjectorBlockEntity.BLAZE,blazeInsert,false);
            System.out.println(potionList.getElementType());
            gauntlet.getTag().put(CGauntletItem.INFO,newNbt);
        }
    }

    private void removePotionsAndBlaze() {
        ItemStack gauntlet = inventory.$getStackInSlot(CPotionInjectorBlockEntity.GAUNTLET);
        if (!gauntlet.isEmpty()) {
            CompoundTag nbt = gauntlet.getTag().getCompound(CGauntletItem.INFO);
            ListTag listNBT = nbt.getList(CGauntletItem.POTIONS, Tag.TAG_COMPOUND);

            boolean allRemoved = true;
            for (int i = 0; i < listNBT.size(); i++) {
                CompoundTag inbt = listNBT.getCompound(i);

                Potion potion = PotionUtils.getPotion(inbt);
                List<MobEffectInstance> customEffects = PotionUtils.getCustomEffects(inbt);
                if (potion != Potions.EMPTY || !customEffects.isEmpty()) {
                    ItemStack present = inventory.$getStackInSlot(i);
                    if (present.getCount() < inventory.$getSlotLimit(i)) {
                        ItemStack stack = new ItemStack(Items.LINGERING_POTION);
                        PotionUtils.setPotion(stack, potion);
                        PotionUtils.setCustomEffects(stack,customEffects);
                        inventory.$insertItem(i, stack, false);
                        listNBT.set(i,new CompoundTag());
                    } else {
                        allRemoved = false;
                    }
                }
            }
            if (allRemoved) {
                nbt.remove(CGauntletItem.POTIONS);
            }

            int presentBlaze = inventory.$getStackInSlot(CPotionInjectorBlockEntity.BLAZE).getCount();

            int maxBlazeRemove = inventory.$getSlotLimit(CPotionInjectorBlockEntity.BLAZE) - presentBlaze;

            int blaze = nbt.getInt(CGauntletItem.BLAZE);

            int blazeRemove = Math.min(maxBlazeRemove,blaze);

            inventory.$insertItem(CPotionInjectorBlockEntity.BLAZE,new ItemStack(Items.BLAZE_POWDER,blazeRemove),false);

            if (blaze > blazeRemove) {
                nbt.putInt(CGauntletItem.BLAZE,blaze - blazeRemove);
            } else {
                nbt.remove(CGauntletItem.BLAZE);
            }
        }
    }

    /**
     * Merges provided ItemStack with the first available one in the container/player inventor between minIndex
     * (included) and maxIndex (excluded). Args : stack, minIndex, maxIndex, negativDirection. [!] the Container
     * implementation do not check if the item is valid for the slot
     *
     */
    protected boolean moveItemStackTo(ItemStack pStack, int pStartIndex, int pEndIndex, boolean pReverseDirection) {
        if (DavesPotioneering.isFabric) {
            return CAdvancedBrewingStandMenu.patchedMoveTo(this,pStack,pStartIndex,pEndIndex,pReverseDirection);
        }
        //don't use the patch on forge
        return super.moveItemStackTo(pStack,pStartIndex,pEndIndex,pReverseDirection);
    }

    public SoundTy getSound(boolean inject) {
        if (inject) {
            for (int i = 0; i < 6; i++) {
                if (!inventory.$getStackInSlot(i).isEmpty()) {
                    return SoundTy.BOTH;
                }
            }

            return inventory.$getStackInSlot(CPotionInjectorBlockEntity.BLAZE).isEmpty() ? SoundTy.NONE : SoundTy.BLAZE;


        } else {
            ItemStack stack = inventory.$getStackInSlot(CPotionInjectorBlockEntity.GAUNTLET);
            CompoundTag nbt = stack.getTag();
            if (nbt != null) {
                CompoundTag info = nbt.getCompound(CGauntletItem.INFO);
                ListTag listNBT = info.getList(CGauntletItem.POTIONS, Tag.TAG_COMPOUND);
                if (!listNBT.isEmpty()) {
                    for (Tag nb : listNBT) {
                        CompoundTag tag = (CompoundTag)nb;
                        Potion potion = PotionUtils.getPotion(tag);
                        if (potion != Potions.EMPTY) {
                            return SoundTy.BOTH;
                        }
                    }
                }
                if (info.getInt(CGauntletItem.BLAZE) > 0) {
                    return SoundTy.BLAZE;
                }
            }
            return SoundTy.NONE;
        }
    }

    public enum SoundTy {
     NONE,BLAZE,BOTH
    }
}
