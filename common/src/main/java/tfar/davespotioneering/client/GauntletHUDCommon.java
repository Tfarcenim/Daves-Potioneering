package tfar.davespotioneering.client;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import tfar.davespotioneering.DavesPotioneering;

public class GauntletHUDCommon {


    public static final ResourceLocation GAUNTLET_ICON_LOC = new ResourceLocation(DavesPotioneering.MODID, "textures/gauntlet_icons/");
    public static final Minecraft mc = Minecraft.getInstance();
    static final int TEX_HEIGHT = 41;
    static final int TEX_WIDTH = 121;
    static final ResourceLocation hud = getGauntletIconLoc("hud");
    static final int maxCooldown = 40;
    public static int[] cooldowns = new int[6];
    static int cooldown = maxCooldown;
    public static int x;
    public static int y;
    public static HudPreset preset;
    static boolean forwardCycle = false;

    public static ResourceLocation getGauntletIconLoc(String fileName) {
        return new ResourceLocation(GAUNTLET_ICON_LOC.getNamespace(), GAUNTLET_ICON_LOC.getPath() + fileName + ".png");
    }
}
