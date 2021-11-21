package tfar.davespotioneering.net;

import net.minecraft.util.ResourceLocation;
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
  }
}
