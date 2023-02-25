package tfar.davespotioneering.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import tfar.davespotioneering.menu.PotionInjectorMenu;

import java.util.function.Supplier;


public class C2SPotionInjectorPacket {

  int button;

  public C2SPotionInjectorPacket(){}

  public C2SPotionInjectorPacket(int button){ this.button = button;}

  //decode
  public C2SPotionInjectorPacket(FriendlyByteBuf buf) {
    this.button = buf.readInt();
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeInt(button);
  }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
      Player player = ctx.get().getSender();
      if (player == null) return;
      ctx.get().enqueueWork(  ()->  {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof PotionInjectorMenu) {
          PotionInjectorMenu potionInjectorMenu = (PotionInjectorMenu) container;
          potionInjectorMenu.handleButton(button);

        }
      });
      ctx.get().setPacketHandled(true);
    }
}

