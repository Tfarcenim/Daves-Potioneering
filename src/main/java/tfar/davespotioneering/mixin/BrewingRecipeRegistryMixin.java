package tfar.davespotioneering.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BrewingRecipeRegistry.class)
public class BrewingRecipeRegistryMixin {

    //makes conditional in BrewingRecipeRegistry always return 1 so that shift clicking and item transfer work better
    @Redirect(method = "isValidInput",at = @At(value = "INVOKE",target = "Lnet/minecraft/world/item/ItemStack;getCount()I"))
    private static int always1(ItemStack stack) {
        return 1;
    }

    //makes conditional in BrewingRecipeRegistry always return 1 so brewing check works for >1 potion
    @Redirect(method = "getOutput",at = @At(value = "INVOKE",target = "Lnet/minecraft/world/item/ItemStack;getCount()I"))
    private static int alwaysOne(ItemStack stack) {
        return 1;
    }
}
