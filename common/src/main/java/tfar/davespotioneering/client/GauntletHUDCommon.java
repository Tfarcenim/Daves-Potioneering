package tfar.davespotioneering.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.alchemy.Potion;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.platform.Services;

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
    static Potion activePotion = null;
    static Potion prePotion = null;
    static Potion postPotion = null;
    static boolean backwardCycle = false;

    public static ResourceLocation getGauntletIconLoc(String fileName) {
        return new ResourceLocation(GAUNTLET_ICON_LOC.getNamespace(), GAUNTLET_ICON_LOC.getPath() + fileName + ".png");
    }

    public static void init(Potion activePotion, Potion prePotion, Potion postPotion) {
        GauntletHUDCommon.activePotion = activePotion;
        GauntletHUDCommon.prePotion = prePotion;
        GauntletHUDCommon.postPotion = postPotion;
    }


    static int getScaledCooldown(float pixels, float cooldown) {
        float totalCooldown = Services.PLATFORM.gauntletCooldown();

        if (totalCooldown != 0) {
            float result = cooldown * pixels / totalCooldown;
            return Math.round(result);
        }

        return 0;
    }

    static void renderPotion(Potion potion, GuiGraphics matrixStack, int x, int y, int cooldown) {
        if (potion == null) return;
        if (potion.getEffects().isEmpty()) return;

        RenderSystem.setShaderColor(1, 1, 1, 1);

        if (potion.getEffects().size() > 1) {

            String name = BuiltInRegistries.POTION.getKey(potion).toString();
            ResourceLocation resourceLocation;
            if (name.contains("turtle_master")) {
                resourceLocation = getGauntletIconLoc("turtle_master");
            } else if (mc.getResourceManager().getResource(getGauntletIconLoc(name)).isPresent()) {
                resourceLocation = getGauntletIconLoc(name);
            } else {
                resourceLocation = getGauntletIconLoc("unknown");
            }
            matrixStack.blit(resourceLocation, x, y, 0, 0, 0, 18, 18, 18, 18);
        } else {
            MobEffect effect = potion.getEffects().get(0).getEffect();
            TextureAtlasSprite sprite = mc.getMobEffectTextures().get(effect);
            matrixStack.blit(x, y, 0, 18, 18, sprite);
        }


        // render cooldown
        if (cooldown > 0) {

            if (Services.PLATFORM.isDevelopmentEnvironment())
                matrixStack.drawString(mc.font, String.valueOf(cooldown), x, y - 20, 0xff0000);

            int w = 18;
            int scale = getScaledCooldown(w, cooldown);
            matrixStack.fill(x, y + w - scale, x + 18, y + w, 0x7fffffff);
        }
    }

    public static void forwardCycle() {
        forwardCycle = true;
    }

    public static void backwardCycle() {
        backwardCycle = true;
    }
}
