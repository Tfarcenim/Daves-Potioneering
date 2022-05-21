package tfar.davespotioneering.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;

@Mixin(BlockEntityType.class)
public interface BlockEntityTypeAcces {
    @Accessor @Mutable void setValidBlocks(Set<Block> blocks);

    @Accessor Set<Block> getValidBlocks();

}
