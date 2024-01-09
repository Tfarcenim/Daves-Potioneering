package tfar.davespotioneering.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.ForgeEvents;
import tfar.davespotioneering.Util;
import tfar.davespotioneering.duck.BrewingStandDuck;

@Mixin(BrewingStandBlockEntity.class)
public class BrewingStandBlockEntityMixin extends BlockEntity implements BrewingStandDuck {

    protected double xp;

    public BrewingStandBlockEntityMixin(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
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
            this.setChanged();
        }
    }

    @Inject(method = "doBrew",at = @At(value = "INVOKE",target = "Lnet/minecraftforge/event/ForgeEventFactory;onPotionBrewed(Lnet/minecraft/core/NonNullList;)V",remap = false))
    private static void betterIntercept(Level itemstack1, BlockPos p_155291_, NonNullList<ItemStack> p_155292_, CallbackInfo ci) {
        DavesPotioneering.potionBrew(itemstack1.getBlockEntity(p_155291_),p_155292_.get(3));
    }
}
