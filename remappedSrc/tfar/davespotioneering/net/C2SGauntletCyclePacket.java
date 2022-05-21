package tfar.davespotioneering.net;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import tfar.davespotioneering.item.GauntletItem;

public class C2SGauntletCyclePacket implements ServerPlayNetworking.PlayChannelHandler {


    public C2SGauntletCyclePacket() {

    }

    public static void encode(boolean up) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBoolean(up);
        ClientPlayNetworking.send(PacketHandler.gauntlet_cycle, buf);
    }

    public void handle(ServerPlayerEntity player, boolean up) {
        player.getServer().execute(() -> {
            if (up) {
                GauntletItem.cycleGauntletForward(player);
            } else {
                GauntletItem.cycleGauntletBackward(player);
            }
        });
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        boolean up = buf.readBoolean();
        handle(player,up);
    }
}
