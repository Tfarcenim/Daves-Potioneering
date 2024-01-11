package tfar.davespotioneering.mixin;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import tfar.davespotioneering.duck.PlayerDuckFabric;

@Mixin(Player.class)
public class PlayerMixinFabric implements PlayerDuckFabric {
}
