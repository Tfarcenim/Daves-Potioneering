package tfar.davespotioneering.menu;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import tfar.davespotioneering.init.ModContainerTypes;
import tfar.davespotioneering.inv.InventorySlot;
import tfar.davespotioneering.inv.PotionInjectorHandler;

public class PotionInjectorMenu extends ScreenHandler {

    private final PotionInjectorHandler inventory;

    //client
    public PotionInjectorMenu(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, new PotionInjectorHandler(8));
    }

    //common
    public PotionInjectorMenu(int id, PlayerInventory playerInventory, PotionInjectorHandler inventory) {
        super(ModContainerTypes.ALCHEMICAL_GAUNTLET, id);
        this.inventory = inventory;
        // assertInventorySize(inventory, 5);
        // assertIntArraySize(data, 2);

        int potY = 77;

        //potion slots
        for (int i = 0; i < 6;i++) {
            this.addSlot(new InventorySlot(inventory, i, 26 + 108 * (i/3), 18 + 18 * (i % 3)));
        }

        addSlot(new InventorySlot(inventory,PotionInjectorHandler.GAUNTLET,80, 32));
        addSlot(new InventorySlot(inventory,PotionInjectorHandler.BLAZE,80,55));

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
    public boolean canUse(PlayerEntity playerIn) {
        return true;
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    @Override
    public ItemStack transferSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index < this.inventory.size()) {
                if (!this.insertItem(itemstack1, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemstack1, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
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
        ItemStack gauntlet = inventory.getStack(PotionInjectorHandler.GAUNTLET);
        if (!gauntlet.isEmpty()) {
            CompoundTag newNbt = new CompoundTag();
            ListTag nbt1 = new ListTag();
            CompoundTag oldNBT = gauntlet.getOrCreateTag();

            CompoundTag info = oldNBT.getCompound("info");
            ListTag oldList = info.getList("potions", 10);

            for (int i = 0; i < PotionInjectorHandler.GAUNTLET; i++) {

                Potion oldPotion = oldList.isEmpty() ? Potions.EMPTY : Registry.POTION.get(new Identifier(oldList.get(i).asString()));

                if (oldPotion == Potions.EMPTY) {
                    ItemStack potionStack = inventory.getStack(i);
                    nbt1.add(StringTag.of(Registry.POTION.getId(PotionUtil.getPotion(potionStack)).toString()));
                    inventory.removeStack(i, 1);
                    //copy old potion over
                } else {
                    nbt1.add(oldList.get(i));
                }
            }

            newNbt.putInt("activePotionIndex", 0);
            newNbt.put("potions", nbt1);

            int presentBlaze = gauntlet.getMaxDamage() - gauntlet.getDamage();

            int blazeInsert = Math.min(BLAZE_CAP - presentBlaze,Math.min(BLAZE_CAP,inventory.getStack(PotionInjectorHandler.BLAZE).getCount()));

            gauntlet.setDamage(gauntlet.getMaxDamage() - (blazeInsert + presentBlaze));
            inventory.removeStack(PotionInjectorHandler.BLAZE,blazeInsert);
            gauntlet.getTag().put("info",newNbt);
        }
    }

    public static final byte TAG_STRING = 8;//get this from Tag in 1.18

    private void removePotionsAndBlaze() {
        ItemStack gauntlet = inventory.getStack(PotionInjectorHandler.GAUNTLET);
        if (!gauntlet.isEmpty()) {
            CompoundTag nbt = gauntlet.getTag().getCompound("info");
            ListTag listNBT = nbt.getList("potions", TAG_STRING);

            boolean allRemoved = true;
            for (int i = 0; i < listNBT.size(); i++) {
                Tag inbt = listNBT.get(i);

                Potion potion = Registry.POTION.get(new Identifier(inbt.asString()));
                if (potion != Potions.EMPTY) {
                    ItemStack present = inventory.getStack(i);
                    if (present.getCount() < inventory.getMaxCountPerStack()) {
                        ItemStack stack = new ItemStack(Items.LINGERING_POTION);
                        PotionUtil.setPotion(stack, potion);
                       inventory.insertItem(i, stack, false);
                        listNBT.set(i,StringTag.of(Registry.POTION.getId(Potions.EMPTY).toString()));
                    } else {
                        allRemoved = false;
                    }
                }
            }
            if (allRemoved) {
                nbt.remove("potions");
            }

            int presentBlaze = inventory.getStack(PotionInjectorHandler.BLAZE).getCount();

            int maxBlazeRemove = inventory.getMaxCountPerStack() - presentBlaze;

            int blaze = gauntlet.getMaxDamage() - gauntlet.getDamage();

            int blazeRemove = Math.min(maxBlazeRemove,blaze);

            inventory.insertItem(PotionInjectorHandler.BLAZE,new ItemStack(Items.BLAZE_POWDER,blazeRemove),false);

            if (blaze > blazeRemove) {
               gauntlet.setDamage(gauntlet.getMaxDamage() - (blaze - blazeRemove));
            } else {
                gauntlet.setDamage(gauntlet.getMaxDamage());
            }
        }
    }

    public SoundTy getSound(boolean inject) {
        if (inject) {
            for (int i = 0; i < 6; i++) {
                if (!inventory.getStack(i).isEmpty()) {
                    return SoundTy.BOTH;
                }
            }

            return inventory.getStack(PotionInjectorHandler.BLAZE).isEmpty() ? SoundTy.NONE : SoundTy.BLAZE;


        } else {
            ItemStack stack = inventory.getStack(PotionInjectorHandler.GAUNTLET);
            CompoundTag nbt = stack.getTag();
            if (nbt != null) {
                CompoundTag info = nbt.getCompound("info");
                ListTag listNBT = info.getList("potions", TAG_STRING);
                if (!listNBT.isEmpty()) {
                    for (Tag nb : listNBT) {
                        Potion potion = Registry.POTION.get(new Identifier(nb.asString()));
                        if (potion != Potions.EMPTY) {
                            return SoundTy.BOTH;
                        }
                    }
                }
                if (info.getInt("blaze") > 0) {
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
