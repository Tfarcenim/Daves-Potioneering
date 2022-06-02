package tfar.davespotioneering.net;

import net.minecraftforge.network.NetworkEvent;
import tfar.davespotioneering.client.GauntletHUDMovementGui;

import java.util.function.Supplier;

public class GauntletHUDMovementGuiPacket {
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(GauntletHUDMovementGui::open);
        ctx.get().setPacketHandled(true);
    }
}
