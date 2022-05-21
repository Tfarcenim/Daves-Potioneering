package tfar.davespotioneering;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableText;
import tfar.davespotioneering.client.GauntletHUD;

public class ModConfig {

    public static class Client {

        public static ConfigCategory client;

        public static int gauntlet_hud_x = -120;

        public static int gauntlet_hud_y;
        public static GauntletHUD.HudPresets gauntlet_hud_preset;
        public static int particle_drip_rate;

        public static void client() {

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(MinecraftClient.getInstance().currentScreen)
                    .setTitle(new TranslatableText("title.examplemod.config"));

            ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("category.examplemod.general"));

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            Client.client = general.addEntry(entryBuilder.startIntField(new TranslatableText("config.gauntlet_hud_x"), gauntlet_hud_x)
                            .setDefaultValue(-120) // Recommended: Used when user click "Reset"
                           // .setTooltip(new TranslatableComponent("This option is awesome!")) // Optional: Shown when the user hover over this option
                            .setSaveConsumer(newValue -> gauntlet_hud_x = newValue) // Recommended: Called when user save the config
                            .build()) // Builds the option entry for cloth config
                    .addEntry(entryBuilder.startIntField(new TranslatableText("config.gauntlet_hud_y"), gauntlet_hud_y)
                            .setDefaultValue(-92)
                            .setTooltip(new TranslatableText("This option is awesome!"))
                            .setSaveConsumer(newValue -> gauntlet_hud_y = newValue)
                            .build())
                    .addEntry(entryBuilder.startEnumSelector(new TranslatableText("config.gauntlet_hud_preset"),
                                    GauntletHUD.HudPresets.class, gauntlet_hud_preset)
                            .setDefaultValue(GauntletHUD.HudPresets.FREE_MOVE)
                       //     .setTooltip(new TranslatableComponent("This option is awesome!"))
                            .setSaveConsumer(newValue -> gauntlet_hud_preset = newValue)
                            .build())
                    .addEntry(entryBuilder.startIntField(new TranslatableText("config.particle_drip_rate"), particle_drip_rate)
                            .setDefaultValue(10)
                       //     .setTooltip(new TranslatableComponent("This option is awesome!"))
                            .setSaveConsumer(newValue -> particle_drip_rate = newValue)
                            .build());
        }
    }

    public static class Server {

        public static ConfigCategory server;

        public static int potion_cooldown = 30;
        public static boolean return_empty_bottles;
        public static boolean milkification;
        public static boolean magic_protection;
        public static int gauntlet_cooldown;

        public static void server() {

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(MinecraftClient.getInstance().currentScreen)
                    .setTitle(new TranslatableText("title.examplemod.config"));

            ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("category.examplemod.general"));


          //  return_empty_bottles = builder.define("return_empty_bottles", true);
     //       milkification = builder.define("milkification", false);
         ///   magic_protection = builder.define("magic_protection", false);
      //      gauntlet_cooldown = builder.defineInRange("gauntlet_cooldown", 600, 1, Integer.MAX_VALUE);
        }
    }
}
