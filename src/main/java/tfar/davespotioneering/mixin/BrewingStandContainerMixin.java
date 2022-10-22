package tfar.davespotioneering.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BrewingStandScreenHandler.class)
abstract class BrewingStandContainerMixin extends ScreenHandler {

    protected BrewingStandContainerMixin(@Nullable ScreenHandlerType<?> screenHandlerType, int i) {
        super(screenHandlerType, i);
    }

    /**
     * @author tfar
     * @param instance stack
     * @return 1
     * @reason This change is made to allow potions to be shift-clicked in and out despite not having a stack size of 1
     */
    @Redirect(method = "transferSlot",at = @At(value = "INVOKE",target = "Lnet/minecraft/item/ItemStack;getCount()I"))
    private int alwasys1(ItemStack instance) {
        return 1;
    }
}
