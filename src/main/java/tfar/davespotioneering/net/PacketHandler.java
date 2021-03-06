package tfar.davespotioneering.net;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import tfar.davespotioneering.DavesPotioneering;

public class PacketHandler {
    public static SimpleChannel INSTANCE;

    public static void registerMessages() {
        int id = 0;

        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(DavesPotioneering.MODID, DavesPotioneering.MODID), () -> "1.0", s -> true, s -> true);

        INSTANCE.registerMessage(id++, C2SPotionInjector.class,
                C2SPotionInjector::encode,
                C2SPotionInjector::new,
                C2SPotionInjector::handle);

        INSTANCE.registerMessage(id++, GauntletCyclePacket.class,
                GauntletCyclePacket::encode,
                GauntletCyclePacket::new,
                GauntletCyclePacket::handle);

        INSTANCE.registerMessage(id++, GauntletHUDMovementGuiPacket.class,
                (packetOpenGui, packetBuffer) -> {},
                buf -> new GauntletHUDMovementGuiPacket(),
                GauntletHUDMovementGuiPacket::handle);
    }

    public static void sendToClient(Object packet, ServerPlayerEntity player) {
        INSTANCE.sendTo(packet, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }
}
