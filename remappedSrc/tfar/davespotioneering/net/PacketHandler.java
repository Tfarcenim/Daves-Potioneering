package tfar.davespotioneering.net;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import tfar.davespotioneering.DavesPotioneering;

public class PacketHandler {
    public static final Identifier potion_injector = new Identifier(DavesPotioneering.MODID, "potion_injector");
    public static final Identifier gauntlet_cycle = new Identifier(DavesPotioneering.MODID, "gauntlet_cycle");
    public static final Identifier gauntlet_hud = new Identifier(DavesPotioneering.MODID, "gauntlet_hud");

    public static void registerMessages() {
        int id = 0;

        ServerPlayNetworking.registerGlobalReceiver(potion_injector, new C2SPotionInjector());
        ServerPlayNetworking.registerGlobalReceiver(gauntlet_cycle, new C2SGauntletCyclePacket());
        ServerPlayNetworking.registerGlobalReceiver(gauntlet_hud, new C2SGauntletHUDMovementGuiPacket());

    }
}
