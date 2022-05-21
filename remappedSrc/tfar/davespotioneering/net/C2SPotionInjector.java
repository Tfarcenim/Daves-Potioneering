package tfar.davespotioneering.net;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import tfar.davespotioneering.menu.PotionInjectorMenu;


public class C2SPotionInjector implements ServerPlayNetworking.PlayChannelHandler {

  public static void encode(int button) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeInt(button);
    ClientPlayNetworking.send(PacketHandler.potion_injector, buf);
  }

    public static void handle(ServerPlayerEntity player, int button) {
      if (player == null) return;
      player.getServer().execute(  ()->  {
        ScreenHandler container = player.currentScreenHandler;
        if (container instanceof PotionInjectorMenu) {
          PotionInjectorMenu potionInjectorMenu = (PotionInjectorMenu) container;
          potionInjectorMenu.handleButton(button);

        }
      });
    }

  @Override
  public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
    int button = buf.readInt();
    handle(player,button);
  }
}

