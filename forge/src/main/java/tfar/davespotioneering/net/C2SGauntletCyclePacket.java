package tfar.davespotioneering.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import tfar.davespotioneering.item.GauntletItem;

import java.util.function.Supplier;

public class C2SGauntletCyclePacket {
    private final boolean up;

    public C2SGauntletCyclePacket(boolean up) {
        this.up = up;
    }

    public C2SGauntletCyclePacket(FriendlyByteBuf buffer) {
        this.up = buffer.readBoolean();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(up);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (up) {
                GauntletItem.cycleGauntletForward(player);
            } else {
                GauntletItem.cycleGauntletBackward(player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}