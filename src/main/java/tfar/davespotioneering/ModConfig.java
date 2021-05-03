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
        public Server(ForgeConfigSpec.Builder builder) {

        }
    }
}
