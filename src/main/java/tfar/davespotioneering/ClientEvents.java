package tfar.davespotioneering;

import net.minecraft.util.SoundEvents;
import net.minecraftforge.client.event.sound.PlaySoundEvent;

public class ClientEvents {

    public static void playSound(PlaySoundEvent event) {
        if (event.getName().equals(SoundEvents.BLOCK_BREWING_STAND_BREW.getName().getPath()) && !ModConfig.Client.play_block_brewing_stand_brew.get()) {
            event.setResultSound(null);
        }
    }
}
