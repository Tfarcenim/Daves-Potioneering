package tfar.davespotioneering.block;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.LecternBlock;

public class MagicLecternBlock extends LecternBlock {
    public MagicLecternBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean matchesBlock(Block block) {
        return block.matchesBlock(Blocks.LECTERN) || super.matchesBlock(block);
    }
}
