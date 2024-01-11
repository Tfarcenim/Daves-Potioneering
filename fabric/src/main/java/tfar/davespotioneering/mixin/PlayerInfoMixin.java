package tfar.davespotioneering.mixin;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.davespotioneering.client.DavesPotioneeeringClientFabric;

@Mixin(PlayerInfo.class)
public class PlayerInfoMixin {

    @Shadow private GameType gameMode;

    @Inject(method = "setGameMode",at = @At("HEAD"))
    private void gamemodeSet(GameType gameType, CallbackInfo ci) {
        DavesPotioneeeringClientFabric.switchGameMode(this.gameMode,gameType);
    }
}
