package tfar.davespotioneering.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class ReinforcedCauldronBlock extends CauldronBlock {
    public ReinforcedCauldronBlock(Properties p_51403_) {
        super(p_51403_);
    }

    @Override
    public void handlePrecipitation(BlockState p_152935_, Level p_152936_, BlockPos p_152937_, Biome.Precipitation p_152938_) {
        if (shouldHandlePrecipitation(p_152936_, p_152938_)) {
            if (p_152938_ == Biome.Precipitation.RAIN) {
                p_152936_.setBlockAndUpdate(p_152937_, Blocks.WATER_CAULDRON.defaultBlockState());
                p_152936_.gameEvent((Entity)null, GameEvent.FLUID_PLACE, p_152937_);
            } else if (p_152938_ == Biome.Precipitation.SNOW) {
                p_152936_.setBlockAndUpdate(p_152937_, Blocks.POWDER_SNOW_CAULDRON.defaultBlockState());
                p_152936_.gameEvent((Entity)null, GameEvent.FLUID_PLACE, p_152937_);
            }

        }    }

    @Override
    protected void receiveStalactiteDrip(BlockState state, Level p_152941_, BlockPos p_152942_, Fluid p_152943_) {
        if (p_152943_ == Fluids.WATER) {
            p_152941_.setBlockAndUpdate(p_152942_, Blocks.WATER_CAULDRON.defaultBlockState());
            p_152941_.levelEvent(1047, p_152942_, 0);
            p_152941_.gameEvent((Entity)null, GameEvent.FLUID_PLACE, p_152942_);
        } else if (p_152943_ == Fluids.LAVA) {
            p_152941_.setBlockAndUpdate(p_152942_, Blocks.LAVA_CAULDRON.defaultBlockState());
            p_152941_.levelEvent(1046, p_152942_, 0);
            p_152941_.gameEvent((Entity)null, GameEvent.FLUID_PLACE, p_152942_);
        }    }
}
