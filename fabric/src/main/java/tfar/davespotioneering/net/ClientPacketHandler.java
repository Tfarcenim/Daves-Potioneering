package tfar.davespotioneering.net;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import tfar.davespotioneering.client.GauntletHUDCommon;
import tfar.davespotioneering.client.GauntletHUDFabric;

import java.util.stream.IntStream;

public class ClientPacketHandler {

    public static void registerClientMessages() {
        ClientPlayNetworking.registerGlobalReceiver(PacketHandler.gauntlet_cooldowns,ClientPacketHandler::handleCooldowns);
    }

    private static void handleCooldowns(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        int size = buf.readInt();
        int[] cooldowns = new int[size];
        IntStream.range(0,size).forEach(i -> cooldowns[i] = buf.readInt());
        client.execute(() -> GauntletHUDCommon.cooldowns = cooldowns);
    }
}
