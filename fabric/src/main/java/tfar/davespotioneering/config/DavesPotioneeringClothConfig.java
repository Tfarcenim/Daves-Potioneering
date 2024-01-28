package tfar.davespotioneering.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.client.HudPreset;

@Config(name = DavesPotioneering.MODID)
public class DavesPotioneeringClothConfig implements ConfigData {

    //client
    @Comment("HUD X")
    public int gauntlet_hud_x = -120;

    @Comment("HUD Y")
    public int gauntlet_hud_y = -92;
    public int particle_drip_rate = 10;

    public HudPreset gauntlet_hud_preset = HudPreset.ABOVE_HOTBAR;


    //server

    public int potion_stack_size = 16;
    public int splash_potion_stack_size = 4;
    public int lingering_potion_stack_size = 4;

    public int potion_use_cooldown = 30;
    public int potion_throw_cooldown = 30;

    public int gauntlet_cooldown = 600;
    public int coating_uses = 25;
    public boolean spike_food = true;
    public boolean show_spiked_food = true;
    public boolean milk = true;

}
