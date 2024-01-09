package tfar.davespotioneering.mixin;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;

@Mixin(BrewingStandMenu.class)
abstract class BrewingStandContainerMixin extends AbstractContainerMenu {

    protected BrewingStandContainerMixin(@Nullable MenuType<?> type, int id) {
        super(type, id);
    }

    @Redirect(method = "quickMoveStack",at = @At(value = "INVOKE",target = "Lnet/minecraft/world/item/ItemStack;getCount()I"))
    private int always1(ItemStack stack) {
        return 1;
    }

}
