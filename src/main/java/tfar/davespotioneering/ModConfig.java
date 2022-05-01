package tfar.davespotioneering;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import tfar.davespotioneering.client.GauntletHUD;

public class ModConfig {

    public static class Client {

        public static ConfigCategory client;

        public static int currentValue = -120;

        public static ForgeConfigSpec.IntValue gauntlet_hud_y;
        public static ForgeConfigSpec.EnumValue<GauntletHUD.HudPresets> gauntlet_hud_preset;
        public static ForgeConfigSpec.IntValue particle_drip_rate;

        public static void client() {

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(Minecraft.getInstance().screen)
                    .setTitle(new TranslatableComponent("title.examplemod.config"));

            ConfigCategory general = builder.getOrCreateCategory(new TranslatableComponent("category.examplemod.general"));

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

             Client.client = general.addEntry(entryBuilder.startIntField(new TranslatableComponent("option.examplemod.optionA"), currentValue)
                    .setDefaultValue(-120) // Recommended: Used when user click "Reset"
                    .setTooltip(new TranslatableComponent("This option is awesome!")) // Optional: Shown when the user hover over this option
                    .setSaveConsumer(newValue -> currentValue = newValue) // Recommended: Called when user save the config
                    .build()); // Builds the option entry for cloth config

            Client.client = builder.comment().defineInRange("gauntlet_hud_x", -120, Integer.MIN_VALUE, Integer.MAX_VALUE);
            gauntlet_hud_y = builder.comment("The y Position of the gauntlet hud (left top). You should be using the in-game gui to change this though").defineInRange("gauntlet_hud_y", -92, Integer.MIN_VALUE, Integer.MAX_VALUE);
            gauntlet_hud_preset = builder.comment("You shouldn't change this. Just don't").defineEnum("gauntlet_hud_preset", GauntletHUD.HudPresets.FREE_MOVE);
            particle_drip_rate = builder.defineInRange("particle_drip_rate",10,1,Integer.MAX_VALUE);
            builder.pop();
        }
    }

    public static class Server {

        public static int potion_cooldown = 30;
        public static ForgeConfigSpec.BooleanValue return_empty_bottles;
        public static ForgeConfigSpec.BooleanValue milkification;
        public static ForgeConfigSpec.BooleanValue magic_protection;
        public static ForgeConfigSpec.IntValue gauntlet_cooldown;

        public static void server() {

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(Minecraft.getInstance().screen)
                    .setTitle(new TranslatableComponent("title.examplemod.config"));

            ConfigCategory general = builder.getOrCreateCategory(new TranslatableComponent("category.examplemod.general"));

            builder.push("general");
            return_empty_bottles = builder.define("return_empty_bottles",true);
            milkification = builder.define("milkification",false);
            magic_protection = builder.define("magic_protection",false);
            gauntlet_cooldown = builder.defineInRange("gauntlet_cooldown", 600, 1, Integer.MAX_VALUE);
            builder.pop();
        }
    }
}
