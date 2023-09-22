package tfar.davespotioneering.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Gui.class)
public interface IngameGuiAccess {
    @Accessor int getToolHighlightTimer();
}
