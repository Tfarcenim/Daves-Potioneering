package tfar.davespotioneering.block;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.LecternBlock;

import net.minecraft.block.AbstractBlock.Properties;

public class MagicLecternBlock extends LecternBlock {
    public MagicLecternBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean is(Block block) {
        return block.is(Blocks.LECTERN) || super.is(block);
    }
}
