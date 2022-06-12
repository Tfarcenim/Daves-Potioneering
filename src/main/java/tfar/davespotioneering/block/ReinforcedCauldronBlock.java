package tfar.davespotioneering.block;

import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.event.GameEvent;
import tfar.davespotioneering.init.ModBlocks;

import java.util.Map;

public class ReinforcedCauldronBlock extends AbstractCauldronBlock {
    public ReinforcedCauldronBlock(Settings settings, Map<Item, CauldronBehavior> interactions) {
        super(settings,interactions);
    }

    @Override
    public boolean isFull(BlockState p_151984_) {
        return false;
    }

    @Override
    public void precipitationTick(BlockState p_152935_, World level, BlockPos pos, Biome.Precipitation precipitation) {
        if (shouldHandlePrecipitation(level, precipitation)) {
            if (precipitation == Biome.Precipitation.RAIN) {
                level.setBlockState(pos, ModBlocks.REINFORCED_WATER_CAULDRON.getDefaultState());
                level.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
            } else if (precipitation == Biome.Precipitation.SNOW) {
                //   level.setBlockAndUpdate(pos, Blocks.POWDER_SNOW_CAULDRON.defaultBlockState());
                //  level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
            }

        }
    }

    protected static boolean shouldHandlePrecipitation(World p_182451_, Biome.Precipitation p_182452_) {
        if (p_182452_ == Biome.Precipitation.RAIN) {
            return p_182451_.getRandom().nextFloat() < 0.05F;
        } else if (p_182452_ == Biome.Precipitation.SNOW) {
            return p_182451_.getRandom().nextFloat() < 0.1F;
        } else {
            return false;
        }
    }

    @Override
    protected void fillFromDripstone(BlockState state, World level, BlockPos pos, Fluid fluid) {
        if (fluid == Fluids.WATER) {
            level.setBlockState(pos, ModBlocks.REINFORCED_WATER_CAULDRON.getDefaultState());
            level.syncWorldEvent(1047, pos, 0);
            level.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
        } else if (fluid == Fluids.LAVA) {
            //   level.setBlockAndUpdate(pos, Blocks.LAVA_CAULDRON.defaultBlockState());
            //   level.levelEvent(1046, pos, 0);
            //    level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
        }
    }
}
