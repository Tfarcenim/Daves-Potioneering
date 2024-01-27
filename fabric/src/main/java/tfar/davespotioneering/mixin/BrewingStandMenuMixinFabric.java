package tfar.davespotioneering.mixin;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tfar.davespotioneering.menu.CAdvancedBrewingStandMenu;

import javax.annotation.Nullable;

@Mixin(BrewingStandMenu.class)
public abstract class BrewingStandMenuMixinFabric extends AbstractContainerMenu {

    protected BrewingStandMenuMixinFabric(@Nullable MenuType<?> type, int id) {
        super(type, id);
    }

    //this is required because the brewing stand on fabric doesn't respect max stack sizes
    @Override
    protected boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        return CAdvancedBrewingStandMenu.patchedMoveTo(this,stack,startIndex,endIndex,reverseDirection);
    }

    /**
     * @author tfar
     * @param instance stack
     * @return 1
     * @reason This change is made to allow potions to be shift-clicked in and out despite not having a stack size of 1
     */
    @Redirect(method = "quickMoveStack",at = @At(value = "INVOKE",target = "Lnet/minecraft/world/item/ItemStack;getCount()I",ordinal = 0))
    private int always1(ItemStack instance) {
        return 1;
    }
}
