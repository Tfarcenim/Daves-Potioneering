package tfar.davespotioneering.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import tfar.davespotioneering.init.ModBlocks;

import java.util.Map;

public class ReinforcedCauldronBlock extends AbstractCauldronBlock {
    public ReinforcedCauldronBlock(Properties p_51403_, Map<Item, CauldronInteraction> interactions) {
        super(p_51403_,interactions);
    }

    @Override
    public boolean isFull(BlockState p_151984_) {
        return false;
    }

    @Override
    public void handlePrecipitation(BlockState p_152935_, Level level, BlockPos pos, Biome.Precipitation precipitation) {
        if (shouldHandlePrecipitation(level, precipitation)) {
            if (precipitation == Biome.Precipitation.RAIN) {
                level.setBlockAndUpdate(pos, ModBlocks.REINFORCED_WATER_CAULDRON.defaultBlockState());
                level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
            } else if (precipitation == Biome.Precipitation.SNOW) {
             //   level.setBlockAndUpdate(pos, Blocks.POWDER_SNOW_CAULDRON.defaultBlockState());
              //  level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
            }

        }
    }

    protected static boolean shouldHandlePrecipitation(Level p_182451_, Biome.Precipitation p_182452_) {
        if (p_182452_ == Biome.Precipitation.RAIN) {
            return p_182451_.getRandom().nextFloat() < 0.05F;
        } else if (p_182452_ == Biome.Precipitation.SNOW) {
            return p_182451_.getRandom().nextFloat() < 0.1F;
        } else {
            return false;
        }
    }

    @Override
    protected void receiveStalactiteDrip(BlockState state, Level level, BlockPos pos, Fluid fluid) {
        if (fluid == Fluids.WATER) {
            level.setBlockAndUpdate(pos, ModBlocks.REINFORCED_WATER_CAULDRON.defaultBlockState());
            level.levelEvent(1047, pos, 0);
            level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
        } else if (fluid == Fluids.LAVA) {
         //   level.setBlockAndUpdate(pos, Blocks.LAVA_CAULDRON.defaultBlockState());
         //   level.levelEvent(1046, pos, 0);
        //    level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
        }
    }
}
