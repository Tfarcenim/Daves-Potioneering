package tfar.davespotioneering.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.BrewingStandMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BrewingStandMenu.class)
public interface BrewingStandContainerAccess {

    @Accessor Container getTileBrewingStand();

}
