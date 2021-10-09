package tfar.davespotioneering.mixin;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(TileEntityType.class)
public interface BlockEntityTypeAcces {
    @Accessor @Mutable void setValidBlocks(Set<Block> blocks);

    @Accessor Set<Block> getValidBlocks();

}
