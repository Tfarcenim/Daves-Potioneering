package tfar.davespotioneering.mixin;

import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Item.class)
public interface ItemAccess {
	@Accessor void setMaxStackSize(int newStackSize);
}
