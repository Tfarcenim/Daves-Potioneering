package tfar.davespotioneering.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.CauldronBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import tfar.davespotioneering.blockentity.ReinforcedCauldronBlockEntity;

import javax.annotation.Nullable;

public class ReinforcedCauldronBlock extends CauldronBlock {
    public ReinforcedCauldronBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ReinforcedCauldronBlockEntity();
    }
}
