package tfar.davespotioneering.block;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import tfar.davespotioneering.init.ModBlocks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class ReinforcedCauldronBlock extends AbstractCauldronBlock {
    public ReinforcedCauldronBlock(Properties p_51403_, Map<Item, CauldronInteraction> interactions) {
        super(p_51403_,interactions);
    }

    @Override
    public boolean isFull(BlockState p_151984_) {
        return false;
    }

    public static final int S_LINES = 2;
    public static final int C_LINES = 3;
    public static final int A_LINES = 3;

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {

        tooltip.add(Component.translatable(getDescriptionId()+".hold_shift.desc"));
        if (Screen.hasShiftDown()) {
                   tooltip.add(this.getShiftDescription().withStyle(ChatFormatting.GRAY));
            //  for (int i = 0; i < S_LINES;i++) {

         //       tooltip.add(this.getShiftDescriptions(i).withStyle(ChatFormatting.GRAY));
         //   }
        }

        tooltip.add(Component.translatable(getDescriptionId()+".hold_ctrl.desc"));
        if (Screen.hasControlDown())
            for (int i = 0; i < C_LINES;i++) {
                tooltip.add(this.getCtrlDescriptions(i).withStyle(ChatFormatting.GRAY));
            }
        tooltip.add(Component.translatable(getDescriptionId()+".hold_alt.desc"));
        if (Screen.hasAltDown()) {
            for (int i = 0; i < A_LINES;i++) {
                tooltip.add(this.getAltDescriptions(i).withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.literal(" "));
            }
        }
    }


    public MutableComponent getShiftDescription() {
        return Component.translatable(this.getDescriptionId() + ".shift.desc");
    }

    public MutableComponent getShiftDescriptions(int i) {
        return Component.translatable(this.getDescriptionId() + i +".shift.desc");
    }

    public MutableComponent getCtrlDescription() {
        return Component.translatable(this.getDescriptionId() + ".ctrl.desc");
    }

    public MutableComponent getCtrlDescriptions(int i) {
        return Component.translatable(this.getDescriptionId() + i +".ctrl.desc");
    }

    public MutableComponent getAltDescription() {
        return Component.translatable(this.getDescriptionId() + ".alt.desc");
    }

    public MutableComponent getAltDescriptions(int i) {
        return Component.translatable(this.getDescriptionId() + i+".alt.desc");
    }

    @Override
    public void handlePrecipitation(BlockState p_152935_, Level level, BlockPos pos, Biome.Precipitation precipitation) {
        if (Dummy.shouldHandlePrecipitation(level, precipitation)) {
            if (precipitation == Biome.Precipitation.RAIN) {
                level.setBlockAndUpdate(pos, ModBlocks.REINFORCED_WATER_CAULDRON.defaultBlockState());
                level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
            } else if (precipitation == Biome.Precipitation.SNOW) {
                //   level.setBlockAndUpdate(pos, Blocks.POWDER_SNOW_CAULDRON.defaultBlockState());
                //  level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
            }

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

    //Avoids AT
    public static class Dummy extends CauldronBlock {
        public Dummy(Properties p_51403_) {
            super(p_51403_);
        }

        public static boolean shouldHandlePrecipitation(Level level, Biome.Precipitation precipitation) {
            return CauldronBlock.shouldHandlePrecipitation(level, precipitation);
        }
    }
}
