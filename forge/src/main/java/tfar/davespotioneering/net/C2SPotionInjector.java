package tfar.davespotioneering.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import tfar.davespotioneering.menu.CPotionInjectorMenu;

import java.util.function.Supplier;


public class C2SPotionInjector {

  int button;

  public C2SPotionInjector(){}

  public C2SPotionInjector(int button){ this.button = button;}

  //decode
  public C2SPotionInjector(FriendlyByteBuf buf) {
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
        if (container instanceof CPotionInjectorMenu) {
          CPotionInjectorMenu potionInjectorMenu = (CPotionInjectorMenu) container;
          potionInjectorMenu.handleButton(button);

        }
      });
      ctx.get().setPacketHandled(true);
    }
}

