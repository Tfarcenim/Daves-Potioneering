package tfar.davespotioneering.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(BlockEntityType.class)
public interface BlockEntityTypeAcces {
    @Accessor @Mutable void setBlocks(Set<Block> blocks);

    @Accessor Set<Block> getBlocks();

}
