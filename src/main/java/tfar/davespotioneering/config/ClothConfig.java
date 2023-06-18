package tfar.davespotioneering.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.client.GauntletHUD;

@Config(name = DavesPotioneering.MODID)
public class ClothConfig implements ConfigData {

    @Comment("HUD X")
    public static int gauntlet_hud_x = -120;

    @Comment("HUD Y")
    public static int gauntlet_hud_y = -92;

    public static GauntletHUD.HudPreset gauntlet_hud_preset = GauntletHUD.HudPreset.ABOVE_HOTBAR;

    public static int potion_cooldown = 30;
    public static boolean return_empty_bottles;
    public static int gauntlet_cooldown = 30;
    public static int particle_drip_rate = 10;
}