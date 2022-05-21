package tfar.davespotioneering.mixin;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SimpleInventory.class)
public interface SimpleContainerAccess {

    @Accessor DefaultedList<ItemStack> getItems();

    @Accessor @Mutable void setItems(DefaultedList<ItemStack> stacks);

    @Accessor @Mutable void setSize(int slots);

}
