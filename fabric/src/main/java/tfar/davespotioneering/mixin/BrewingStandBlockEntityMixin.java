package tfar.davespotioneering.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import tfar.davespotioneering.FabricUtil;
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
    public void dump(Player player) {
        if (xp > 0) {
            xp = 0;
            Util.splitAndSpawnExperience(level, player.position(), xp);
            setChanged(level,worldPosition,getBlockState());
        }
    }
}
