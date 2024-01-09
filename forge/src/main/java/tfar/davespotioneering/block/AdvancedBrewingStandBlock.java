package tfar.davespotioneering.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import tfar.davespotioneering.blockentity.AdvancedBrewingStandBlockEntity;

public class AdvancedBrewingStandBlock extends CAdvancedBrewingStandBlock{
    public AdvancedBrewingStandBlock(Properties properties) {
        super(properties);
    }

    public BlockEntity newBlockEntity(BlockPos p_152698_, BlockState p_152699_) {
        return new AdvancedBrewingStandBlockEntity(p_152698_, p_152699_);
    }

}
