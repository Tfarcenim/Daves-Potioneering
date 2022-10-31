package tfar.davespotioneering.net;

import net.minecraftforge.network.NetworkEvent;
import tfar.davespotioneering.client.GauntletHUDMovementScreen;

import java.util.function.Supplier;

public class GauntletHUDMovementGuiPacket {
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(GauntletHUDMovementScreen::open);
        ctx.get().setPacketHandled(true);
    }
}
