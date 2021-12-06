package tfar.davespotioneering.mixin;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.BrewingStandContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BrewingStandContainer.class)
public interface BrewingStandContainerAccess {

    @Accessor IInventory getTileBrewingStand();

}
