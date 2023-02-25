package tfar.davespotioneering.net;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import tfar.davespotioneering.DavesPotioneering;

public class PacketHandler {
    public static SimpleChannel INSTANCE;

    public static void registerMessages() {
        int id = 0;

        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(DavesPotioneering.MODID, DavesPotioneering.MODID), () -> "1.0", s -> true, s -> true);

        INSTANCE.registerMessage(id++, C2SPotionInjectorPacket.class,
                C2SPotionInjectorPacket::encode,
                C2SPotionInjectorPacket::new,
                C2SPotionInjectorPacket::handle);

        INSTANCE.registerMessage(id++, C2SGauntletCyclePacket.class,
                C2SGauntletCyclePacket::encode,
                C2SGauntletCyclePacket::new,
                C2SGauntletCyclePacket::handle);

        INSTANCE.registerMessage(id++, S2CCooldownPacket.class,
                S2CCooldownPacket::encode,
                S2CCooldownPacket::new,
                S2CCooldownPacket::handle);
    }

    public static void sendToClient(Object packet, ServerPlayer player) {
        INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }
}
