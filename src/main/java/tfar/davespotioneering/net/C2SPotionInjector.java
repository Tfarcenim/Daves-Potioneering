package tfar.davespotioneering.net;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.FriendlyByteBuf;
import tfar.davespotioneering.menu.PotionInjectorMenu;


public class C2SPotionInjector implements ServerPlayNetworking.PlayChannelHandler {

  public static void encode(int button) {
    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
    buf.writeInt(button);
    ClientPlayNetworking.send(PacketHandler.potion_injector, buf);
  }

    public static void handle(ServerPlayer player, int button) {
      if (player == null) return;
      player.getServer().execute(  ()->  {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof PotionInjectorMenu) {
          PotionInjectorMenu potionInjectorMenu = (PotionInjectorMenu) container;
          potionInjectorMenu.handleButton(button);

        }
      });
    }

  @Override
  public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
    int button = buf.readInt();
    handle(player,button);
  }
}

