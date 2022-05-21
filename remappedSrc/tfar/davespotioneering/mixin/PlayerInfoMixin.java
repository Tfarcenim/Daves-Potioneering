package tfar.davespotioneering.mixin;

import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfar.davespotioneering.client.ClientEvents;

@Mixin(PlayerListEntry.class)
public class PlayerInfoMixin {

    @Shadow private GameMode gameMode;

    @Inject(method = "setGameMode",at = @At("HEAD"))
    private void gamemodeSet(GameMode gameType, CallbackInfo ci) {
        ClientEvents.switchGameMode(this.gameMode,gameType);
    }
}
