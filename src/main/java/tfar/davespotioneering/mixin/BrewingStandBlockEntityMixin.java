package tfar.davespotioneering.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.BrewingStandTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.davespotioneering.Events;
import tfar.davespotioneering.Util;
import tfar.davespotioneering.duck.BrewingStandDuck;

@Mixin(BrewingStandTileEntity.class)
public class BrewingStandBlockEntityMixin extends TileEntity implements BrewingStandDuck {

    @Shadow private NonNullList<ItemStack> brewingItemStacks;
    protected double xp;

    public BrewingStandBlockEntityMixin(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void addXp(double xp) {
        this.xp += xp;
    }

    @Override
    public void dump(PlayerEntity player) {
        Util.splitAndSpawnExperience(world,player.getPositionVec(),xp);
        xp = 0;
        this.markDirty();
    }

    @Inject(method = "brewPotions",at = @At(value = "INVOKE",target = "Lnet/minecraftforge/event/ForgeEventFactory;onPotionBrewed(Lnet/minecraft/util/NonNullList;)V"))
    private void betterIntercept(CallbackInfo ci) {
        Events.potionBrew((BrewingStandTileEntity)(Object)this,this.brewingItemStacks.get(3));
    }
}
