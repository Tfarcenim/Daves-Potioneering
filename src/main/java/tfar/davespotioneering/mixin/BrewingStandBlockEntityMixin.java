package tfar.davespotioneering.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.davespotioneering.Events;
import tfar.davespotioneering.Util;
import tfar.davespotioneering.duck.BrewingStandDuck;

@Mixin(BrewingStandBlockEntity.class)
public class BrewingStandBlockEntityMixin extends BlockEntity implements BrewingStandDuck {

    @Shadow private NonNullList<ItemStack> brewingItemStacks;
    protected double xp;

    public BrewingStandBlockEntityMixin(BlockEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
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

    @Inject(method = "brewPotions",at = @At(value = "INVOKE",target = "Lnet/minecraftforge/event/ForgeEventFactory;onPotionBrewed(Lnet/minecraft/util/NonNullList;)V",remap = false))
    private void betterIntercept(CallbackInfo ci) {
        Events.potionBrew((BrewingStandBlockEntity)(Object)this,this.brewingItemStacks.get(3));
    }
}
