package tfar.davespotioneering.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import tfar.davespotioneering.client.GauntletHUD;

import java.util.function.Supplier;

public class S2CCooldownPacket {
    int[] cooldowns;

    public S2CCooldownPacket(){}

    public S2CCooldownPacket(int[] cooldowns){ this.cooldowns = cooldowns;}

    //decode
    public S2CCooldownPacket(FriendlyByteBuf buf) {
        int size = buf.readInt();
        cooldowns = new int[size];
        for (int i = 0; i < size;i++) {
            this.cooldowns[i] = buf.readInt();
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(cooldowns.length);
        for (int cooldown : cooldowns) {
            buf.writeInt(cooldown);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(()-> GauntletHUD.cooldowns = cooldowns);
    }
}
