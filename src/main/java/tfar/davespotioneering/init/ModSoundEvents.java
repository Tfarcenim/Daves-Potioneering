package tfar.davespotioneering.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import tfar.davespotioneering.DavesPotioneering;

public class ModSoundEvents {

    public static final SoundEvent GAUNTLET_SCROLL = createSound("gauntlet_scroll");
    public static final SoundEvent GAUNTLET_TURNING_ON = createSound("gauntlet_turning_on");
    public static final SoundEvent GAUNTLET_TURNING_OFF = createSound("gauntlet_turning_off");
    public static final SoundEvent GAUNTLET_EQUIP = createSound("gauntlet_equip");

    private static SoundEvent createSound(String name) {
        return new SoundEvent(new ResourceLocation(DavesPotioneering.MODID, name));
    }
}
