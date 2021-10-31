package tfar.davespotioneering;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfig {

    public static class Client {

        //couldn't this be a resource pack?
        public static ForgeConfigSpec.BooleanValue play_block_brewing_stand_brew;

        public Client(ForgeConfigSpec.Builder builder) {
            builder.push("general");
            play_block_brewing_stand_brew = builder.define("play_block_brewing_stand_brew",false);
            builder.pop();
        }
    }

    public static class Server {

        public static int potion_cooldown = 30;
        public static ForgeConfigSpec.BooleanValue return_empty_bottles;
        public static ForgeConfigSpec.BooleanValue milkification;
        public static ForgeConfigSpec.BooleanValue magic_protection;

        public Server(ForgeConfigSpec.Builder builder) {
            builder.push("general");
            return_empty_bottles = builder.define("return_empty_bottles",true);
            milkification = builder.define("milkification",false);
            magic_protection = builder.define("magic_protection",false);
            builder.pop();
        }
    }
}
