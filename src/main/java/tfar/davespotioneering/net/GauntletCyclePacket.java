package tfar.davespotioneering.net;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import tfar.davespotioneering.item.GauntletItem;

import java.util.function.Supplier;

public class GauntletCyclePacket {
    private final boolean up;

    public GauntletCyclePacket(boolean up) {
        this.up = up;
    }

    public GauntletCyclePacket(PacketBuffer buffer) {
        this.up = buffer.readBoolean();
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeBoolean(up);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (up) {
                GauntletItem.cycleGauntletForward(player);
            } else {
                GauntletItem.cycleGauntletBackward(player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
