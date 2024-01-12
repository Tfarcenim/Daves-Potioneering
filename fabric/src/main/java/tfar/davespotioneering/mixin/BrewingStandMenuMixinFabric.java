package tfar.davespotioneering.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import tfar.davespotioneering.menu.CAdvancedBrewingStandMenu;

import javax.annotation.Nullable;

@Mixin(BrewingStandMenu.class)
public abstract class BrewingStandMenuMixinFabric extends AbstractContainerMenu {

    @Shadow @Final private Slot ingredientSlot;

    protected BrewingStandMenuMixinFabric(@Nullable MenuType<?> type, int id) {
        super(type, id);
    }

    //this is required because the brewing stand on fabric doesn't respect max stack sizes
    @Override
    protected boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        return CAdvancedBrewingStandMenu.patchedMoveTo(this,stack,startIndex,endIndex,reverseDirection);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if ((pIndex < 0 || pIndex > 2) && pIndex != 3 && pIndex != 4) {
                if (BrewingStandMenu.FuelSlot.mayPlaceItem(itemstack)) {
                    if (this.moveItemStackTo(itemstack1, 4, 5, false) || this.ingredientSlot.mayPlace(itemstack1) && !this.moveItemStackTo(itemstack1, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.ingredientSlot.mayPlace(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (BrewingStandMenu.PotionSlot.mayPlaceItem(itemstack)) {
                    if (!this.moveItemStackTo(itemstack1, 0, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (pIndex >= 5 && pIndex < 32) {
                    if (!this.moveItemStackTo(itemstack1, 32, 41, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (pIndex >= 32 && pIndex < 41) {
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
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(pPlayer, itemstack1);
        }

        return itemstack;
    }


    /**
     * @author tfar
     * @param instance stack
     * @return 1
     * @reason This change is made to allow potions to be shift-clicked in and out despite not having a stack size of 1
     */
  /*   @Redirect(method = "quickMoveStack",at = @At(value = "INVOKE",target = "Lnet/minecraft/world/item/ItemStack;getCount()I"))
     private int alwasys1(ItemStack instance) {
         return 1;
     }*/

}
