package tfar.davespotioneering.net;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import tfar.davespotioneering.item.GauntletItemFabric;

public class C2SGauntletCyclePacket implements ServerPlayNetworking.PlayChannelHandler {


    public C2SGauntletCyclePacket() {

    }

    public static void encode(boolean up) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBoolean(up);
        ClientPlayNetworking.send(PacketHandler.gauntlet_cycle, buf);
    }

    public void handle(ServerPlayer player, boolean up) {
        player.getServer().execute(() -> {
            if (up) {
                GauntletItemFabric.cycleGauntletForward(player);
            } else {
                GauntletItemFabric.cycleGauntletBackward(player);
            }
        });
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        boolean up = buf.readBoolean();
        handle(player,up);
    }
}
