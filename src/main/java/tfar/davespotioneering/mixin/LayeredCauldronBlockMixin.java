package tfar.davespotioneering.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.davespotioneering.block.LayeredReinforcedCauldronBlock;

@Mixin(LeveledCauldronBlock.class)
public class LayeredCauldronBlockMixin {

    @Inject(method = "decrementFluidLevel",at = @At("HEAD"),cancellable = true)
    private static void handleThis(BlockState blockState, World world, BlockPos blockPos, CallbackInfo ci) {
        if (blockState.getBlock() instanceof LayeredReinforcedCauldronBlock) {
            LayeredReinforcedCauldronBlock.lowerFillLevel0(blockState, world, blockPos);
            ci.cancel();
        }
    }
}
