package tfar.davespotioneering.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import tfar.davespotioneering.DavesPotioneering;

public class ModSoundEvents {

    public static final SoundEvent GAUNTLET_SCROLL = createSound("gauntlet_scroll");
    public static final SoundEvent GAUNTLET_TURNING_ON = createSound("gauntlet_turning_on");
    public static final SoundEvent GAUNTLET_TURNING_OFF = createSound("gauntlet_turning_off");
    public static final SoundEvent BUBBLING_WATER_CAULDRON = createSound("bubbling_water_cauldron");
    public static final SoundEvent UMBRELLA_OPEN = createSound("umbrella_open");
    public static final SoundEvent UMBRELLA_CLOSE = createSound("umbrella_close");

    public static final SoundEvent GAUNTLET_EQUIP = createSound("gauntlet_equip");

    private static SoundEvent createSound(String name) {
        return SoundEvent.createVariableRangeEvent(new ResourceLocation(DavesPotioneering.MODID, name));
    }
}
