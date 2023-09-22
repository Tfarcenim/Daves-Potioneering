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

@Mixin(LayeredCauldronBlock.class)
public class LayeredCauldronBlockMixin {

    @Inject(method = "lowerFillLevel",at = @At("HEAD"),cancellable = true)
    private static void handleThis(BlockState blockState, Level world, BlockPos blockPos, CallbackInfo ci) {
        if (blockState.getBlock() instanceof LayeredReinforcedCauldronBlock) {
            LayeredReinforcedCauldronBlock.lowerFillLevel0(blockState, world, blockPos);
            ci.cancel();
        }
    }
}
