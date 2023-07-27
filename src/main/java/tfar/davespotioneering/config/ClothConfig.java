package tfar.davespotioneering.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.client.GauntletHUD;

@Config(name = DavesPotioneering.MODID)
public class ClothConfig implements ConfigData {

    //client
    @Comment("HUD X")
    public static int gauntlet_hud_x = -120;

    @Comment("HUD Y")
    public static int gauntlet_hud_y = -92;
    public static int particle_drip_rate = 10;

    public static GauntletHUD.HudPreset gauntlet_hud_preset = GauntletHUD.HudPreset.ABOVE_HOTBAR;


    //server

    public static int potion_stack_size = 16;
    public static int splash_potion_stack_size = 4;
    public static int lingering_potion_stack_size = 4;

    public static int potion_use_cooldown = 30;
    public static int potion_throw_cooldown = 30;

    public static int gauntlet_cooldown = 30;
    public static int coating_uses = 25;

    public static boolean coat_tools = true;
    public static boolean spike_food = true;
    public static boolean show_spiked_food = true;
    public static boolean coat_anything;
    public static boolean milk = true;

}
