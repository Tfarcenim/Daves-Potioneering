package tfar.davespotioneering.net;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import tfar.davespotioneering.DavesPotioneeringFabric;

public class PacketHandler {
    public static final ResourceLocation potion_injector = new ResourceLocation(DavesPotioneeringFabric.MODID, "potion_injector");
    public static final ResourceLocation gauntlet_cycle = new ResourceLocation(DavesPotioneeringFabric.MODID, "gauntlet_cycle");
    public static final ResourceLocation gauntlet_hud = new ResourceLocation(DavesPotioneeringFabric.MODID, "gauntlet_hud");

    public static void registerMessages() {
        int id = 0;

        ServerPlayNetworking.registerGlobalReceiver(potion_injector, new C2SPotionInjector());
        ServerPlayNetworking.registerGlobalReceiver(gauntlet_cycle, new C2SGauntletCyclePacket());
        ServerPlayNetworking.registerGlobalReceiver(gauntlet_hud, new C2SGauntletHUDMovementGuiPacket());

    }
}
