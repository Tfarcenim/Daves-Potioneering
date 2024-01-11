package tfar.davespotioneering.net;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.DavesPotioneeringFabric;

import java.util.stream.IntStream;

public class PacketHandler {
    public static final ResourceLocation potion_injector = new ResourceLocation(DavesPotioneering.MODID, "potion_injector");
    public static final ResourceLocation gauntlet_cycle = new ResourceLocation(DavesPotioneering.MODID, "gauntlet_cycle");
    public static final ResourceLocation gauntlet_hud = new ResourceLocation(DavesPotioneering.MODID, "gauntlet_hud");
    public static final ResourceLocation gauntlet_cooldowns = new ResourceLocation(DavesPotioneering.MODID, "gauntlet_cooldowns");

    public static void registerMessages() {
        ServerPlayNetworking.registerGlobalReceiver(potion_injector, new C2SPotionInjector());
        ServerPlayNetworking.registerGlobalReceiver(gauntlet_cycle, new C2SGauntletCyclePacket());
        ServerPlayNetworking.registerGlobalReceiver(gauntlet_hud, new C2SGauntletHUDMovementGuiPacket());
    }

    public static void sendCooldowns(ServerPlayer player, int[] cooldowns) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(cooldowns.length);
        IntStream.range(0,cooldowns.length).forEach(i -> buf.writeInt(cooldowns[i]));
        ServerPlayNetworking.send(player,gauntlet_cooldowns,buf);
    }
}
