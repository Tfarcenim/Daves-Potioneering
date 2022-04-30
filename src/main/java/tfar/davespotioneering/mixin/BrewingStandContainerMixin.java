package tfar.davespotioneering.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(BrewingStandMenu.class)
abstract class BrewingStandContainerMixin extends AbstractContainerMenu {

    @Shadow @Final private Slot slot;

    protected BrewingStandContainerMixin(@Nullable MenuType<?> type, int id) {
        super(type, id);
    }

    /**
     * @author tfar
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     *
     *
     */
    @Overwrite
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index > 2 && index != 3 && index != 4) {
                if (BrewingStandMenu.FuelSlot.mayPlaceItem(itemstack)) {
                    if (this.moveItemStackTo(itemstack1, 4, 5, false) || this.slot.mayPlace(itemstack1) && !this.moveItemStackTo(itemstack1, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.slot.mayPlace(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (BrewingStandMenu.PotionSlot.mayPlaceItem(itemstack) /*&& itemstack.getCount() == 1*/) { //<--- The change is here
                    if (!this.moveItemStackTo(itemstack1, 0, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 32) {
                    if (!this.moveItemStackTo(itemstack1, 32, 41, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 41) {
                    if (!this.moveItemStackTo(itemstack1, 5, 32, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, 5, 41, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(itemstack1, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

}
