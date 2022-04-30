package tfar.davespotioneering.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class MagicLecternBlock extends LecternBlock {
    public MagicLecternBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean is(Block block) {
        return block.is(Blocks.LECTERN) || super.is(block);
    }
}
