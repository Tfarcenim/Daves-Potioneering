package tfar.davespotioneering.net;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import tfar.davespotioneering.menu.GauntletMenu;

import java.util.function.Supplier;


public class C2SPotionInjector {

  int button;

  public C2SPotionInjector(){}

  public C2SPotionInjector(int button){ this.button = button;}

  //decode
  public C2SPotionInjector(PacketBuffer buf) {
    this.button = buf.readInt();
  }

  public void encode(PacketBuffer buf) {
    buf.writeInt(button);
  }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
      PlayerEntity player = ctx.get().getSender();
      if (player == null) return;
      ctx.get().enqueueWork(  ()->  {
        Container container = player.openContainer;
        if (container instanceof GauntletMenu) {
          GauntletMenu gauntletMenu = (GauntletMenu) container;
          gauntletMenu.handleButton(button);

        }
      });
      ctx.get().setPacketHandled(true);
    }
}

