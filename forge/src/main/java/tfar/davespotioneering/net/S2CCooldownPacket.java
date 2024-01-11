package tfar.davespotioneering.net;

import net.minecraft.network.FriendlyByteBuf;
import tfar.davespotioneering.client.GauntletHUDCommon;
import tfar.davespotioneering.net.util.S2CPacketHelper;

import java.util.stream.IntStream;

public class S2CCooldownPacket implements S2CPacketHelper {
    private final int[] cooldowns;

    public S2CCooldownPacket(int[] cooldowns) {
        this.cooldowns = cooldowns;
    }

    public S2CCooldownPacket(FriendlyByteBuf buf) {
        int size = buf.readInt();
        cooldowns = new int[size];
        IntStream.range(0,size).forEach(i -> cooldowns[i] = buf.readInt());
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(cooldowns.length);
        IntStream.range(0,cooldowns.length).forEach(i -> buf.writeInt(cooldowns[i]));
    }

    @Override
    public void handleClient() {
        GauntletHUDCommon.cooldowns = cooldowns;
    }
}
