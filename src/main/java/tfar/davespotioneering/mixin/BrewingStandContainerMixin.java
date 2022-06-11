package tfar.davespotioneering.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(BrewingStandScreenHandler.class)
abstract class BrewingStandContainerMixin extends ScreenHandler {

    @Shadow @Final private Slot ingredientSlot;

    protected BrewingStandContainerMixin(@Nullable ScreenHandlerType<?> type, int id) {
        super(type, id);
    }

    /**
     * @author tfar
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     *
     * @reason This change is made to allow potions to be shift-clicked in and out despite not having a stack size of 1
     *
     */
    @Overwrite
    public ItemStack transferSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index > 2 && index != 3 && index != 4) {
                if (BrewingStandScreenHandler.FuelSlot.matches(itemstack)) {
                    if (this.insertItem(itemstack1, 4, 5, false) || this.ingredientSlot.canInsert(itemstack1) && !this.insertItem(itemstack1, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.ingredientSlot.canInsert(itemstack1)) {
                    if (!this.insertItem(itemstack1, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (BrewingStandScreenHandler.PotionSlot.matches(itemstack) /*&& itemstack.getCount() == 1*/) { //<--- The change is here
                    if (!this.insertItem(itemstack1, 0, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 32) {
                    if (!this.insertItem(itemstack1, 32, 41, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 41) {
                    if (!this.insertItem(itemstack1, 5, 32, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.insertItem(itemstack1, 5, 41, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.insertItem(itemstack1, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onStackChanged(itemstack1, itemstack);
            }

            if (itemstack1.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(playerIn, itemstack1);
        }

        return itemstack;
    }

}
