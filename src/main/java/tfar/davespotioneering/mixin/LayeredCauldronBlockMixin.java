package tfar.davespotioneering.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.davespotioneering.block.LayeredReinforcedCauldronBlock;

//not strictly necessary, but cuts down on duplicate code
@Mixin(LayeredCauldronBlock.class)
public class LayeredCauldronBlockMixin {

    @Inject(method = "lowerFillLevel",at = @At("HEAD"),cancellable = true)
    private static void handleThis(BlockState p_153560_, Level p_153561_, BlockPos p_153562_, CallbackInfo ci) {
        if (p_153560_.getBlock() instanceof LayeredCauldronBlock) {
            LayeredReinforcedCauldronBlock.lowerFillLevel0(p_153560_,p_153561_,p_153562_);
            ci.cancel();
        }
    }
}
