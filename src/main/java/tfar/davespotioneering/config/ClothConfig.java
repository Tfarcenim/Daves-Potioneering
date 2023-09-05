package tfar.davespotioneering.config;

import blue.endless.jankson.Comment;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.client.GauntletHUD;

@Config(name = DavesPotioneering.MODID)
public class ClothConfig implements ConfigData {

    //client
    @Comment("HUD X")
    public int gauntlet_hud_x = -120;

    @Comment("HUD Y")
    public int gauntlet_hud_y = -92;
    public int particle_drip_rate = 10;

    public GauntletHUD.HudPreset gauntlet_hud_preset = GauntletHUD.HudPreset.ABOVE_HOTBAR;


    //server

    public int potion_stack_size = 16;
    public int splash_potion_stack_size = 4;
    public int lingering_potion_stack_size = 4;

    public int potion_use_cooldown = 30;
    public int potion_throw_cooldown = 30;

    public int gauntlet_cooldown = 30;
    public int coating_uses = 25;

    public boolean coat_tools = true;
    public boolean spike_food = true;
    public boolean show_spiked_food = true;
    public boolean coat_anything;
    public boolean milk = true;

}
