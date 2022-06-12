package tfar.davespotioneering.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import tfar.davespotioneering.Util;
import tfar.davespotioneering.duck.BrewingStandDuck;

@Mixin(BrewingStandBlockEntity.class)
public class BrewingStandBlockEntityMixin extends BlockEntity implements BrewingStandDuck {

    protected double xp;

    public BrewingStandBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }


    @Override
    public void addXp(double xp) {
        this.xp += xp;
    }

    @Override
    public void dump(PlayerEntity player) {
        if (xp > 0) {
            xp = 0;
            Util.splitAndSpawnExperience(world, player.getPos(), xp);
            this.markDirty();
        }
    }
}
