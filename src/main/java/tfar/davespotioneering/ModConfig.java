package tfar.davespotioneering;

import net.minecraftforge.common.ForgeConfigSpec;
import tfar.davespotioneering.client.GauntletHUD;

public class ModConfig {

    public static class Client {

        public static ForgeConfigSpec.IntValue gauntlet_hud_x;
        public static ForgeConfigSpec.IntValue gauntlet_hud_y;
        public static ForgeConfigSpec.EnumValue<GauntletHUD.HudPresets> gauntlet_hud_preset;
        public static ForgeConfigSpec.IntValue particle_drip_rate;

        public Client(ForgeConfigSpec.Builder builder) {
            builder.push("general");
            gauntlet_hud_x = builder.comment("The X Position of the gauntlet hud (left top). You should be using the in-game gui to change this though").defineInRange("gauntlet_hud_x", -120, Integer.MIN_VALUE, Integer.MAX_VALUE);
            gauntlet_hud_y = builder.comment("The y Position of the gauntlet hud (left top). You should be using the in-game gui to change this though").defineInRange("gauntlet_hud_y", -92, Integer.MIN_VALUE, Integer.MAX_VALUE);
            gauntlet_hud_preset = builder.comment("You shouldn't change this. Just don't").defineEnum("gauntlet_hud_preset", GauntletHUD.HudPresets.FREE_MOVE);
            particle_drip_rate = builder.defineInRange("particle_drip_rate",10,1,Integer.MAX_VALUE);
            builder.pop();
        }
    }

    public static class Server {
        public static ForgeConfigSpec.BooleanValue coat_all;
        public static ForgeConfigSpec.IntValue potion_switch_cooldown;
        public static ForgeConfigSpec.IntValue potion_throw_cooldown;
        public static ForgeConfigSpec.BooleanValue milkification;
        public static ForgeConfigSpec.BooleanValue milk;

        public static ForgeConfigSpec.IntValue gauntlet_cooldown;
        public static final String pot_throw_key = "config.davespotioneering.potion_throw_cooldown";
        public static final String pot_switch_key = "config.davespotioneering.potion_switch_cooldown";


        public Server(ForgeConfigSpec.Builder builder) {
            builder.push("general");
            milkification = builder.define("milkification",false);
            milk = builder.define("milk",true);
            gauntlet_cooldown = builder.defineInRange("gauntlet_cooldown", 600, 1, Integer.MAX_VALUE);
            potion_switch_cooldown = builder.comment("Cooldown in ticks when switching to potions").translation(pot_switch_key)
                    .defineInRange("potion_switch_cooldown", 30, 0, 20000);
            potion_throw_cooldown = builder.comment("Cooldown in ticks when throwing potions").translation(pot_throw_key)
                    .defineInRange("potion_throw_cooldown", 30, 0, 20000);
            coat_all = builder.comment("Allows all items to be coated").define("coat_all",false);

            builder.pop();
        }
    }
}
