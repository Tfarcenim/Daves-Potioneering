package tfar.davespotioneering.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.duck.PlayerDuckFabric;

@Mixin(Player.class)
public class PlayerMixinFabric implements PlayerDuckFabric {

    @Unique
    private int[] gauntletCooldowns = new int[0];

    @Override
    public int[] gauntletCooldowns() {
        return gauntletCooldowns;
    }

    @Override
    public void setGauntletCooldowns(int[] cooldowns) {
        gauntletCooldowns = cooldowns;
    }

    @Inject(method = "readAdditionalSaveData",at = @At("RETURN"))
    private void readExtra(CompoundTag compound, CallbackInfo ci) {
        gauntletCooldowns = compound.getIntArray(DavesPotioneering.MODID+":gauntletcooldowns");
    }

    @Inject(method = "addAdditionalSaveData",at = @At("RETURN"))
    private void writeExtra(CompoundTag compound, CallbackInfo ci) {
        compound.putIntArray(DavesPotioneering.MODID+":gauntletcooldowns",gauntletCooldowns);
    }
}
