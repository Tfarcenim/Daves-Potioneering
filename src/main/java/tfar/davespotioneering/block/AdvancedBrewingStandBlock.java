package tfar.davespotioneering.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import tfar.davespotioneering.blockentity.AdvancedBrewingStandBlockEntity;

public class AdvancedBrewingStandBlock extends BrewingStandBlock {
    public AdvancedBrewingStandBlock(Properties properties) {
        super(properties);
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isRemote) {
            return ActionResultType.SUCCESS;
        } else {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof AdvancedBrewingStandBlockEntity) {
                player.openContainer((AdvancedBrewingStandBlockEntity)tileentity);
                player.addStat(Stats.INTERACT_WITH_BREWINGSTAND);
            }
            return ActionResultType.CONSUME;
        }
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new AdvancedBrewingStandBlockEntity();
    }
}
